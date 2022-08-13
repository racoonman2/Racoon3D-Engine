package racoonman.r3d.core.libraries;

import org.lwjgl.system.NativeResource;

public class NativeLibrary implements NativeResource {
	private Runnable deallocator;
	
	public NativeLibrary(Runnable loader, Runnable deallocater) {
		this.deallocator = deallocater;
		
		loader.run();
	}

	@Override
	public void free() {
		this.deallocator.run();
	}
}
