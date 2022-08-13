package racoonman.r3d.render.api.vulkan;

import java.nio.ByteBuffer;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.TextureState;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.natives.IHandle;

public interface ITexture extends IHandle {
	int getWidth();
	
	int getHeight();
	
	Format getFormat();

	TextureState getState();
	
	void free();

	default ITexture copy() {
		throw new UnsupportedOperationException();
	}
	
	static IDeviceBuffer makeStageBuffer(ByteBuffer data) {
		IDeviceBuffer buf = RenderSystem.allocate(data.limit(), BufferUsage.TRANSFER_SRC);
		buf.map();
		buf.asByteBuffer().put(data);
		buf.unmap();
		return buf;
	}
	
	static IDeviceBuffer makeStageBuffer(int width, int height, int channels, ByteBuffer data) {
		IDeviceBuffer buf = RenderSystem.allocate(width * height * channels, BufferUsage.TRANSFER_SRC);
		buf.map();
		buf.asByteBuffer().put(data);
		buf.unmap();
		return buf;
	}
	
	static IDeviceBuffer makeStageBuffer(int width, int height, int channels) {
		return RenderSystem.allocate(width * height * channels, BufferUsage.TRANSFER_SRC);
	}
}
