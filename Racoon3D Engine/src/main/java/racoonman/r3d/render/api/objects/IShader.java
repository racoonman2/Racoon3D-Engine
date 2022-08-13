package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.render.shader.ShaderStage;

public interface IShader extends IHandle {
	ShaderStage stage();
	
	String name();
}
