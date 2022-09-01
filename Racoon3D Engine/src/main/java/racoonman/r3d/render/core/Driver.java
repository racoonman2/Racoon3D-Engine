package racoonman.r3d.render.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.system.NativeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.ITexture;
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
import racoonman.r3d.render.debug.IDebugLogger;
import racoonman.r3d.render.debug.IssueOnlyDebugLogger;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.util.ArrayUtil;
import racoonman.r3d.util.IPair;
import racoonman.r3d.util.LazyValue;
import racoonman.r3d.window.IWindow;

public class Driver {
	private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
	private static final Map<String, IRenderAPI> APIS = new HashMap<>(); static {
		registerApi(IRenderAPI.VULKAN);
	}
	
	private static final LazyValue<Service> SERVICE = new LazyValue<>(() -> {
		LOGGER.info("Initializing render service for api [{}]", Config.RENDER_API);
		return APIS.get(Config.RENDER_API).initService();
	});
	
	private static final Map<IPair<Integer, Work[]>, IWorkPool> POOLS = Collections.synchronizedMap(new TreeMap<>((o1, o2) -> {
		return o1 == o2 ? 1 : 
			   o1.left() == o2.left() && ArrayUtil.softEquals(o1.right(), o2.right()) ? 0 
			  : -1;
	}));
	private static IDebugLogger logger = new IssueOnlyDebugLogger();
	
	public static Context record(int index, Work... flags) {
		return getPool(index, flags).dispatch();
	}
	
	public static IWorkPool getPool(int index, Work... flags) {
		return POOLS.computeIfAbsent(IPair.of(index, flags), (k) -> {
			LOGGER.info("Initializing work pool [index:{}, work:{}]", index, Arrays.toString(flags));
			return createPool(index, flags);
		});
	}
	
	public static IShaderProgram loadProgram(String path, String... args) {
		return SERVICE.get().getShaderLoader().loadProgram(path, args);
	}
	
	public static IShader loadShader(ShaderStage stage, String entry, String path, String... args) {
		return SERVICE.get().getShaderLoader().loadShader(stage, entry, path, args);
	}
	
	public static ITexture loadTexture(String path) {
		return SERVICE.get().getTextureLoader().load(path);
	}
	
	public static void flush() {
		SERVICE.get().getShaderLoader().flushResultCache();
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

	public static IHostSync createHostSync(boolean signaled) {
		return SERVICE.get().createHostSync(signaled);
	}
	
	public static IDeviceSync createDeviceSync() {
		return SERVICE.get().createDeviceSync();
	}
	
	public static IDeviceBuffer map(long size) {
		return SERVICE.get().getMappedMemory().allocate(size);
	}
	
	public static IWorkPool createPool(int index, Work... flags) {
		return SERVICE.get().createPool(index, flags);
	}
	
	public static void free(NativeResource resource) {
		SERVICE.get().free(resource);
	}
	
	public static void poll() {
		SERVICE.get().poll();
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
