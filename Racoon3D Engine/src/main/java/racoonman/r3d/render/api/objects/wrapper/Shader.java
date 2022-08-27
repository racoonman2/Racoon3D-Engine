package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.shader.ShaderStage;

public class Shader implements IShader {
	private IShader delegate;
	
	public Shader(ShaderStage stage, String entry, String file, String src, String... args) {
		this.delegate = Driver.createShader(stage, entry, file, src, args);
	}
	
	@Override
	public long asLong() {
		return this.delegate.asLong();
	}

	@Override
	public void free() {
		this.delegate.free();
	}

	@Override
	public ShaderStage stage() {
		return this.delegate.stage();
	}

	@Override
	public String name() {
		return this.delegate.name();
	}
}
