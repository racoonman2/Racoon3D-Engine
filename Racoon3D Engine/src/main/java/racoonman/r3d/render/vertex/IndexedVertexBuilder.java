package racoonman.r3d.render.vertex;

import java.nio.ByteBuffer;
import java.util.Optional;

import racoonman.r3d.render.buffer.EmptyRenderBuffer;
import racoonman.r3d.render.buffer.IRenderBuffer;
import racoonman.r3d.render.buffer.IndexedRenderBuffer;
import racoonman.r3d.render.util.buffer.IGrowableBuffer;
import racoonman.r3d.render.util.buffer.IResizer;
import racoonman.r3d.util.IPair;

//move this into a buffer attachment instead of a seperate class
public class IndexedVertexBuilder extends VertexBuilderImpl implements IIndexedBuilder {
	private IGrowableBuffer<ByteBuffer> indices;
	private int nextIndice;
	private int indexCount;
	private int[] indexFillBuffer;

	public IndexedVertexBuilder(IVertexOrder vertexOrder, int indexBufferSize) {
		super(vertexOrder, false);
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
	public void finish(IRenderBuffer target) {
		target.update(this.vertexBuffers.stream().map((buffer) -> {
			return IPair.of(buffer.format, new RenderBufferData(buffer.vertexCount, buffer.data.get()));
		}).toList(), Optional.of(new RenderBufferData(this.indexCount, this.indices.get())));
	}

	@Override
	public IRenderBuffer finish() {
		if (this.getVertexCount() == 0) {
			return EmptyRenderBuffer.INSTANCE;
		}

		IRenderBuffer renderBuffer = new IndexedRenderBuffer();
		this.finish(renderBuffer);
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
