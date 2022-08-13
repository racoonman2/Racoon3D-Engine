package racoonman.r3d.render.buffer;

import java.nio.ByteBuffer;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.core.RenderSystem;

class UploadUtil {

	public static IDeviceBuffer upload(ByteBuffer buffer, BufferUsage... usage) {
		ByteBuffer bytes = buffer.rewind();
		IDeviceBuffer gpuBuffer = RenderSystem.allocate(bytes.limit(), usage);

		IDeviceBuffer region = RenderSystem.allocate(bytes.limit(), BufferUsage.TRANSFER_SRC);
		region.asByteBuffer().put(bytes);
		gpuBuffer.copy(region);
		
		return gpuBuffer;
	}
}
