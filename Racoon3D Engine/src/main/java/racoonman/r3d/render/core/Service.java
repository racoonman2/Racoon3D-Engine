package racoonman.r3d.render.core;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.core.libraries.Libraries;
import racoonman.r3d.render.Context;
import racoonman.r3d.render.Work;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IMappedMemory;
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
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.resource.ShaderLoader;
import racoonman.r3d.render.resource.TextureLoader;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.window.IWindow;

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
	
	public abstract Context createContext(int queueIndex, Work type);

	public abstract IShader createShader(ShaderStage stage, String entry, String file, String src, String... args);

	public abstract IShaderProgram createProgram(IShader... shaders);

	public abstract IDeviceBuffer allocate(long size, BufferUsage[] usages, Property[] properties);

	public abstract IWindowSurface createSurface(IWindow window);
	
	public abstract IFramebuffer createFramebuffer(int width, int height);

	public abstract IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage);

	protected abstract IContextSync createContextSync();

	public abstract IMappedMemory getMappedMemory();

	public abstract void free(NativeResource resource);

	protected abstract void poll();

	static {
		Libraries.init();
	}
}
