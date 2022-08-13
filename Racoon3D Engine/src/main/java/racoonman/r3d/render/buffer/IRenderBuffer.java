package racoonman.r3d.render.buffer;

import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.IBindable;
import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.vertex.RenderBufferData;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

public interface IRenderBuffer extends IBindable {
	void draw(RenderContext context, int instanceCount);

	//TODO void drawRange(RenderContext context, int instanceCount, int start, int amount);

	void update(List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer);

	void free();
	
    //TODO void copy(IRenderBuffer buffer);
	
	IRenderBuffer sub();
	
	List<IPair<VertexFormat, IDeviceBuffer>> getVertexBuffers();
	
	default Optional<IDeviceBuffer> getIndexBuffer() {
		return Optional.empty();
	}
	
	// TODO
	public static IRenderBuffer withSize(int initialSize) {
		// TODO allow sizes of 0
		if (initialSize <= 0) {
			throw new IllegalArgumentException("initial size must be greater than 0");
		} else {
			return null;
		}
	}
}
