package racoonman.r3d.util;

import static org.lwjgl.system.MemoryUtil.memAlloc;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Util {
	public static final long NANOS_PER_SECOND = 1000000000L;
	
	public static ByteBuffer toBuffer(String s, MemoryStack stack) {
		return stack.bytes(s.getBytes());
	}

	public static ByteBuffer toBuffer(String s) {
		byte[] bytes = s.getBytes();
		return MemoryUtil.memAlloc(bytes.length).put(0, bytes);
	}

	public static ByteBuffer merge(ByteBuffer... buffers) {
		int size = 0;

		for (ByteBuffer buffer : buffers) {
			size += buffer.capacity();
		}

		ByteBuffer container = memAlloc(size);

		for (ByteBuffer buffer : buffers) {
			container.put(buffer);
		}
		
		container.flip();

		return container;
	}

	public static int nextWhitespace(int start, String s) {
		for (int i = start; i < s.length(); i++) {
			if (Character.isWhitespace(s.charAt(i))) {
				return i;
			}
		}

		return -1;
	}
}
