package racoonman.r3d.render.api.objects;

import java.util.List;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.Scissor;
import racoonman.r3d.render.Viewport;
import racoonman.r3d.render.api.types.ColorComponent;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.Mode;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.state.IState;
import racoonman.r3d.render.util.Color;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

//TODO pass dependent states
public abstract class RenderPass implements AutoCloseable, IState, IMemoryCopier {
	protected Context context;
	
	public RenderPass(Context context) {
		this.context = context;
	}
	
	public abstract void begin();
	
	public abstract void end();
	
	public abstract IFramebuffer getFramebuffer();
	
	public abstract void draw(int instanceCount, int start, int amount);

	public abstract void drawIndexed(int instanceCount, int vertexStart, int indexStart, int amount);
	
	public abstract void clear(float r, float g, float b, float a);
	
	public void clear(int r, int g, int b, int a) {
		this.clear(Color.normalize(r), Color.normalize(g), Color.normalize(b), Color.normalize(a));
	}
	
	@Override
	public void close() {
		this.end();
	}

	@Override
	public <T> T getMatrix(IMatrixType<T> type) {
		return this.context.getMatrix(type);
	}

	@Override
	public IMatrixType<?> currentType() {
		return this.context.currentType();
	}

	@Override
	public void matrixType(IMatrixType<?> type) {
		this.context.matrixType(type);
	}

	@Override
	public IShaderProgram getProgram() {
		return this.context.getProgram();
	}

	@Override
	public CullMode getCullMode() {
		return this.context.getCullMode();
	}

	@Override
	public float getLineWidth() {
		return this.context.getLineWidth();
	}

	@Override
	public Viewport getViewport() {
		return this.context.getViewport();
	}

	@Override
	public Scissor getScissor() {
		return this.context.getScissor();
	}

	@Override
	public Mode getMode() {
		return this.context.getMode();
	}

	@Override
	public SampleCount getSampleCount() {
		return this.context.getSampleCount();
	}

	@Override
	public ColorComponent[] getWriteMask() {
		return this.context.getWriteMask();
	}

	@Override
	public PolygonMode getPolygonMode() {
		return this.context.getPolygonMode();
	}

	@Override
	public FrontFace getFrontFace() {
		return this.context.getFrontFace();
	}

	@Override
	public void bindProgram(IShaderProgram program) {
		this.context.bindProgram(program);
	}

	@Override
	public void setCullMode(CullMode mode) {
		this.context.setCullMode(mode);
	}

	@Override
	public void setLineWidth(float lineWidth) {
		this.context.setLineWidth(lineWidth);
	}

	@Override
	public void setViewport(Viewport viewport) {
		this.context.setViewport(viewport);
	}

	@Override
	public void setScissor(Scissor scissor) {
		this.context.setScissor(scissor);
	}

	@Override
	public void bindVertexBuffers(List<IPair<VertexFormat, IDeviceBuffer>> buffers) {
		this.context.bindVertexBuffers(buffers);
	}

	@Override
	public void bindIndexBuffer(IDeviceBuffer indexBuffer) {
		this.context.bindIndexBuffer(indexBuffer);
	}

	@Override
	public void setTopology(Mode topology) {
		this.context.setTopology(topology);
	}

	@Override
	public void setSamples(SampleCount sampleCount) {
		this.context.setSamples(sampleCount);
	}

	@Override
	public void setWriteMask(ColorComponent... components) {
		this.context.setWriteMask(components);
	}

	@Override
	public void setPolygonMode(PolygonMode polygonMode) {
		this.context.setPolygonMode(polygonMode);
	}

	@Override
	public void setFrontFace(FrontFace frontFace) {
		this.context.setFrontFace(frontFace);
	}
	
	@Override
	public void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		this.context.copy(src, dst);
	}

	@Override
	public void copy(IImage src, IImage dst) {
		this.context.copy(src, dst);
	}
}
