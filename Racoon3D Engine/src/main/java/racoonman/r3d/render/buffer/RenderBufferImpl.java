package racoonman.r3d.render.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.memory.IMemoryCopier;
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
	public void bind(Context context) {
		context.bindVertexBuffers(this.vertexBuffers);
	}
	
	@Override
	public void draw(Context context, int instanceCount) {
		context.draw(instanceCount, 0, this.vertexCount);
	}

	@Override
	public void update(IMemoryCopier uploader, List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer) {
		int vertexCount = 0;
		
		for(int i = 0; i < vertexBuffers.size(); i++) {
			IPair<VertexFormat, RenderBufferData> vertexBuffer = vertexBuffers.get(i);
			
			RenderBufferData data = vertexBuffer.right();
			int elements = data.elements();
			
			if(vertexCount == 0) {
				vertexCount = elements;
			} else {
				vertexCount = Math.min(vertexCount, elements);
			}
						
			UploadUtil.upload(uploader, vertexBuffer.right().data().rewind(), this.vertexBuffers.get(i).right());
		}
		
		this.vertexCount = vertexCount;
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

	@Override
	public IRenderBuffer withBuffer(VertexFormat format, IDeviceBuffer buffer) {
		this.vertexBuffers.add(IPair.of(format, buffer));
		return this;
	}
}
