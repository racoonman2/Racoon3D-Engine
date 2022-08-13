package racoonman.r3d.render.api.vulkan;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IMappedMemoryRegion;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.QueueFamily;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.core.IRenderPlatform;
import racoonman.r3d.render.core.RenderService;
import racoonman.r3d.render.shader.ShaderCompiler.Result;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.window.Window;

// use gl_(Matrix) naming for built in matrix uniforms (almost done)
// Also BuiltInUniform class or something

// TODO use vulkan pipeline cache
class VkRenderService extends RenderService {
	private Vulkan vulkan;
	private PhysicalDevice physicalDevice;
	private Device device;
	private WorkDispatcher graphicsDispatch;
	private VkMappedRegion mappedRegion;
	private Queue<NativeResource> freeQueue;
	private FrameManager frameManager;
	
	public VkRenderService(IRenderPlatform platform, boolean validate) {
		super(new VkShaderProcessor());
		this.vulkan = new Vulkan(platform.getApiVersion(), platform.getEngineVersion(), platform.getEngineName(), platform.getAppName(), validate);
		this.physicalDevice = PhysicalDevice.findPhysicalDevice(this.vulkan);
		this.device = new Device(this.vulkan, this.physicalDevice, IDeviceExtension.DYNAMIC_RENDERING, IDeviceExtension.KHR_SWAPCHAIN, IDeviceExtension.MULTI_DRAW, IDeviceExtension.PUSH_DESCRIPTOR);
		this.graphicsDispatch = new WorkDispatcher(this.device, QueueFamily.GRAPHICS, 0);
		this.mappedRegion = new VkMappedRegion(this, Config.initialMappedRegionSize);
		this.freeQueue = new ConcurrentLinkedQueue<>();
		this.frameManager = new FrameManager(this.graphicsDispatch, this.device);
	}

	@Override
	public RenderContext createContext() {
		return new VkRenderContext(this.graphicsDispatch, this.frameManager.take());
	}

	@Override
	public IShader createShader(ShaderStage stage, String entry, String file, String... args) {
		Result result = this.shaderLoader.compileShader(file, entry, stage, args);
		IShader shader = new VkShader(this.device, stage, file, result.getData());
		result.free();
		return shader;
	}
	
	@Override
	public IShaderProgram createProgram(IShader... shader) {
		return new VkShaderProgram(shader);
	}

	@Override
	public IDeviceBuffer allocate(long size, BufferUsage... usages) {
		return new VkDeviceBuffer(this.device, size, usages);
	}

	@Override
	public IFramebuffer createFramebuffer(Window window) {
		return new VkWindowFramebuffer(this.device, this.vulkan, window, 0);
	}

	@Override
	public IFramebuffer createFramebuffer(int width, int height, int frameCount) {
		return new VkOffscreenFramebuffer(this.device, width, height, frameCount);
	}

	@Override
	public IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		return new VkAttachment(width, height, layers, format, viewType, usage, this.device);
	}

	@Override
	public void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public IMappedMemoryRegion getMappedMemoryRegion() {
		return this.mappedRegion;
	}
	
	@Override
	public void pollInternal() {
		if(!this.freeQueue.isEmpty()) {
			this.graphicsDispatch.join();
			
			while(!this.freeQueue.isEmpty()) {
				this.freeQueue.poll().free();
			}
		}
		
		this.frameManager.poll();
	}

	@Override
	public void free(NativeResource resource) {
		this.freeQueue.add(resource);
	}

	@Override
	public void close() {
		
	}
}
