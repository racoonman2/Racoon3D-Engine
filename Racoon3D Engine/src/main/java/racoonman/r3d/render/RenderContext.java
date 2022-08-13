package racoonman.r3d.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix4fStack;
import org.joml.Vector4f;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.CullMode;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.matrix.MatrixStackImpl;
import racoonman.r3d.render.util.Color;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;
import racoonman.r3d.util.math.Mathf;

//TODO multiple viewports / scissors
//TODO add ortho matrix function
//FIXME CCW and CW are opposite on opengl and vulkan due to flipping the y axis
public abstract class RenderContext extends MatrixStackImpl implements AutoCloseable {
	protected State<IShaderProgram> program;
	protected State<CullMode> cullMode;
	protected State<Float> lineWidth;
	protected State<Viewport> viewport;
	protected State<Scissor> scissor;
	protected State<IFramebuffer> framebuffer;
	protected Vector4f clear;
	protected List<IPair<VertexFormat, IDeviceBuffer>> vertexBuffers;
	protected State<IDeviceBuffer> indexBuffer;
	
	public RenderContext() {
		super(Config.defaultMatrixStackSize, IMatrixType.PROJECTION);
		this.program = new State<>();
		this.cullMode = new State<>(CullMode.NONE); //TODO change this to BACK once everything is working
		this.lineWidth = new State<>(1.0F);
		this.viewport = new State<>(Viewport.of(0, 0, 0, 0, 0.0F, 1.0F));
		this.scissor = new State<>(Scissor.of(0, 0, 0, 0));
		this.framebuffer = new State<>();
		this.clear = new Vector4f();
		this.vertexBuffers = new ArrayList<>();
		this.indexBuffer = new State<>();
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
	
	public IFramebuffer getFramebuffer() {
		return this.framebuffer.getValue();
	}

	public Vector4f getClear() {
		return this.clear;
	}
	
	public void perspective(float fov, float near, float far) {
		IFramebuffer target = this.getFramebuffer();
		this.<Matrix4fStack>currentMatrix().perspective(Mathf.toRadians(fov), (float) target.getWidth() / (float) target.getHeight(), near, far);
	}
	
	public void program(IShaderProgram program) {
		this.program.set(program);
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
	
	public abstract void draw(int instanceCount, int start, int amount);

	public abstract void drawIndexed(int instanceCount, int vertexStart, int indexStart, int amount);
	
	public abstract void submit();
	
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
	
	protected static void check(String name, List<?> list) {
		if(list.isEmpty()) {
			throw new IllegalStateException("Required state [" + name + "] is not bound");
		}
	}
}
