package racoonman.r3d.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix4fStack;
import org.joml.Vector4f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.ColorComponent;
import racoonman.r3d.render.api.vulkan.types.CullMode;
import racoonman.r3d.render.api.vulkan.types.FrontFace;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;
import racoonman.r3d.render.api.vulkan.types.PolygonMode;
import racoonman.r3d.render.api.vulkan.types.SampleCount;
import racoonman.r3d.render.api.vulkan.types.Topology;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.matrix.MatrixStackImpl;
import racoonman.r3d.render.util.Color;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;
import racoonman.r3d.util.math.Mathf;

//TODO multiple viewports / scissors
//FIXME CCW and CW are opposite on opengl and vulkan due to flipping the y axis
public abstract class RenderContext extends MatrixStackImpl implements AutoCloseable {
	protected State<IShaderProgram> program;
	protected State<CullMode> cullMode;
	protected State<Float> lineWidth;
	protected State<Viewport> viewport;
	protected State<Scissor> scissor;
	protected State<IFramebuffer> framebuffer;
	protected Vector4f clear;
	protected ListState<IPair<VertexFormat, IDeviceBuffer>> vertexBuffers;
	protected State<IDeviceBuffer> indexBuffer;
	protected State<Topology> topology;
	protected State<SampleCount> sampleCount;
	protected ListState<ColorComponent> writeMask;
	protected State<PolygonMode> polygonMode;
	protected State<FrontFace> frontFace;
	
	public RenderContext() {
		super(Config.defaultMatrixStackSize, IMatrixType.PROJECTION);
		this.program = new State<>();
		this.cullMode = new State<>(CullMode.BACK);
		this.lineWidth = new State<>(1.0F);
		this.viewport = new State<>(Viewport.of(0, 0, 0, 0, 0.0F, 1.0F));
		this.scissor = new State<>(Scissor.of(0, 0, 0, 0));
		this.framebuffer = new State<>();
		this.clear = new Vector4f();
		this.vertexBuffers = new ListState<>();
		this.indexBuffer = new State<>();
		this.topology = new State<>(Topology.TRIANGLE_LIST);
		this.sampleCount = new State<>(SampleCount.COUNT_1);
		this.writeMask = new ListState<>(ColorComponent.values());
		this.polygonMode = new State<>(PolygonMode.FILL);
		this.frontFace = new State<>(FrontFace.CW);
	}
	
	//TODO remove, only used by vulkan
	public abstract void wait(Semaphore semaphore, PipelineStage stage);
	
	//TODO remove, only used by vulkan
	public abstract void signal(Semaphore semaphore);
	
	public IShaderProgram getProgram() {
		return this.program.getValue();
	}
	
	public CullMode getCullMode() {
		return this.cullMode.getValue();
	}
	
	public float getLineWidth() {
		return this.lineWidth.getValue();
	}
	
	public Viewport getViewport() {
		return this.viewport.getValue();
	}
	
	public Scissor getScissor() {
		return this.scissor.getValue();
	}
	
	public IFramebuffer getFramebuffer() {
		return this.framebuffer.getValue();
	}

	public Vector4f getClear() {
		return this.clear;
	}
	
	public Topology getTopology() {
		return this.topology.getValue();
	}
	
	public SampleCount getSampleCount() {
		return this.sampleCount.getValue();
	}
	
	public List<ColorComponent> getWriteMask() {
		return ImmutableList.copyOf(this.writeMask.getValues());
	}
	
	public PolygonMode getPolygonMode() {
		return this.polygonMode.getValue();
	}
	
	public FrontFace getFrontFace() {
		return this.frontFace.getValue();
	}
	
	public void perspective(float fov, float aspect, float near, float far) {
		this.<Matrix4fStack>currentMatrix().perspective(Mathf.toRadians(fov), aspect, near, far, true);
	}
	
	public void perspective(float fov, float near, float far) {
		this.matrixType(IMatrixType.PROJECTION);
		
		IFramebuffer target = this.getFramebuffer();
		this.perspective(fov, (float) target.getWidth() / target.getHeight(), near, far);
	}
	
	public void ortho(float left, float right, float top, float bottom) {
		this.<Matrix4fStack>currentMatrix().ortho(left, right, top, bottom, 0.1F, 100.0F, true);
	}
	
	public void program(IShaderProgram program) {
		this.program.set(program);
	}
	
	public void cullMode(CullMode cullMode) {
		this.cullMode.set(cullMode);
	}
	
	public void lineWidth(float width) {
		this.lineWidth.set(width);
	}
	
	public void viewport(Viewport viewport) {
		this.viewport.set(viewport);
	}

	public void scissor(Scissor scissor) {
		this.scissor.set(scissor);
	}
	
	public void framebuffer(IFramebuffer framebuffer) {
		this.framebuffer.set(framebuffer);
	}
	
	public void clear(int r, int g, int b, int a) {
		this.clear(
			Color.normalize(r),
			Color.normalize(g),
			Color.normalize(b),
			Color.normalize(a)
		);
	}
	
	public void clear(float r, float g, float b, float a) {
		this.clear.set(r, g, b, a);
	}
	
	public void bindVertexBuffers(List<IPair<VertexFormat, IDeviceBuffer>> buffers) {
		this.vertexBuffers.set(buffers);
	}
	
	public void bindIndexBuffer(IDeviceBuffer indexBuffer) {
		this.indexBuffer.set(indexBuffer);
	}
	
	public void topology(Topology topology) {
		this.topology.set(topology);
	}
	
	public void samples(SampleCount samples) {
		this.sampleCount.set(samples);
	}
	
	public void writeMask(ColorComponent... channels) {
		this.writeMask.set(channels);
	}
	
	public void polygonMode(PolygonMode mode) {
		this.polygonMode.set(mode);
	}
	
	public void frontFace(FrontFace frontFace) {
		this.frontFace.set(frontFace);
	}
	
	public abstract void draw(int instanceCount, int start, int amount);

	public abstract void drawIndexed(int instanceCount, int vertexStart, int indexStart, int amount);
	
	public abstract void submit();
	
	@Override
	public void close() {
		this.submit();
	}
	
	public static class ListState<T> {
		private List<T> values;
		private boolean updated;

		public ListState() {
			this.values = new ArrayList<>();
		}
		
		public ListState(T[] initialValues) {
			this.values = Lists.newArrayList(initialValues);
		}
		
		public void set(List<T> values) {
			if(!this.values.equals(values)) {
				this.values.clear();
				this.values.addAll(values);
				this.updated = true;
			}
		}
		
		public void set(T[] values) {
			boolean changed = values.length != this.values.size();
			
			if(!changed) {
				for(int i = 0; i < values.length; i++) {
					if(this.values.get(i) != values[i]) {
						changed = true;
						break;
					}
				}
			}
			
			if(changed) {
				this.values.clear();
				for(T t : values) {
					this.values.add(t);
				}
				this.updated = true;
			}
		}
		
		public List<T> getValues() {
			return this.values;
		}
		
		public void applyChanges(Consumer<List<T>> consumer) {
			if(this.updated) {
				if(!this.values.isEmpty()) {
					consumer.accept(this.values);
				}
				
				this.updated = false;
			}
		}
		
		public boolean exists() {
			return this.values.isEmpty();
		}
		
		public void check(String name) {
			if(!this.exists()) {
				throw new IllegalStateException("Required state [" + name + "] is not bound");
			}
		}
	}
	
	public static class State<T> {
		private T value;
		private boolean updated;
		
		public State() {
		}
		
		public State(T initial) {
			this.set(initial);
		}
		
		public void set(T value) {
			if(this.value == null || !this.value.equals(value)) {
				this.value = value;
				this.updated = true;
			}
		}
		
		public T getValue() {
			return this.value;
		}
		
		public void applyChanges(Consumer<T> consumer) {
			if(this.updated) {
				consumer.accept(this.value);
				this.updated = false;
			}
		}
		
		public boolean exists() {
			return this.value != null;
		}
		
		public void check(String name) {
			if(!this.exists()) {
				throw new IllegalStateException("Required state [" + name + "] is not bound");
			}
		}
	}
}
