package racoonman.r3d.util;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;

public class NativeImage implements NativeResource {
	private ByteBuffer data;
	private int width;
	private int height;
	private int channels;

	public NativeImage(int width, int height, int channels) {
		this(memAlloc(width * height * channels), width, height, channels);
	}

	public NativeImage(ByteBuffer data, int width, int height, int channels) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.channels = channels;
	}

	public ByteBuffer getData() {
		return this.data;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getChannels() {
		return this.channels;
	}

	public int getRed(int x, int y) {
		return this.getAtIndex(x, y, 0);
	}

	public int getGreen(int x, int y) {
		return this.getAtIndex(x, y, 1);
	}

	public int getBlue(int x, int y) {
		return this.getAtIndex(x, y, 2);
	}

	public int getAlpha(int x, int y) {
		return this.getAtIndex(x, y, 3);
	}

	public int getAtIndex(int x, int y, int channel) {
		return Byte.toUnsignedInt(this.data.get(x * this.channels + channel + y * this.channels * this.width));
	}

	public void setRed(int value, int x, int y) {
		this.setAtIndex(value, x, y, 0);
	}

	public void setGreen(int value, int x, int y) {
		this.setAtIndex(value, x, y, 1);
	}

	public void setBlue(int value, int x, int y) {
		this.setAtIndex(value, x, y, 2);
	}

	public void setAlpha(int value, int x, int y) {
		this.setAtIndex(value, x, y, 3);
	}

	public void setAtIndex(int value, int x, int y, int channel) {
		this.data.put(x * this.channels + channel + y * this.channels * this.width, (byte) value);
	}

	@Override
	public void free() {
		memFree(this.data);
	}

	public static NativeImage load(InputStream in) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			byte[] imageBytes = in.readAllBytes();

			ByteBuffer imageBuf = memAlloc(imageBytes.length);
			imageBuf.put(imageBytes);
			imageBuf.flip();

			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer c = stack.mallocInt(1);

			ByteBuffer imgData = STBImage.stbi_load_from_memory(imageBuf, w, h, c, 4);

			if (imgData == null)
				throw new IOException("Unable to load image, reason [" + STBImage.stbi_failure_reason() + "]");

			memFree(imageBuf);
			return new NativeImage(imgData, w.get(), h.get(), c.get());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
