package racoonman.r3d.render.api.objects;

// Opengl: display list
// vulkan: pre-recorded command buffer
public interface ICommandList {
	void execute();
}
