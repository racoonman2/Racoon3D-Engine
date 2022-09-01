package racoonman.r3d.render;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.api.sync.LocalSync;
import racoonman.r3d.render.api.types.ColorComponent;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.Mode;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.render.compute.Compute;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.matrix.MatrixStackImpl;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.state.IState;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

//TODO multiple viewports / scissors
//FIXME CCW and CW are opposite on opengl and vulkan due to flipping the y axis
public abstract class Context extends MatrixStackImpl implements AutoCloseable, IMemoryCopier, IState {
	protected State<IShaderProgram> program;
	protected State<CullMode> cullMode;
	protected State<Float> lineWidth;
	protected State<Viewport> viewport;
	protected State<Scissor> scissor;
	protected State<List<IPair<VertexFormat, IDeviceBuffer>>> vertexBuffers;
	protected State<IDeviceBuffer> indexBuffer;
	protected State<Mode> mode;
	protected State<SampleCount> sampleCount;
	protected State<ColorComponent[]> writeMask;
	protected State<PolygonMode> polygonMode;
	protected State<FrontFace> frontFace;
	protected Optional<RenderPass> pass;
	
	public Context() {
		super(Config.MATRIX_STACK_SIZE, IMatrixType.PROJECTION);
		this.program = new State<>();
		this.cullMode = new State<>(CullMode.BACK);
		this.lineWidth = new State<>(1.0F);
		this.viewport = new State<>(new Viewport(0, 0, 0, 0, 0.0F, 1.0F));
		this.scissor = new State<>(new Scissor(0, 0, 0, 0));
		this.vertexBuffers = new State<>();
		this.indexBuffer = new State<>();
		this.mode = new State<>(Mode.TRIANGLE_LIST);
		this.sampleCount = new State<>(SampleCount.COUNT_1);
		this.writeMask = new State<>(ColorComponent.values());
		this.polygonMode = new State<>(PolygonMode.FILL);
		this.frontFace = new State<>(FrontFace.CW);
		this.pass = Optional.empty();
	}

	public RenderPass begin(IFramebuffer framebuffer) {
		this.setViewport(framebuffer.getViewport(0.0F, 1.0F));
		this.setScissor(framebuffer.getScissor());

		RenderPass pass = this.createPass(framebuffer);
		pass.begin();
		return pass;
	}
	
	public abstract RenderPass createPass(IFramebuffer framebuffer);
	
	public abstract void alert(IDeviceSync... syncs);
	
	public abstract void await(IDeviceSync sync, Stage stage);
	
	public abstract void await(LocalSync sync);
	
	public abstract Compute dispatch(IShader shader, int xThreads, int yThreads, int zThreads);
	
	@Override
	public IShaderProgram getProgram() {
		return this.program.getValue();
	}

	@Override
	public CullMode getCullMode() {
		return this.cullMode.getValue();
	}

	@Override
	public float getLineWidth() {
		return this.lineWidth.getValue();
	}

	@Override
	public Viewport getViewport() {
		return this.viewport.getValue();
	}

	@Override
	public Scissor getScissor() {
		return this.scissor.getValue();
	}

	@Override
	public Mode getMode() {
		return this.mode.getValue();
	}

	@Override
	public SampleCount getSampleCount() {
		return this.sampleCount.getValue();
	}

	@Override
	public ColorComponent[] getWriteMask() {
		return this.writeMask.getValue();
	}

	@Override
	public PolygonMode getPolygonMode() {
		return this.polygonMode.getValue();
	}

	@Override
	public FrontFace getFrontFace() {
		return this.frontFace.getValue();
	}

	@Override
	public void bindProgram(IShaderProgram program) {
		this.program.set(program);
		this.updateState();
	}
	
	@Override
	public void setCullMode(CullMode cullMode) {
		this.cullMode.set(cullMode);
		this.updateState();
	}

	@Override
	public void setLineWidth(float width) {
		this.lineWidth.set(width);
		this.updateState();
	}

	@Override
	public void setViewport(Viewport viewport) {
		this.viewport.set(viewport);
		this.updateState();
	}

	@Override
	public void setScissor(Scissor scissor) {
		this.scissor.set(scissor);
		this.updateState();
	}

	@Override
	public void bindVertexBuffers(List<IPair<VertexFormat, IDeviceBuffer>> buffers) {
		this.vertexBuffers.set(buffers);
		this.updateState();
	}

	@Override
	public void bindIndexBuffer(IDeviceBuffer indexBuffer) {
		this.indexBuffer.set(indexBuffer);
		this.updateState();
	}

	@Override
	public void setTopology(Mode topology) {
		this.mode.set(topology);
		this.updateState();
	}

	@Override
	public void setSamples(SampleCount samples) {
		this.sampleCount.set(samples);
		this.updateState();
	}

	@Override
	public void setWriteMask(ColorComponent... components) {
		this.writeMask.set(components);
		this.updateState();
	}

	@Override
	public void setPolygonMode(PolygonMode mode) {
		this.polygonMode.set(mode);
		this.updateState();
	}

	@Override
	public void setFrontFace(FrontFace frontFace) {
		this.frontFace.set(frontFace);
		this.updateState();
	}
	
	protected abstract void submit();
	
	protected abstract void updateState();
	
	@Override
	public void close() {
		this.submit();
	}
	
	public static class State<T> {
		private T value;
		private boolean updated;
		
		public State() {
		}
		
		public State(T initial) {
			this.set(initial);
		}
		
		public boolean set(T value) {
			if(this.value == null || !this.value.equals(value)) {
				this.value = value;
				this.updated = true;
			}
			
			return this.updated;
		}
		
		public T getValue() {
			return this.value;
		}
		
		public void setAndApply(T value, Consumer<T> consumer) {
			this.set(value);
			this.update(consumer);
		}
		
		public void update(Consumer<T> consumer) {
			if(this.updated) {
				consumer.accept(this.value);
				this.updated = false;
			}
		}
		
		public boolean exists() {
			return this.value != null;
		}
	}
}
