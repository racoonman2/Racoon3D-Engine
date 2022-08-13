package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.CommandBuffer.VertexBuffer;
import racoonman.r3d.render.api.vulkan.FrameManager.Frame;
import racoonman.r3d.render.api.vulkan.cache.PipelineState;
import racoonman.r3d.render.api.vulkan.cache.RenderCache;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.BindPoint;
import racoonman.r3d.render.api.vulkan.types.ColorComponent;
import racoonman.r3d.render.api.vulkan.types.DescriptorType;
import racoonman.r3d.render.api.vulkan.types.IndexType;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.state.uniform.UniformBuffer;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

//TODO remember to make this class package only and make applyState() private
public class VkRenderContext extends RenderContext {
	private RenderCache cache;
	private WorkDispatcher dispatcher;
	private Frame frame;
	private CommandRecorder recorder;
	private RenderPass activePass;
	private boolean inPass;
	private Map<PipelineStage, Set<Semaphore>> waits;
	private Set<Semaphore> signals;
	private State<IPipeline> pipeline;
	private UniformBuffer uniformBuffer;
	
	public VkRenderContext(RenderCache cache, Device device, WorkDispatcher dispatcher, Frame frame, UniformBuffer uniformBuffer) {
		this.cache = cache;
		this.dispatcher = dispatcher;
		this.frame = frame;
		this.recorder = new CommandRecorder(this);
		this.waits = new HashMap<>();
		this.signals = new HashSet<>();
		this.pipeline = new State<>();
		this.uniformBuffer = uniformBuffer;
	}

	@Override
	public void wait(Semaphore semaphore, PipelineStage stage) {
		this.waits.computeIfAbsent(stage, (k) -> new HashSet<>()).add(semaphore);
	}

	@Override
	public void signal(Semaphore semaphore) {
		this.signals.add(semaphore);
	}

	@Override
	public void draw(int instanceCount, int start, int amount) {
		this.applyState();
		this.recorder.record((cmdBuffer) -> {
			cmdBuffer.draw(amount, instanceCount, start, 0);
		});
	}

	@Override
	public void drawIndexed(int instanceCount, int vertexStart, int indexStart, int amount) {
		this.applyState();
		this.recorder.record((cmdBuffer) -> {
			cmdBuffer.drawIndexed(amount, instanceCount, vertexStart, indexStart, 0);
		});
	}

	@Override
	public void submit() {
		this.applyState();
		this.maybeEndPass();
		
		CommandBuffer cmdBuffer = this.frame.getCommandBuffer();
		cmdBuffer.begin();
		this.recorder.submit(cmdBuffer);
		cmdBuffer.end();

		QueueSubmission submission = QueueSubmission.of()
			.withCommandBuffer(cmdBuffer)
			.withFence(this.frame.getFence());
		
		for(Semaphore signal : this.signals) {
			submission.withSignal(signal);
		}
		
		for(Entry<PipelineStage, Set<Semaphore>> entry : this.waits.entrySet()) {
			for(Semaphore wait : entry.getValue()) {
				submission.withWait(wait, entry.getKey());
			}
		}
		
		this.dispatcher.submit(submission);
	}
	
	private void maybeBeginPass(IFramebuffer framebuffer) {
		if(this.inPass) {
			this.maybeEndPass();
		}
		
		if(this.activePass == null) {
			this.activePass = new RenderPass(this, this.recorder, this.framebuffer.getValue());
		}
			
		this.activePass.begin();
		this.inPass = true;
	}
	
	private void maybeEndPass() {
		if(this.inPass) {
			this.activePass.end();
		}
	}
	
	public void applyState() {
		this.viewport.applyChanges((viewport) -> {
			this.recorder.record((cmdBuffer) -> {
				try(MemoryStack stack = stackPush()) {
					VkViewport.Buffer viewports = VkViewport.calloc(1, stack);
					viewports.get(0)
						.x(0.0F)
						.y(viewport.getHeight())
						.minDepth(viewport.getMinDepth())
						.maxDepth(viewport.getMaxDepth())
						.width(viewport.getWidth())
						.height(-viewport.getHeight());
					cmdBuffer.setViewport(0, viewports);
				}
			});
		});
		this.scissor.applyChanges((scissor) -> {
			this.recorder.record((cmdBuffer) -> {
				try(MemoryStack stack = stackPush()) {
					VkRect2D.Buffer scissors = VkRect2D.calloc(1, stack);
					scissors.get(0)
						.extent((extent) -> extent
							.width(scissor.getWidth())
							.height(scissor.getHeight()))
						.offset((offset) -> offset
							.x(scissor.getX())
							.y(scissor.getY()));
					cmdBuffer.setScissor(0, scissors);
				}	
			});
		});
	
		this.program.check("program");
		this.framebuffer.check("framebuffer");
		this.framebuffer.applyChanges(this::maybeBeginPass);
		
		this.vertexBuffers.applyChanges((buffers) -> {
			this.recorder.record((cmdBuffer) -> {
				List<VertexBuffer> vertexBuffers = new ArrayList<>();
				
				for(IPair<VertexFormat, IDeviceBuffer> pair : buffers) {
					vertexBuffers.add(new VertexBuffer(pair.right(), 0));
				}
				
				cmdBuffer.bindVertexBuffers(0, vertexBuffers.toArray(VertexBuffer[]::new));
			});
		});
		
		this.indexBuffer.applyChanges((buffer) -> {
			this.recorder.record((cmdBuffer) -> {
				cmdBuffer.bindIndexBuffer(buffer, 0, IndexType.UINT32);
			});
		});

		this.pipeline.set(this.cache.get(this.makeState()));
		this.pipeline.applyChanges((pipeline) -> {
			this.recorder.record((cmdBuffer) -> {
				cmdBuffer.bindPipeline(pipeline);
			});
		});
		
		this.recorder.record((cmdBuffer) -> {
			try(MemoryStack stack = stackPush()) {
				this.uniformBuffer.reset();
				this.uniformBuffer.put(this.getMatrix(IMatrixType.PROJECTION));
				this.uniformBuffer.put(this.getMatrix(IMatrixType.VIEW));
				
				IDeviceBuffer buffer = this.uniformBuffer.getDeviceBuffer(); 
				
				VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc(1, stack);
				writes.apply(0, (write) -> write
					.sType$Default()
					.descriptorType(DescriptorType.UNIFORM_BUFFER.getVkType())
					.descriptorCount(1)
					.pBufferInfo(VkDescriptorBufferInfo.calloc(1, stack)
						.buffer(buffer.asLong())
						.offset(0)
						.range(128)));
				
				cmdBuffer.pushDescriptor(BindPoint.GRAPHICS, 
					this.pipeline.getValue().getLayout(), 0, writes);				
			}
		});
	}
	
	private PipelineState makeState() {
		return new PipelineState(
			this.getProgram(),
			this.vertexBuffers.getValues().stream().map(IPair::left).toArray(VertexFormat[]::new),
			this.getTopology(),
			this.getPolygonMode(),
			this.getCullMode(),
			this.getLineWidth(),
			this.getFrontFace(),
			this.getSampleCount(),
			this.getWriteMask().toArray(ColorComponent[]::new),
			this.getFramebuffer()
		);
	}
}
