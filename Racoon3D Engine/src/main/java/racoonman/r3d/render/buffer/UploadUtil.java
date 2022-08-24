package racoonman.r3d.render.buffer;

import java.nio.ByteBuffer;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Property;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.memory.Allocation;
import racoonman.r3d.render.memory.IMemoryCopier;

class UploadUtil {

	public static void upload(IMemoryCopier uploader, ByteBuffer buffer, IDeviceBuffer target) {
		if(target.isHostVisible()) {
			target.asByteBuffer().put(buffer);
		} else {
			IDeviceBuffer region = Allocation.ofSize(buffer.limit())
				.withProperties(Property.HOST_COHERENT, Property.HOST_VISIBLE)
				.withUsage(BufferUsage.TRANSFER_SRC)
				.allocate();
			region.asByteBuffer().put(buffer);
			uploader.copy(region, target);
			Driver.free(region);
		}
	}
}