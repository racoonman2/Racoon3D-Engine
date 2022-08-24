package racoonman.r3d.render.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.WorkType;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.ITexture;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.Property;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.debug.IDebugLogger;
import racoonman.r3d.render.debug.IssueOnlyDebugLogger;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.resource.Decoders;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.format.IEncodingFormat;
import racoonman.r3d.resource.io.ClassPathReader;
import racoonman.r3d.window.api.glfw.Window;

public class Driver {
	private static final Map<String, IRenderAPI> APIS = new HashMap<>(); static {
		registerApi(IRenderAPI.VULKAN);
	}
	
	private static IDebugLogger logger = new IssueOnlyDebugLogger();
	private static Service service;
	
	//FIXME not thread safe, like, at all
	private static Context begin(WorkType type) {
		return getService().createContext(type);
	}
	
	public static Context graphics() {
		return begin(WorkType.GRAPHICS);
	}
	
	public static Context compute() {
		return begin(WorkType.COMPUTE);
	}
	
	public static Context transfer() {
		return begin(WorkType.TRANSFER);
	}
	
	public static void graphics(Consumer<Context> consumer) {
		try(Context ctx = graphics()) {
			consumer.accept(ctx);
		}
	}
	
	public static void compute(Consumer<Context> consumer) {
		try(Context ctx = compute()) {
			consumer.accept(ctx);
		}
	}
	
	public static void transfer(Consumer<Context> consumer) {
		try(Context ctx = transfer()) {
			consumer.accept(ctx);
		}
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
	
	public static IDeviceBuffer allocate(long size, BufferUsage[] usage, Property[] properties) {
		return getService().allocate(size, usage, properties);
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

	public static IFramebuffer createFramebuffer(int width, int height) {
		return getService().createFramebuffer(width, height);
	}
	
	public static IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		return getService().createAttachment(width, height, layers, layout, format, viewType, usage);
	}
	
	public static IDeviceBuffer map(long size) {
		return getService().getMappedMemory().allocate(size);
	}
	
	public static void free(NativeResource resource) {
		getService().free(resource);
	}
	
	public static void poll() {
		getService().poll();
	}

	public static IMemoryCopier getMemoryCopier() {
		return getService();
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
	
	public static void close() {
		try {
			getService().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Service getService() {
		return service == null ? service = APIS.get(Config.RENDER_API).initService() : service;
	}
}
