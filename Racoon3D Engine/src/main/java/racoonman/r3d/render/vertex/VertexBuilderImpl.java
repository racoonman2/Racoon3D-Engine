package racoonman.r3d.render.vertex;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Matrix4f;

import racoonman.r3d.render.buffer.EmptyRenderBuffer;
import racoonman.r3d.render.buffer.IRenderBuffer;
import racoonman.r3d.render.buffer.IRenderBuffer.Type;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.util.buffer.IGrowableBuffer;
import racoonman.r3d.render.util.buffer.IResizer;
import racoonman.r3d.render.vertex.VertexFormat.Attribute;
import racoonman.r3d.util.IPair;

public class VertexBuilderImpl implements IVertexBuilder {
	protected List<VertexBuffer> vertexBuffers;
	protected IVertexOrder vertexOrder;
	private int currentBuffer;
	private int primaryBuffer;

	protected VertexBuilderImpl(IVertexOrder vertexOrder) {
		this.vertexBuffers = new ArrayList<>();
		this.vertexOrder = vertexOrder;

		this.order(vertexOrder);
	}

	@Override
	public IVertexBuilder withBuffer(VertexFormat format, int size) {
		this.vertexBuffers.add(new VertexBuffer(size, format));
		this.currentBuffer = this.vertexBuffers.size() - 1;
		return this;
	}
	
	@Override
	public IVertexBuilder floats(Attribute attribute, float... floats) {
		this.checkAndGrow(attribute, floats.length * attribute.size());
			
		for (float f : floats) {
			this.buffer().get().putFloat(f);
		}
		
		return this;
	}

	@Override
	public IVertexBuilder doubles(Attribute attribute, double... doubles) {
		this.checkAndGrow(attribute, doubles.length * attribute.size());
		
		for (double d : doubles) {
			this.buffer().get().putDouble(d);
		}

		return this;
	}

	@Override
	public IVertexBuilder ints(Attribute attribute, int... ints) {
		this.checkAndGrow(attribute, ints.length * attribute.size());

		for (int i : ints) {
			this.buffer().get().putInt(i);
		}

		return this;
	}

	@Override
	public IVertexBuilder reset() {
		this.getCurrent().reset();
		return this;
	}

	protected void autofill() {
	}
	
	@Override
	public IVertexBuilder end() {
		VertexBuffer current = this.getCurrent();

		current.vertexIndex++;
		if (this.vertexBuffers.get(this.currentBuffer).vertexCount % this.vertexOrder.getVertexCount() == 0) {
			this.autofill();
		}

		current.attributeIndex = 0;
		current.vertexCount++;
		return this;
	}

	@Override
	public IVertexBuilder transform(Matrix4f matrix) {
		this.getTransform().mul(matrix);
		return this;
	}

	@Override
	public IVertexBuilder swap(int buffer) {
		this.currentBuffer = buffer;
		return this;
	}

	@Override
	public IVertexBuilder primary(int buffer) {
		this.primaryBuffer = buffer;
		return this;
	}

	@Override
	public int getVertexCount() {
		int vertexCount = this.vertexBuffers.get(this.primaryBuffer).vertexCount;
		
		for(VertexBuffer buffer : this.vertexBuffers) {
			vertexCount = Math.min(vertexCount, buffer.vertexCount);
		}
		
		return vertexCount;
	}

	@Override
	public Matrix4f getTransform() {
		return this.getCurrent().transform;
	}

	@Override
	public void finish(IMemoryCopier uploader, IRenderBuffer target) {
		target.update(uploader, this.vertexBuffers.stream().map((buffer) -> {
			return IPair.of(buffer.format, new RenderBufferData(buffer.vertexCount, buffer.data.get()));
		}).toList(), Optional.empty());
	}

	// FIXME Empty secondary buffers will still throw an exception
	@Override
	public IRenderBuffer finish(IMemoryCopier uploader) {
		if (this.getVertexCount() == 0) {
			return EmptyRenderBuffer.INSTANCE;
		}

		IRenderBuffer renderBuffer = IRenderBuffer.withSize();
		
		for(VertexBuffer buffer : this.vertexBuffers) {
			ByteBuffer data = buffer.data.get()
				.rewind();
			
			renderBuffer.withBuffer(buffer.format, data.limit(), Type.STATIC);
		}
		
		this.finish(uploader, renderBuffer);
		return renderBuffer;
	}

	@Override
	public void free() {
		for (VertexBuffer buffer : this.vertexBuffers) {
			buffer.free();
		}
	}
	
	@Override
	public IVertexBuilder order(IVertexOrder order) {
		this.vertexOrder = order;
		return this;
	}

	private void checkAndGrow(Attribute attribute, int amount) {
		VertexBuffer buffer = this.getCurrent();

		buffer.check(attribute);
		buffer.data.grow(amount);

		if (++buffer.attributeIndex == buffer.format.getAttributeCount()) {
			this.end();
		}
	}

	private IGrowableBuffer<ByteBuffer> buffer() {
		return this.getCurrent().data;
	}

	private VertexBuffer getCurrent() {
		return this.vertexBuffers.get(this.currentBuffer);
	}

	class VertexBuffer {
		IGrowableBuffer<ByteBuffer> data;
		Matrix4f transform;
		VertexFormat format;
		int attributeIndex;
		int vertexCount;
		int vertexIndex;
		Vertex[] vertices;

		VertexBuffer(int initialSize, VertexFormat format) {
			this.data = IGrowableBuffer.bytes(IResizer.third(), initialSize);
			this.transform = new Matrix4f();
			this.format = format;
			this.vertices = new Vertex[VertexBuilderImpl.this.vertexOrder.getVertexCount()];
			
			for(int i = 0; i < this.vertices.length; i++) {
				this.vertices[i] = new Vertex(format);
			}
		}

		Vertex getVertex() {
			return this.vertices[this.vertexIndex];
		}
		
		void reset() {
			this.vertexCount = 0;
			this.attributeIndex = 0;
			this.vertexIndex = 0;
			this.transform.identity();
			this.data.clear();
		}

		void free() {
			this.data.free();
			
			for(Vertex vertex : this.vertices) {
				vertex.free();
			}
		}

		void check(Attribute attribute) {
			Attribute expectedAttribute = this.format.getAttribute(this.attributeIndex);

			if (!expectedAttribute.equals(attribute)) {
				throw new IllegalStateException("Incorrect attribute at index " + this.attributeIndex + ", got " + attribute + ", expected " + expectedAttribute);
			}
		}
	}
	
	class Vertex {
		ByteBuffer buffer;
		
		Vertex(VertexFormat format) {
			this.buffer = memAlloc(format.getStride());
		}
		
		void free() {
			memFree(this.buffer);
		}
	}
}
