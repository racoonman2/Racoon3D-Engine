package racoonman.r3d.render.natives;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;

public interface IHandle extends NativeResource {
	default int asInt() {
		return (int) this.asLong();
	}
	
	long asLong();
	
	default boolean is(long l) {
		return this.asLong() == l;
	}
	
	default boolean is(int i) {
		return this.asInt() == i;
	}
	
	public static long getSafely(IHandle handle) {
		return handle != null ? handle.asLong() : MemoryUtil.NULL;
	}
}
