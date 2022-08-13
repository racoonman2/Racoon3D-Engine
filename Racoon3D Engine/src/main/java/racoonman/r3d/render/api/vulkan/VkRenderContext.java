package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.FrameManager.Frame;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;

//TODO remember to make this class package only and make applyState() private
public class VkRenderContext extends RenderContext {
	private WorkDispatcher dispatcher;
	private Frame frame;
	private CommandRecorder recorder;
	private RenderPass activePass;
	private boolean inPass;
	private Map<PipelineStage, Set<Semaphore>> waits;
	private Set<Semaphore> signals;
	
	public VkRenderContext(WorkDispatcher dispatcher, Frame frame) {
		this.dispatcher = dispatcher;
		this.frame = frame;
		this.recorder = new CommandRecorder(this);
		this.waits = new HashMap<>();
		this.signals = new HashSet<>();
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
		//No pass was started, framebuffer still needs to be cleared
		if(this.activePass == null) {
			this.applyState();
		}
		
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
		//	this.program.check("program");
		this.framebuffer.check("framebuffer");
		this.framebuffer.applyChanges(this::maybeBeginPass);
		
		//	check("vertex buffer", this.vertexBuffers);
		
	}
}
