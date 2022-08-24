package racoonman.r3d.render.buffer;

import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IBindable;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.Property;
import racoonman.r3d.render.memory.Allocation;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.vertex.RenderBufferData;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.ArrayUtil;
import racoonman.r3d.util.IPair;

public interface IRenderBuffer extends IBindable {
	void draw(Context context, int instanceCount);

	//TODO void drawRange(RenderContext context, int instanceCount, int start, int amount);

	void update(IMemoryCopier uploader, List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer);

	void free();

	default IRenderBuffer withBuffer(VertexFormat format, long size, Type type) {
		return this.withBuffer(format, Allocation.ofSize(size)
			.withProperties(type.getProperties())
			.withUsage(ArrayUtil.add(type.getUsage(), BufferUsage.VERTEX_BUFFER))
			.allocate());
	}
	
	IRenderBuffer withBuffer(VertexFormat format, IDeviceBuffer buffer);
	
	List<IPair<VertexFormat, IDeviceBuffer>> getVertexBuffers();
	
	default Optional<IDeviceBuffer> getIndexBuffer() {
		return Optional.empty();
	}
	
	public static IRenderBuffer withSize() {
		return new RenderBufferImpl();
	}
	
	public static IRenderBuffer withSize(long indexSize, Type type) {
		return new IndexedRenderBuffer(Allocation.ofSize(indexSize)
			.withProperties(type.getProperties())
			.withUsage(ArrayUtil.add(type.getUsage(), BufferUsage.INDEX_BUFFER))
			.allocate());
	}
	
	public static enum Type {
		STREAM(new BufferUsage[] {}, new Property[] { Property.HOST_VISIBLE, Property.DEVICE_LOCAL }),
		STATIC(new BufferUsage[] { BufferUsage.TRANSFER_DST }, new Property[] { Property.DEVICE_LOCAL });
		
		private BufferUsage[] usage;
		private Property[] properties;
		
		private Type(BufferUsage[] usage, Property[] properties) {
			this.usage = usage;
			this.properties = properties;
		}
		
		public BufferUsage[] getUsage() {
			return this.usage;
		}
		
		public Property[] getProperties() {
			return this.properties;
		}
	}
}
