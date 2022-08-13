package racoonman.r3d.render.api.objects;

//Note: this is not the same as glfw's window surface
public interface IWindowSurface {
	boolean acquire();
	
	boolean present();
	
	boolean isValid();
	
	IFramebuffer getFramebuffer();
}
