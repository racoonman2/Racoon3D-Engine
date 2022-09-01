package racoonman.r3d.render.state;

import java.util.List;

import racoonman.r3d.render.Scissor;
import racoonman.r3d.render.Viewport;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.types.ColorComponent;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.Mode;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.matrix.IMatrixStack;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

public interface IState extends IMatrixStack {
	IShaderProgram getProgram();
	
	CullMode getCullMode();
	
	float getLineWidth();
	
	Viewport getViewport();
	
	Scissor getScissor();
	
	Mode getMode();
	
	SampleCount getSampleCount();
	
	ColorComponent[] getWriteMask();
	
	PolygonMode getPolygonMode();
	
	FrontFace getFrontFace();
	
	void bindProgram(IShaderProgram program);
	
	void setCullMode(CullMode mode);
	
	void setLineWidth(float lineWidth);
	
	void setViewport(Viewport viewport);
	
	void setScissor(Scissor scissor);

	void bindVertexBuffers(List<IPair<VertexFormat, IDeviceBuffer>> buffers);

	void bindIndexBuffer(IDeviceBuffer indexBuffer);
	
	void setTopology(Mode topology);
	
	void setSamples(SampleCount sampleCount);
	
	void setWriteMask(ColorComponent... components);
	
	void setPolygonMode(PolygonMode polygonMode);
	
	void setFrontFace(FrontFace frontFace);
}
