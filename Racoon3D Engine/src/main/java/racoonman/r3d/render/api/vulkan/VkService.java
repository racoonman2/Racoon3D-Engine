package racoonman.r3d.render.api.vulkan;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.objects.IMappedMemory;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.objects.IWorkPool;
import racoonman.r3d.render.api.types.BufferUsage;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.ImageLayout;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.api.types.Property;
import racoonman.r3d.render.api.types.ViewType;
import racoonman.r3d.render.api.types.Work;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.core.IRenderPlatform;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.memory.Allocation;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.state.uniform.UniformBuffer;
import racoonman.r3d.util.Bytes;
import racoonman.r3d.window.IWindow;

// TODO use vulkan pipeline cache
class VkService extends Service {
	private Vulkan vulkan;
	private PhysicalDevice physicalDevice;
	private Device device;
	private VkMappedRegion mappedMemory;
	private FrameManager frameManager;
	private Queue<NativeResource> freeQueue;
	private GraphicsCache graphicsCache;
	private ComputeCache computeCache;
	private UniformBuffer uniformBuffer;
	
	public VkService(IRenderPlatform platform, boolean validate) {
		super(new VkShaderProcessor());
		this.vulkan = new Vulkan(platform.getApiVersion(), platform.getEngineVersion(), platform.getEngineName(), platform.getAppName(), validate);
		this.physicalDevice = PhysicalDevice.findPhysicalDevice(this.vulkan);
		this.device = new Device(this.vulkan, this.physicalDevice, IDeviceExtension.DYNAMIC_RENDERING, IDeviceExtension.KHR_SWAPCHAIN, IDeviceExtension.MULTI_DRAW, IDeviceExtension.PUSH_DESCRIPTOR);
		this.mappedMemory = new VkMappedRegion(this, Config.MAPPED_REGION_SIZE);
		this.frameManager = new FrameManager(this.device);
		this.freeQueue = new ConcurrentLinkedQueue<>();
		this.graphicsCache = new GraphicsCache(this.device);
		this.computeCache = new ComputeCache(this.device);
		this.uniformBuffer = new UniformBuffer(Allocation.ofSize(Bytes.mb(3))
			.withProperties(Property.DEVICE_LOCAL, Property.HOST_VISIBLE)
			.withUsage(BufferUsage.UNIFORM_BUFFER)
			.allocate(this));
	}

	@Override
	public IShader createShader(ShaderStage stage, String entry, String file, String src, String... args) {
		return new VkShader(this.device, stage, entry, this.getShaderLoader().createShader(stage, entry, file, src, args).getData());
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
		return new VkFramebufferImpl(this.device, width, height, Config.FRAME_COUNT);
	}

	@Override
	public IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		return new VkAttachment(width, height, layers, format, viewType, usage, this.device);
	}

	@Override
	protected IHostSync createHostSync(boolean signaled) {
		return new VkHostSync(this.device, signaled);
	}
	
	@Override
	protected IDeviceSync createDeviceSync() {
		return new VkDeviceSync(this.device);
	}

	@Override
	public IMappedMemory getMappedMemory() {
		return this.mappedMemory;
	}
	
	@Override
	public void poll() {
		this.frameManager.poll();
		
		if(!this.freeQueue.isEmpty()) {
			this.device.waitIdle(); //TODO remove
			
			while(!this.freeQueue.isEmpty()) {
				this.freeQueue.poll().free();
			}
		}
	}

	@Override
	public IWorkPool createPool(int index, Work... flags) {
		return new VkWorkPool(this.device, this, index, flags);
	}
	
	Context createContext(VkWorkPool pool) {
		return new VkContext(this.graphicsCache, this.computeCache, pool, this.frameManager.dispatch(pool), uniformBuffer);
	}

	@Override
	public void free(NativeResource resource) {
		this.freeQueue.add(resource);
	}
	
	@Override
	public void close() {
		//TODO
	}
}
