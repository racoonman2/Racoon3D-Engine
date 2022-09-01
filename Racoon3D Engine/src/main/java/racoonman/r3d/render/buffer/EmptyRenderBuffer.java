package racoonman.r3d.render.buffer;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.vertex.RenderBufferData;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

public class EmptyRenderBuffer implements IRenderBuffer {
	public static final EmptyRenderBuffer INSTANCE = new EmptyRenderBuffer();

	@Override
	public void draw(RenderPass pass, int instanceCount) {
	}
	
	@Override
	public void update(IMemoryCopier uploader, List<IPair<VertexFormat, RenderBufferData>> vertexBuffers, Optional<RenderBufferData> indexBuffer) {
	}
	
	@Override
	public List<IPair<VertexFormat, IDeviceBuffer>> getVertexBuffers() {
		return ImmutableList.of();
	}

	@Override
	public IRenderBuffer attach(VertexFormat format, IDeviceBuffer buffer) {
		return this;
	}
	
	@Override
	public void free() {
	}
}
