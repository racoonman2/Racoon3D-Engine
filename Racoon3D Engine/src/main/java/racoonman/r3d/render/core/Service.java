package racoonman.r3d.render.core;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.core.libraries.Libraries;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.objects.IFramebuffer;
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
import racoonman.r3d.render.resource.ShaderLoader;
import racoonman.r3d.render.resource.TextureLoader;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.window.IWindow;

public abstract class Service implements AutoCloseable {
	private ShaderLoader shaderLoader;
	private TextureLoader textureLoader;

	public Service(IShaderProcessor shaderProcessor) {
		this.shaderLoader = new ShaderLoader(shaderProcessor);
		this.textureLoader = new TextureLoader(this);
	}
	
	public ShaderLoader getShaderLoader() {
		return this.shaderLoader;
	}
	
	public TextureLoader getTextureLoader()	{
		return this.textureLoader;
	}

	protected abstract IShader createShader(ShaderStage stage, String entry, String file, String src, String... args);

	protected abstract IShaderProgram createProgram(IShader... shaders);

	public abstract IDeviceBuffer allocate(long size, BufferUsage[] usages, Property[] properties); //TODO this should be protected

	protected abstract IWindowSurface createSurface(IWindow window);
	
	protected abstract IFramebuffer createFramebuffer(int width, int height);

	protected abstract IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage);

	protected abstract IHostSync createHostSync(boolean signaled);
	
	protected abstract IDeviceSync createDeviceSync();

	protected abstract IMappedMemory getMappedMemory();

	protected abstract IWorkPool createPool(int index, Work... flags);

	protected abstract void free(NativeResource resource);

	protected abstract void poll();

	static {
		Libraries.init();
	}
}
