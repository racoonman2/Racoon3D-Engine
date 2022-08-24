package racoonman.r3d.render.core;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.core.libraries.Libraries;
import racoonman.r3d.render.Context;
import racoonman.r3d.render.WorkType;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IMappedMemory;
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
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.resource.ShaderLoader;
import racoonman.r3d.render.resource.TextureLoader;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.window.api.glfw.Window;

public abstract class Service implements AutoCloseable, IMemoryCopier {
	protected ShaderLoader shaderLoader;
	protected TextureLoader textureLoader;

	public Service(IShaderProcessor shaderProcessor) {
		this.shaderLoader = new ShaderLoader(this, shaderProcessor);
		this.textureLoader = new TextureLoader(this);
	}

	public IShaderProgram loadShader(String path) {
		return this.shaderLoader.load(path);
	}

	public ITexture loadTexture(String path) {
		return this.textureLoader.load(path);
	}
	
	public abstract Context createContext(WorkType type);
	
	public abstract IShader createShader(ShaderStage stage, String entry, String file, String... args);

	public abstract IShaderProgram createProgram(IShader... shaders);

	public abstract IDeviceBuffer allocate(long size, BufferUsage[] usages, Property[] properties);

	public abstract IWindowSurface createSurface(Window window);
	
	public abstract IFramebuffer createFramebuffer(int width, int height);

	public abstract IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage);

	public abstract IMappedMemory getMappedMemory();

	public abstract void free(NativeResource resource);

	protected abstract void poll();

	static {
		Libraries.init();
	}
}
