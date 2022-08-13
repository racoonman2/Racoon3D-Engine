package racoonman.r3d.render.core;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.core.libraries.Libraries;
import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IMappedMemoryRegion;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.vulkan.ITexture;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.resource.ShaderLoader;
import racoonman.r3d.render.resource.TextureLoader;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.window.Window;

public abstract class RenderService implements AutoCloseable {
	protected ShaderLoader shaderLoader;
	protected TextureLoader textureLoader;
	protected long beginCount;

	public RenderService(IShaderProcessor shaderProcessor) {
		this.shaderLoader = new ShaderLoader(this, shaderProcessor);
		this.textureLoader = new TextureLoader(this);
	}

	public IShaderProgram loadShader(String path) {
		return this.shaderLoader.load(path);
	}

	public ITexture loadTexture(String path) {
		return this.textureLoader.load(path);
	}

	public RenderContext begin() {
		this.poll(); //TODO move to submit instead
		return this.createContext();
	}
	
	public abstract IShader createShader(ShaderStage stage, String entry, String file, String... args);

	public abstract IShaderProgram createProgram(IShader... shaders);

	public abstract void free(NativeResource resource);

	public abstract IDeviceBuffer allocate(long size, BufferUsage... usages);

	public abstract IFramebuffer createFramebuffer(Window window);

	public abstract IFramebuffer createFramebuffer(int width, int height, int frameCount);

	public abstract IAttachment createAttachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage);

	public abstract void copy(IDeviceBuffer src, IDeviceBuffer dst);
	
	public abstract IMappedMemoryRegion getMappedMemoryRegion();

	protected abstract void pollInternal();
	
	protected abstract RenderContext createContext();
	
	protected final void poll() {
		this.pollInternal();

		this.beginCount++;
	}

	public long getBeginCount() {
		return this.beginCount;
	}

	static {
		Libraries.init();
	}
}
