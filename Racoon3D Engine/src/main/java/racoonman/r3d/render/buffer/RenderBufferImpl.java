package racoonman.r3d.render.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.vertex.RenderBufferData;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

public class RenderBufferImpl implements IRenderBuffer {
	protected List<IPair<VertexFormat, IDeviceBuffer>> vertexBuffers;
	protected int vertexCount;

	public RenderBufferImpl() {
		this.vertexBuffers = new ArrayList<>();
	}
	
	@Override
	public void bind(RenderContext context) {
		context.bindVertexBuffers(this.vertexBuffers);
	}
	
	@Override
	public void draw(RenderContext context, int instanceCount) {
		context.draw(instanceCount, 0, this.vertexCount);
	}

	@Override
	public void update(List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer) {
		while(!this.vertexBuffers.isEmpty()) {
			RenderSystem.free(this.vertexBuffers.remove(this.vertexBuffers.size() - 1).right());
		}
		
		int vertexCount = 0;
		for(IPair<VertexFormat, RenderBufferData> vertexBuffer : vertexBuffers) {
			RenderBufferData data = vertexBuffer.right();
			int elements = data.elements();
			
			if(vertexCount == 0) {
				vertexCount = elements;
			} else {
				vertexCount = Math.min(vertexCount, elements);
			}
			
			this.vertexBuffers.add(
				IPair.of(
					vertexBuffer.left(), 
					UploadUtil.upload(vertexBuffer.right().data().rewind(), BufferUsage.VERTEX_BUFFER, BufferUsage.TRANSFER_DST)
				)
			);
		}
	}

	@Override
	public IRenderBuffer sub() {
		return null;
	}

	@Override
	public List<IPair<VertexFormat, IDeviceBuffer>> getVertexBuffers() {
		return this.vertexBuffers;
	}
	
	@Override
	public void free() {
		for(IPair<VertexFormat, IDeviceBuffer> vertexBuffer : this.vertexBuffers) {
			vertexBuffer.right().free();
		}
	}
}
