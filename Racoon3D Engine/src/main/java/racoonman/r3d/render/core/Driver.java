package racoonman.r3d.render.core;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.Work;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.ITexture;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.types.BufferUsage;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.ImageLayout;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.api.types.Property;
import racoonman.r3d.render.api.types.ViewType;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.debug.IDebugLogger;
import racoonman.r3d.render.debug.IssueOnlyDebugLogger;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.util.LazyValue;
import racoonman.r3d.window.IWindow;

public class Driver {
	private static final Map<String, IRenderAPI> APIS = new HashMap<>(); static {
		registerApi(IRenderAPI.VULKAN);
	}
	
	private static final LazyValue<Service> SERVICE = new LazyValue<>(APIS.get(Config.RENDER_API)::initService);
	private static IDebugLogger logger = new IssueOnlyDebugLogger();
	
	//FIXME not thread safe, like, at all
	public static Context begin(int queueIndex, Work type) {
		return SERVICE.get().createContext(queueIndex, type);
	}
	
	public static IShaderProgram loadShader(String path) {
		return SERVICE.get().loadShader(path);
	}
	
	public static ITexture loadTexture(String path) {
		return SERVICE.get().loadTexture(path);
	}
	
	public static IDeviceBuffer allocate(long size, BufferUsage[] usage, Property[] properties) {
		return SERVICE.get().allocate(size, usage, properties);
	}
	
	public static IWindowSurface createSurface(IWindow window) {
		return SERVICE.get().createSurface(window);
	}
	
	public static IShader createShader(ShaderStage stage, String entry, String file, String src, String... args) {
		return SERVICE.get().createShader(stage, entry, file, src, args);
	}
	
	public static IShaderProgram createProgram(IShader... shaders) {
		return SERVICE.get().createProgram(shaders);
	}

	public static IFramebuffer createFramebuffer(int width, int height) {
		return SERVICE.get().createFramebuffer(width, height);
	}
	
	public static IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		return SERVICE.get().createAttachment(width, height, layers, layout, format, viewType, usage);
	}

	public static IContextSync createContextSync() {
		return SERVICE.get().createContextSync();
	}
	
	public static IDeviceBuffer map(long size) {
		return SERVICE.get().getMappedMemory().allocate(size);
	}
	
	public static void free(NativeResource resource) {
		SERVICE.get().free(resource);
	}
	
	public static void poll() {
		SERVICE.get().poll();
	}

	public static IMemoryCopier getMemoryCopier() {
		return SERVICE.get();
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
			SERVICE.get().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
