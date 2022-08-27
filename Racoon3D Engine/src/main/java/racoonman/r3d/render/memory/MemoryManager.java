package racoonman.r3d.render.memory;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.types.BufferUsage;

public class MemoryManager {

	public void allocate(long size, BufferUsage[] usage, BufferHint... hints) {
		
	}
	
	public void promote(IDeviceBuffer buffer) {
		
	}
	
	public void demote() {
		
	}
	
	public static enum BufferHint {
		DEVICE_GUARANTEED;
	}
}
