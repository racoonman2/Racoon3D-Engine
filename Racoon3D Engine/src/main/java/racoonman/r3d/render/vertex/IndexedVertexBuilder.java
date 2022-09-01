package racoonman.r3d.render.vertex;

import java.nio.ByteBuffer;
import java.util.Optional;

import racoonman.r3d.render.buffer.EmptyRenderBuffer;
import racoonman.r3d.render.buffer.IRenderBuffer;
import racoonman.r3d.render.buffer.IRenderBuffer.Type;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.util.buffer.IGrowableBuffer;
import racoonman.r3d.render.util.buffer.IResizer;
import racoonman.r3d.util.IPair;

public class IndexedVertexBuilder extends VertexBuilderImpl implements IIndexedBuilder {
	private IGrowableBuffer<ByteBuffer> indices;
	private int nextIndice;
	private int indexCount;
	private int[] indexFillBuffer;

	public IndexedVertexBuilder(IVertexOrder vertexOrder, int indexBufferSize) {
		super(vertexOrder);
		this.indices = IGrowableBuffer.bytes(IResizer.fourth(), indexBufferSize);
	}
	
	@Override
	public IIndexedBuilder order(IVertexOrder order) {
		super.order(order);
		this.indexFillBuffer = new int[order.getVertexCount()];
		return this;
	}
	
	@Override
	protected void autofill() {
		for(int i = 0; i < this.vertexOrder.getVertexCount(); i++) {
			this.indexFillBuffer[i] = this.nextIndice();
		}
		
		for(int index : this.vertexOrder.getOrder()) {
			this.indices(this.indexFillBuffer[index]);
		}
	}
	
	@Override
	public IIndexedBuilder reset() {
		this.indices.clear();
		this.nextIndice = 0;
		this.indexCount = 0;
		super.reset();
		return this;
	}

	@Override
	public void finish(IMemoryCopier uploader, IRenderBuffer target) {
		target.update(uploader, this.vertexBuffers.stream().map((buffer) -> {
			return IPair.of(buffer.format, new RenderBufferData(buffer.vertexCount, buffer.data.get()));
		}).toList(), Optional.of(new RenderBufferData(this.indexCount, this.indices.get())));
	}

	@Override
	public IRenderBuffer finish(IMemoryCopier uploader) {
		if (this.getVertexCount() == 0) {
			return EmptyRenderBuffer.INSTANCE;
		}

		IRenderBuffer renderBuffer = IRenderBuffer.sized(this.indices.get().rewind().limit(), Type.STATIC);
		
		for(VertexBuffer buffer : this.vertexBuffers) {
			ByteBuffer data = buffer.data.get()
				.rewind();
			
			renderBuffer.attach(buffer.format, data.limit(), Type.STATIC);
		}
		
		this.finish(uploader, renderBuffer);
		return renderBuffer;
	}

	@Override
	public int nextIndice() {
		return this.nextIndice++;
	}

	@Override
	public IIndexedBuilder indices(int... indices) {
		this.indices.grow(indices.length * Integer.BYTES);

		for (int indice : indices) {
			this.indices.get().putInt(indice);
			this.indexCount++;
		}

		return this;
	}

	@Override
	public int getIndexCount() {
		return this.indexCount;
	}

	@Override
	public void free() {
		this.indices.free();

		super.free();
	}
}
