package racoonman.r3d.render.core;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.Scissor;
import racoonman.r3d.render.Viewport;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.vulkan.ITexture;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.debug.IDebugLogger;
import racoonman.r3d.render.debug.IssueOnlyDebugLogger;
import racoonman.r3d.render.resource.Decoders;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.format.IEncodingFormat;
import racoonman.r3d.resource.io.ClassPathReader;
import racoonman.r3d.window.Window;

//TODO maybe push/pop system for render contexts to replace begin()
public class RenderSystem {
	private static final Map<String, IRenderAPI> APIS = new HashMap<>(); static {
		registerApi(IRenderAPI.OPENGL);
		registerApi(IRenderAPI.VULKAN);
	}
	
	private static IDebugLogger logger = new IssueOnlyDebugLogger();
	private static RenderService service;

	private static RenderService initService() {
		if(service == null) {
			return service = APIS.get(R3DRuntime.getStringOr("r3d.renderApi", IRenderAPI.OPENGL.getName())).initService();			
		}
		return service;
	}
	
	public static RenderContext begin(IFramebuffer target) {
		RenderContext ctx = begin();
		target.next();
		target.bind(ctx);
		ctx.viewport(Viewport.of(target, 0.0F, 1.0F));
		ctx.scissor(Scissor.of(target));
		return ctx;
	}
	
	public static RenderContext begin() {
		return getService().begin();
	}
	
	public static IShaderProgram loadShader(String path) {
		return getService().loadShader(path);
	}
	
	public static ITexture loadTexture(String path) {
		return getService().loadTexture(path);
	}
	
	public static IFramebuffer loadFramebuffer(String path, Object... args) {
		return ClassPathReader.decode(Decoders.forFramebuffer(getService(), args), IEncodingFormat.JSON, path);
	}
	
	public static IDeviceBuffer allocate(long size, BufferUsage... usages) {
		return getService().allocate(size, usages);
	}
	
	public static IWindowSurface createSurface(Window window) {
		return getService().createSurface(window);
	}
	
	public static IShader createShader(ShaderStage stage, String entry, String file, String... args) {
		return getService().createShader(stage, entry, file, args);
	}
	
	public static IShaderProgram createProgram(IShader... shaders) {
		return getService().createProgram(shaders);
	}

	public static IFramebuffer createFramebuffer(int width, int height, int frameCount) {
		return getService().createFramebuffer(width, height, frameCount);
	}
	
	public static IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		return getService().createAttachment(width, height, layers, layout, format, viewType, usage);
	}
	
	public static void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		getService().copy(src, dst);
	}
	
	public static IDeviceBuffer map(long size) {
		return getService().getMappedMemoryRegion().allocate(size);
	}
	
	public static void free(NativeResource resource) {
		getService().free(resource);
	}
	
	public static void poll() {
		getService().poll();
	}
	
	public static void close() {
		try {
			getService().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static RenderService getService() {
		if(service == null)
			initService();
		return service;
	}
	
	public static IDebugLogger getDebugLogger() {
		return logger;
	}
	
	public static void setDebugLogger(IDebugLogger newlogger) {
		logger = newlogger;
	}
	
	public static IRenderAPI getApi(String name) {
		return APIS.get(name);
	}
	
	public static void registerApi(IRenderAPI api) { 
		APIS.put(api.getName(), api);
	}
}
