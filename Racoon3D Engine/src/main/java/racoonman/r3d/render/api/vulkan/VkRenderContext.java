package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.CommandBuffer.VertexBuffer;
import racoonman.r3d.render.api.vulkan.FrameManager.Frame;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.AssemblyInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.BlendAttachmentInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.BlendStateInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.DepthStencilInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.DynamicStateInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.MultisampleInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.RasterizationInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.RenderingInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.ViewportInfo;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.BlendFactor;
import racoonman.r3d.render.api.vulkan.types.BlendOp;
import racoonman.r3d.render.api.vulkan.types.ColorComponent;
import racoonman.r3d.render.api.vulkan.types.CompareOp;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.FrontFace;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.IndexType;
import racoonman.r3d.render.api.vulkan.types.LogicOp;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;
import racoonman.r3d.render.api.vulkan.types.PolygonMode;
import racoonman.r3d.render.api.vulkan.types.SampleCount;
import racoonman.r3d.render.api.vulkan.types.Topology;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

//TODO remember to make this class package only and make applyState() private
public class VkRenderContext extends RenderContext {
	private Device device;
	private WorkDispatcher dispatcher;
	private Frame frame;
	private CommandRecorder recorder;
	private RenderPass activePass;
	private boolean inPass;
	private Map<PipelineStage, Set<Semaphore>> waits;
	private Set<Semaphore> signals;
	private State<IPipeline> pipeline;
	
	public VkRenderContext(Device device, WorkDispatcher dispatcher, Frame frame) {
		this.device = device;
		this.dispatcher = dispatcher;
		this.frame = frame;
		this.recorder = new CommandRecorder(this);
		this.waits = new HashMap<>();
		this.signals = new HashSet<>();
		this.pipeline = new State<>();
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
		//	this.program.check("program");
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

		this.pipeline.set(this.getPipelineFromState());
		this.pipeline.applyChanges((pipeline) -> {
			this.recorder.record((cmdBuffer) -> {
				cmdBuffer.bindPipeline(pipeline);
			});
		});
	}
	
	private IPipeline getPipelineFromState() {
		return new GraphicsPipeline(this.device, 
			this.getProgram(), 
			new VertexInfo(this.vertexBuffers.getValues().stream().map(IPair::left).toArray(VertexFormat[]::new)),
			new AssemblyInfo(Topology.TRIANGLE_LIST),
			new ViewportInfo(1, 1),
			new RasterizationInfo(PolygonMode.FILL, this.getCullMode(), this.getLineWidth(), FrontFace.CW), 
			new MultisampleInfo(SampleCount.COUNT_1), 
			new BlendStateInfo[] {
				new BlendStateInfo(
					IVkType.bitMask(ColorComponent.values()), 
					false, 
					BlendOp.ADD, 
					BlendFactor.ONE_MINUS_SRC_COLOR, 
					BlendFactor.ONE_MINUS_DST_COLOR, 
					BlendOp.ADD, 
					BlendFactor.ONE_MINUS_SRC_ALPHA, 
					BlendFactor.ONE_MINUS_DST_ALPHA)
			}, 
			new BlendAttachmentInfo(false, LogicOp.AND, new float[0]),
			new DynamicStateInfo(new int[] {VK13.VK_DYNAMIC_STATE_VIEWPORT, VK13.VK_DYNAMIC_STATE_SCISSOR}),
			Optional.of(new DepthStencilInfo(false, false, CompareOp.NEVER, false, false)),
			Optional.empty(), 
			new RenderingInfo(this.framebuffer.getValue().getColorAttachments().stream().map(IAttachment::getFormat).toArray(Format[]::new), Format.D32_SFLOAT_S8_UINT, Format.D32_SFLOAT_S8_UINT), 
			new PipelineLayout(this.device));
	}
}
