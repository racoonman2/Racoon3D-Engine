package racoonman.r3d.render.api.vulkan;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.Work;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IMappedMemory;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.types.BufferUsage;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.ImageLayout;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.api.types.Property;
import racoonman.r3d.render.api.types.QueueFamily;
import racoonman.r3d.render.api.types.ViewType;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.core.IRenderPlatform;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.memory.Allocation;
import racoonman.r3d.render.shader.ShaderCompiler.Result;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.state.uniform.UniformBuffer;
import racoonman.r3d.util.Bytes;
import racoonman.r3d.window.IWindow;

// TODO use vulkan pipeline cache
class VkService extends Service {
	private Vulkan vulkan;
	private PhysicalDevice physicalDevice;
	private Device device;
	private WorkPool graphicsPool;
	private WorkPool computePool;
	private WorkPool transferPool;
	private VkMappedRegion mappedMemory;
	private FrameManager frameManager;
	private Queue<NativeResource> freeQueue;
	private RenderCache renderCache;
	private UniformBuffer uniformBuffer;
	
	public VkService(IRenderPlatform platform, boolean validate) {
		super(new VkShaderProcessor());
		this.vulkan = new Vulkan(platform.getApiVersion(), platform.getEngineVersion(), platform.getEngineName(), platform.getAppName(), validate);
		this.physicalDevice = PhysicalDevice.findPhysicalDevice(this.vulkan);
		this.device = new Device(this.vulkan, this.physicalDevice, IDeviceExtension.DYNAMIC_RENDERING, IDeviceExtension.KHR_SWAPCHAIN, IDeviceExtension.MULTI_DRAW, IDeviceExtension.PUSH_DESCRIPTOR);
		this.graphicsPool = new WorkPool(this.device, QueueFamily.GRAPHICS, 0);
		this.computePool = new WorkPool(this.device, QueueFamily.COMPUTE, 0);
		this.transferPool = new WorkPool(this.device, QueueFamily.TRANSFER, 0);
		this.mappedMemory = new VkMappedRegion(this, Config.MAPPED_REGION_SIZE);
		this.frameManager = new FrameManager(this.graphicsPool, this.device);
		this.freeQueue = new ConcurrentLinkedQueue<>();
		this.renderCache = new RenderCache(this.device);
		this.uniformBuffer = new UniformBuffer(Allocation.ofSize(Bytes.mb(3))
			.withProperties(Property.DEVICE_LOCAL, Property.HOST_VISIBLE)
			.withUsage(BufferUsage.UNIFORM_BUFFER)
			.allocate(this));
	}

	//TODO lookup queue based on index and type instead
	@Override
	public Context createContext(int queueIndex, Work type) {
		return new VkContext(this.renderCache, switch(type) {
			case GRAPHICS -> this.graphicsPool;
			case COMPUTE -> this.computePool;
			case TRANSFER -> this.transferPool;
		}, this.frameManager.take(), this.uniformBuffer);
	}

	@Override
	public IShader createShader(ShaderStage stage, String entry, String file, String src, String... args) {
		try(Result result = this.shaderLoader.createShader(stage, entry, file, src, args)) {
			return new VkShader(this.device, stage, entry, result.getData());
		}
	}
	
	@Override
	public IShaderProgram createProgram(IShader... shader) {
		return new VkShaderProgram(shader);
	}

	@Override
	public IDeviceBuffer allocate(long size, BufferUsage[] usage, Property[] properties) {
		return new VkDeviceBuffer(this.device, size, usage, properties);
	}

	@Override
	public IWindowSurface createSurface(IWindow window) {
		return new VkWindowSurface(this.device, window, 0);
	}

	@Override
	public IFramebuffer createFramebuffer(int width, int height) {
		return new VkOffscreenFramebuffer(this.device, width, height, Config.FRAME_COUNT);
	}

	@Override
	public IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		return new VkAttachment(width, height, layers, format, viewType, usage, this.device);
	}

	@Override
	protected IContextSync createContextSync() {
		return new VkSemaphore(this.device);
	}

	@Override
	public void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		this.transferPool.runAsync((cmdBuffer) -> VkUtils.copy(cmdBuffer, src, dst)).join();
	}

	@Override
	public IMappedMemory getMappedMemory() {
		return this.mappedMemory;
	}
	
	@Override
	public void poll() {
		this.frameManager.poll();
		
		if(!this.freeQueue.isEmpty()) {
			//TODO remove
			this.graphicsPool.join();
			this.computePool.join();
			this.transferPool.join();
			
			while(!this.freeQueue.isEmpty()) {
				this.freeQueue.poll().free();
			}
		}
	}

	@Override
	public void free(NativeResource resource) {
		this.freeQueue.add(resource);
	}
	
	@Override
	public void close() {
		
	}
}
