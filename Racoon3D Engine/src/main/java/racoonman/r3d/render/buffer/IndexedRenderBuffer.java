package racoonman.r3d.render.buffer;

import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.state.IState;
import racoonman.r3d.render.vertex.RenderBufferData;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

public class IndexedRenderBuffer extends RenderBufferImpl {
	private IDeviceBuffer indexBuffer;
	private int indexCount;
	
	public IndexedRenderBuffer() {
	}
	
	public IndexedRenderBuffer(IDeviceBuffer indexBuffer) {
		this.indexBuffer = indexBuffer;
	}
	
	@Override
	public void bind(IState state) {
		super.bind(state);
		
		state.bindIndexBuffer(this.indexBuffer);
	}

	@Override
	public void draw(RenderPass pass, int instanceCount) {
		this.bind(pass);
		
		pass.drawIndexed(instanceCount, 0, 0, this.indexCount);
	}

	@Override
	public void update(IMemoryCopier uploader,List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer) {
		super.update(uploader, vertexBuffers, indexBuffer);

		indexBuffer.ifPresent((buffer) -> {
			this.indexCount = buffer.elements();
			UploadUtil.upload(uploader, buffer.data().rewind(), this.indexBuffer);
		});
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
