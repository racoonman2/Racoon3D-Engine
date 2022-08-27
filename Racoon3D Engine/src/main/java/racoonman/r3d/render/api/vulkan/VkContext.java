package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.List;
import java.util.Optional;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.Scissor;
import racoonman.r3d.render.Viewport;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.api.sync.LocalSync;
import racoonman.r3d.render.api.sync.LocalSync.ImageSync;
import racoonman.r3d.render.api.types.BindPoint;
import racoonman.r3d.render.api.types.DescriptorType;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.IndexType;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.render.api.vulkan.CommandBuffer.VertexBuffer;
import racoonman.r3d.render.api.vulkan.FrameManager.Frame;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.state.uniform.UniformBuffer;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

class VkContext extends Context {
	private RenderCache cache;
	private WorkPool pool;
	private Frame frame;
	private State<IPipeline> pipeline;
	private UniformBuffer uniformBuffer;
	private Matrix4fStack proj;
	private Matrix4fStack view;
	private Matrix4fStack model;
	private Matrix4f lastProj;
	private Matrix4f lastView;
	private Matrix4f lastModel;
	private boolean needsStateUpdate;
	private QueueSubmission submission;
	
	public VkContext(RenderCache cache, WorkPool pool, Frame frame, UniformBuffer uniformBuffer) {
		this.cache = cache;
		this.pool = pool;
		this.frame = frame;
		this.pipeline = new State<>();
		this.uniformBuffer = uniformBuffer;
		this.proj = this.getMatrix(IMatrixType.PROJECTION);
		this.view = this.getMatrix(IMatrixType.VIEW);
		this.model = this.getMatrix(IMatrixType.MODEL);
		this.lastProj = invalid();
		this.lastView = invalid();
		this.lastModel = invalid();
		
		CommandBuffer cmdBuffer = this.frame.getCommandBuffer();
		this.submission = QueueSubmission.of()
			.withBuffers(cmdBuffer)
			.withFence(this.frame.getFence());
		cmdBuffer.begin();
	}
	
	@Override
	public void setViewport(Viewport viewport) {
		super.setViewport(viewport);
		this.viewport.applyChanges((v) -> {
			try(MemoryStack stack = stackPush()) {
				VkViewport.Buffer viewports = VkViewport.calloc(1, stack);
				viewports.get(0)
					.x(v.x())
					.y(v.y())
					.width(v.width())
					.height(v.height())
					.minDepth(v.minDepth())
					.maxDepth(v.maxDepth());
				this.frame.getCommandBuffer().setViewport(0, viewports);
			}
		});
	}
	
	@Override
	public void setScissor(Scissor scissor) {
		super.setScissor(scissor);
		this.scissor.applyChanges((s) -> {
			try(MemoryStack stack = stackPush()) {
				VkRect2D.Buffer scissors = VkRect2D.calloc(1, stack);
				scissors.get(0)
					.extent((extent) -> extent
						.width(s.width())
						.height(s.height()))
					.offset((offset) -> offset
						.x(s.x())
						.y(s.y()));
				this.frame.getCommandBuffer().setScissor(0, scissors);
			}	
		});
	}

	@Override
	public RenderPass createPass(IFramebuffer framebuffer) {
		return (this.pass = Optional.of(new VkRenderPass(this, this.frame.getCommandBuffer(), framebuffer))).get();
	}

	@Override
	public void sync(IContextSync sync, Stage stage) {
		this.submission.withWait(sync, stage);
	}

	@Override
	public void alert(IContextSync... syncs) {
		this.submission.withSignal(syncs);
	}

	@Override
	public void sync(LocalSync fence) {
		try(MemoryStack stack = stackPush()) {
			List<ImageSync> imgFences = fence.getImageSync();
			VkImageMemoryBarrier.Buffer barriers = VkImageMemoryBarrier.calloc(imgFences.size(), stack);
			
			for(int i = 0; i < imgFences.size(); i++) {
				ImageSync imgFence = imgFences.get(i);
				barriers.get(i)
					.sType$Default()
					.srcAccessMask(imgFence.getSrcAccess())
					.dstAccessMask(imgFence.getDstAccess())
					.oldLayout(imgFence.getOldLayout().getVkType())
					.newLayout(imgFence.getNewLayout().getVkType())
					.srcQueueFamilyIndex(imgFence.getSrcQueue())
					.dstQueueFamilyIndex(imgFence.getDstQueue())
					.image(imgFence.getImage().getHandle())
					.subresourceRange((range) -> range
						.aspectMask(IVkType.bitMask(VkUtils.getAspect(imgFence.getImage().getUsage())))
						.baseArrayLayer(imgFence.getBaseLayer())
						.baseMipLevel(imgFence.getBaseMipLevel())
						.layerCount(imgFence.getImage().getLayerCount())
						.levelCount(imgFence.getImage().getMipLevels()));
				}
				
			this.frame.getCommandBuffer().pipelineBarrier(
				fence.getSrcStages(), 
				fence.getDstStages(),
				fence.getDependencies(), 
				null, 
				null, 
				barriers
			);
		}
	}

	@Override
	public void submit() {
		this.applyState();
		
		CommandBuffer cmdBuffer = this.frame.getCommandBuffer();
		cmdBuffer.end();

		this.pool.submit(this.submission);
	}

	@Override
	public void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		VkUtils.copy(this.frame.getCommandBuffer(), src, dst);
	}

	@Override
	protected void updateState() {
		this.needsStateUpdate = true;
	}
	
	void applyState() {
		CommandBuffer cmdBuffer = this.frame.getCommandBuffer();
		
		if(this.needsStateUpdate) {
			this.cache.get(this.makeState()).ifPresent((pipeline) -> {
				this.pipeline.set(pipeline);
				this.pipeline.applyChanges(cmdBuffer::bindPipeline);

				PipelineLayout layout = pipeline.getLayout();

				this.program.applyChanges((program) -> {
					IDeviceBuffer buffer = this.uniformBuffer.getDeviceBuffer(); 

					try(MemoryStack stack = stackPush()) {
						VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc(1, stack);
						writes.apply(0, (write) -> write
							.sType$Default()
							.descriptorType(DescriptorType.UNIFORM_BUFFER.getVkType())
							.descriptorCount(1)
							.pBufferInfo(VkDescriptorBufferInfo.calloc(1, stack)
								.buffer(buffer.asLong())
								.offset(0)
								.range(128)));
							
						cmdBuffer.pushDescriptor(BindPoint.GRAPHICS, layout, 0, writes);
					}
				});
				
				try(MemoryStack stack = stackPush()) {
					if(!this.lastProj.equals(this.proj)) {
						this.uniformBuffer.putAt(0, this.proj);
					}
					
					if(!this.lastView.equals(this.view)) {
						this.uniformBuffer.putAt(64, this.view);
					}

					if(!this.lastModel.equals(this.model)) {
						cmdBuffer.pushConstants(layout, IVkType.bitMask(ShaderStage.VERTEX), 0, this.model.get(stack.malloc(64)));
					}
				}
			});
			
			this.needsStateUpdate = false;	
		}
				
		this.vertexBuffers.applyChanges((buffers) -> {
			VertexBuffer[] vertexBuffers = new VertexBuffer[buffers.size()];
				
			for(int i = 0; i < vertexBuffers.length; i++) {
				IPair<VertexFormat, IDeviceBuffer> pair = buffers.get(i);
				vertexBuffers[i] = new VertexBuffer(pair.right(), 0);
			}
			
			cmdBuffer.bindVertexBuffers(0, vertexBuffers);
		});
		
		this.indexBuffer.applyChanges((buffer) -> {
			cmdBuffer.bindIndexBuffer(buffer, 0, IndexType.UINT32);
		});
		
		this.lastProj.set(this.proj);
		this.lastView.set(this.view);
		this.lastModel.set(this.model);
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
			this.getWriteMask(),
			this.pass.orElseThrow().getFramebuffer()
		);
	}
	
	private static Matrix4f invalid() {
		return new Matrix4f(
			Float.NaN, Float.NaN, Float.NaN, Float.NaN,
			Float.NaN, Float.NaN, Float.NaN, Float.NaN,
			Float.NaN, Float.NaN, Float.NaN, Float.NaN,
			Float.NaN, Float.NaN, Float.NaN, Float.NaN
		);
	}
}
