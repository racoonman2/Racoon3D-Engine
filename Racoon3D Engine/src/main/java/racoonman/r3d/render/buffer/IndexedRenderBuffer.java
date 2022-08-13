package racoonman.r3d.render.buffer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.vertex.RenderBufferData;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

public class IndexedRenderBuffer extends RenderBufferImpl {
	private IDeviceBuffer indexBuffer;
	private int indexCount;
	
	@Override
	public void draw(RenderContext context, int instanceCount) {
		this.bind(context);
		context.drawIndexed(instanceCount, 0, 0, this.indexCount);
	}

	@Override
	public void bind(RenderContext context) {
		super.bind(context);
		
		context.bindIndexBuffer(this.indexBuffer);
	}

	@Override
	public void update(List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer) {
		super.update(vertexBuffers, indexBuffer);

		indexBuffer.ifPresent((buffer) -> {
			if(this.indexBuffer != null) {
				RenderSystem.free(this.indexBuffer);
			}
			
			this.indexCount = buffer.elements();
			ByteBuffer data = buffer.data();
			
			this.indexBuffer = UploadUtil.upload(data, BufferUsage.INDEX_BUFFER, BufferUsage.TRANSFER_DST);
		});
	}

	@Override
	public IRenderBuffer sub() {
		return null;
	}

	@Override
	public void free() {
		this.indexBuffer.free();
		
		super.free();
	}
	
	@Override
	public List<IPair<VertexFormat, IDeviceBuffer>> getVertexBuffers() {
		return this.vertexBuffers;
	}
	
	@Override
	public Optional<IDeviceBuffer> getIndexBuffer() {
		return Optional.of(this.indexBuffer);
	}
}
