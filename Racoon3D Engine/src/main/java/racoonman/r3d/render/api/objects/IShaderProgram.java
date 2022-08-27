package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.IBindable;
import racoonman.r3d.render.natives.IHandle;

public interface IShaderProgram extends IBindable, IHandle {
	IShader[] getShaders();
}
