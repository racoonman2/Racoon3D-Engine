package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.state.IState;

public class ShaderProgram implements IShaderProgram {
	private IShaderProgram delegate;
	
	public ShaderProgram(IShader... shaders) {
		this.delegate = Driver.createProgram(shaders);
	}
	
	@Override
	public void bind(IState state) {
		this.delegate.bind(state);
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
	public IShader[] getShaders() {
		return this.delegate.getShaders();
	}
}
