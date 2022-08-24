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
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.api.objects.Scissor;
import racoonman.r3d.render.api.objects.Viewport;
import racoonman.r3d.render.api.objects.sync.GpuFence;
import racoonman.r3d.render.api.objects.sync.ImageFence;
import racoonman.r3d.render.api.vulkan.CommandBuffer.VertexBuffer;
import racoonman.r3d.render.api.vulkan.FrameManager.Frame;
import racoonman.r3d.render.api.vulkan.types.BindPoint;
import racoonman.r3d.render.api.vulkan.types.DescriptorType;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.IndexType;
import racoonman.r3d.render.api.vulkan.types.Stage;
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
		this.submission = QueueSubmission.of().withBuffers(cmdBuffer).withFence(this.frame.getFence());
		cmdBuffer.begin();
	}
	
	@Override
	public void viewport(Viewport viewport) {
		super.viewport(viewport);
		this.viewport.applyChanges((v) -> {
			try(MemoryStack stack = stackPush()) {
				VkViewport.Buffer viewports = VkViewport.calloc(1, stack);
				viewports.get(0)
					.x(0.0F)
					.y(v.height())
					.minDepth(v.minDepth())
					.maxDepth(v.maxDepth())
					.width(v.width())
					.height(-v.height());
				this.frame.getCommandBuffer().setViewport(0, viewports);
			}
		});
	}
	
	@Override
	public void scissor(Scissor scissor) {
		super.scissor(scissor);
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
	public void wait(Semaphore semaphore, Stage stage) {
		this.submission.withWait(semaphore, stage);
	}

	@Override
	public void signal(Semaphore semaphore) {
		this.submission.withSignal(semaphore);
	}

	@Override
	public void insert(GpuFence fence) {
		try(MemoryStack stack = stackPush()) {
			List<ImageFence> imgFences = fence.getImageFences();
			VkImageMemoryBarrier.Buffer barriers = VkImageMemoryBarrier.calloc(imgFences.size(), stack);
			
			for(int i = 0; i < imgFences.size(); i++) {
				ImageFence imgFence = imgFences.get(i);
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
						.layerCount(imgFence.getImage().getLayers())
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
	public void draw(int instanceCount, int start, int amount) {
		this.applyState();
		this.frame.getCommandBuffer().draw(amount, instanceCount, start, 0);
	}

	@Override
	public void drawIndexed(int instanceCount, int vertexStart, int indexStart, int amount) {
		this.applyState();
		this.frame.getCommandBuffer().drawIndexed(amount, instanceCount, vertexStart, indexStart, 0);
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
	
	private void applyState() {
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
			VertexBuffer[] vbs = new VertexBuffer[buffers.size()];
				
			for(int i = 0; i < vbs.length; i++) {
				IPair<VertexFormat, IDeviceBuffer> pair = buffers.get(i);
				vbs[i] = new VertexBuffer(pair.right(), 0);
			}
			
			cmdBuffer.bindVertexBuffers(0, vbs);
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
