package racoonman.r3d.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.joml.Matrix4fStack;

import com.google.common.collect.Lists;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.api.objects.Scissor;
import racoonman.r3d.render.api.objects.Viewport;
import racoonman.r3d.render.api.objects.sync.GpuFence;
import racoonman.r3d.render.api.vulkan.Semaphore;
import racoonman.r3d.render.api.vulkan.types.ColorComponent;
import racoonman.r3d.render.api.vulkan.types.CullMode;
import racoonman.r3d.render.api.vulkan.types.FrontFace;
import racoonman.r3d.render.api.vulkan.types.PolygonMode;
import racoonman.r3d.render.api.vulkan.types.SampleCount;
import racoonman.r3d.render.api.vulkan.types.Stage;
import racoonman.r3d.render.api.vulkan.types.Topology;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.matrix.MatrixStackImpl;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;
import racoonman.r3d.util.math.Mathf;

//TODO multiple viewports / scissors
//FIXME CCW and CW are opposite on opengl and vulkan due to flipping the y axis
public abstract class Context extends MatrixStackImpl implements AutoCloseable, IMemoryCopier {
	protected State<IShaderProgram> program;
	protected State<CullMode> cullMode;
	protected State<Float> lineWidth;
	protected State<Viewport> viewport;
	protected State<Scissor> scissor;
	protected ListState<IPair<VertexFormat, IDeviceBuffer>> vertexBuffers;
	protected State<IDeviceBuffer> indexBuffer;
	protected State<Topology> topology;
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
		this.vertexBuffers = new ListState<>();
		this.indexBuffer = new State<>();
		this.topology = new State<>(Topology.TRIANGLE_LIST);
		this.sampleCount = new State<>(SampleCount.COUNT_1);
		this.writeMask = new State<>(ColorComponent.values());
		this.polygonMode = new State<>(PolygonMode.FILL);
		this.frontFace = new State<>(FrontFace.CW);
		this.pass = Optional.empty();
	}

	public RenderPass beginPass(IFramebuffer framebuffer) {
		this.viewport(framebuffer.getViewport(0.0F, 1.0F));
		this.scissor(framebuffer.getScissor());
		framebuffer.acquire();
		return this.createPass(framebuffer);
	}
	
	public abstract RenderPass createPass(IFramebuffer framebuffer);

	//TODO remove, directly references vulkan
	public abstract void wait(Semaphore semaphore, Stage stage);
	
	//TODO remove, directly references vulkan
	public abstract void signal(Semaphore semaphore);
	
	public abstract void insert(GpuFence fence);
	
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
	
	public Topology getTopology() {
		return this.topology.getValue();
	}
	
	public SampleCount getSampleCount() {
		return this.sampleCount.getValue();
	}
	
	public ColorComponent[] getWriteMask() {
		return this.writeMask.getValue();
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
	
	public void perspective(int width, int height, float fov, float near, float far) {
		this.matrixType(IMatrixType.PROJECTION);
		
		this.perspective(fov, (float) width / height, near, far);
	}
	
	public void ortho(float left, float right, float top, float bottom, float near, float far) {
		this.<Matrix4fStack>currentMatrix().ortho(left, right, top, bottom, near, far, true);
	}
	
	public void program(IShaderProgram program) {
		this.program.set(program);
		this.updateState();
	}
	
	public void cullMode(CullMode cullMode) {
		this.cullMode.set(cullMode);
		this.updateState();
	}
	
	public void lineWidth(float width) {
		this.lineWidth.set(width);
		this.updateState();
	}
	
	public void viewport(Viewport viewport) {
		this.viewport.set(viewport);
		this.updateState();
	}

	public void scissor(Scissor scissor) {
		this.scissor.set(scissor);
		this.updateState();
	}
	
	public void bindVertexBuffers(List<IPair<VertexFormat, IDeviceBuffer>> buffers) {
		this.vertexBuffers.set(buffers);
		this.updateState();
	}
	
	public void bindIndexBuffer(IDeviceBuffer indexBuffer) {
		this.indexBuffer.set(indexBuffer);
		this.updateState();
	}
	
	public void topology(Topology topology) {
		this.topology.set(topology);
		this.updateState();
	}
	
	public void samples(SampleCount samples) {
		this.sampleCount.set(samples);
		this.updateState();
	}
	
	public void writeMask(ColorComponent... channels) {
		this.writeMask.set(channels);
		this.updateState();
	}
	
	public void polygonMode(PolygonMode mode) {
		this.polygonMode.set(mode);
		this.updateState();
	}
	
	public void frontFace(FrontFace frontFace) {
		this.frontFace.set(frontFace);
		this.updateState();
	}
	
	protected abstract void updateState();
	
	public abstract void draw(int instanceCount, int start, int amount);

	public abstract void drawIndexed(int instanceCount, int vertexStart, int indexStart, int amount);
	
	public abstract void submit();
	
	@Override
	public void close() {
		this.submit();
	}
	
	//TODO remove
	public static class ListState<T> {
		private List<T> values;
		private boolean updated;

		public ListState() {
			this.values = new ArrayList<>();
		}
		
		public ListState(T[] initialValues) {
			this.values = Lists.newArrayList(initialValues);
		}
		
		public boolean set(List<T> values) {
			if(this.values.hashCode() != values.hashCode()) {
				this.values.clear();
				this.values.addAll(values);
				this.updated = true;
			}
			
			return this.updated;
		}
		
		public boolean set(T[] values) {
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
			
			return this.updated;
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
			this.applyChanges(consumer);
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
	}
}
