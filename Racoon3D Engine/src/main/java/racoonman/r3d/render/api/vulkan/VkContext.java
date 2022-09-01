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
import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.api.sync.LocalSync;
import racoonman.r3d.render.api.sync.LocalSync.ImageSync;
import racoonman.r3d.render.api.types.BindPoint;
import racoonman.r3d.render.api.types.Dependency;
import racoonman.r3d.render.api.types.DescriptorType;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.IndexType;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.render.api.vulkan.FrameManager.Frame;
import racoonman.r3d.render.compute.Compute;
import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.state.uniform.UniformBuffer;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.util.IPair;

class VkContext extends Context {
	private GraphicsCache graphicsCache;
	private ComputeCache computeCache;
	private VkWorkPool pool;
	private Frame frame;
	private State<Pipeline> graphicsPipeline;
	private State<Pipeline> computePipeline;
	private UniformBuffer uniformBuffer;
	private Matrix4fStack proj;
	private Matrix4fStack view;
	private Matrix4fStack model;
	private Matrix4f lastProj;
	private Matrix4f lastView;
	private Matrix4f lastModel;
	private boolean needsStateUpdate;
	private QueueSubmission submission;
	
	public VkContext(GraphicsCache graphicsCache, ComputeCache computeCache, VkWorkPool pool, Frame frame, UniformBuffer uniformBuffer) {
		this.graphicsCache = graphicsCache;
		this.computeCache = computeCache;
		this.pool = pool;
		this.frame = frame;
		this.graphicsPipeline = new State<>();
		this.computePipeline = new State<>();
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
			.withHostSync(this.frame.getHostSync());
		cmdBuffer.begin();
	}
	
	@Override
	public void setViewport(Viewport viewport) {
		super.setViewport(viewport);
		this.viewport.update((v) -> {
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
		this.scissor.update((s) -> {
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
	public void await(IDeviceSync sync, Stage stage) {
		this.submission.withWait(sync, stage);
	}
	
	@Override
	public void alert(IDeviceSync... syncs) {
		this.submission.withSignal(syncs);
	}

	@Override
	public void await(LocalSync sync) {
		try(MemoryStack stack = stackPush()) {
			List<ImageSync> imageSyncs = sync.getImageSync();
			VkImageMemoryBarrier.Buffer barriers = VkImageMemoryBarrier.calloc(imageSyncs.size(), stack);
			
			for(int i = 0; i < imageSyncs.size(); i++) {
				ImageSync imageSync = imageSyncs.get(i);
				barriers.get(i)
					.sType$Default()
					.srcAccessMask(imageSync.getSrcAccess())
					.dstAccessMask(imageSync.getDstAccess())
					.oldLayout(imageSync.getOldLayout().getVkType())
					.newLayout(imageSync.getNewLayout().getVkType())
					.srcQueueFamilyIndex(imageSync.getSrcQueue())
					.dstQueueFamilyIndex(imageSync.getDstQueue())
					.image(imageSync.getImage().getHandle())
					.subresourceRange((range) -> range
						.aspectMask(IVkType.bitMask(VkUtils.getAspect(imageSync.getImage().getUsage())))
						.baseArrayLayer(imageSync.getBaseLayer())
						.baseMipLevel(imageSync.getBaseMipLevel())
						.layerCount(imageSync.getImage().getLayerCount())
						.levelCount(imageSync.getImage().getMipLevels()));
				}

			this.frame.getCommandBuffer().pipelineBarrier(
				sync.getSrcStages(), 
				sync.getDstStages(),
				Dependency.REGION.getVkType(), 
				null, 
				null, 
				barriers
			);
		}
	}

	@Override
	public Compute dispatch(IShader shader, int xThreads, int yThreads, int zThreads) {
		CommandBuffer buffer = this.frame.getCommandBuffer();
		this.computePipeline.set(this.computeCache.getPipeline(new ComputeState(shader)));
		this.computePipeline.update((p) -> buffer.bindPipeline(BindPoint.COMPUTE, p));
		buffer.dispatch(xThreads, yThreads, zThreads);
		return new Compute(this);
	}

	@Override
	protected void submit() {
		this.frame.getCommandBuffer().end();

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
			this.graphicsCache.getPipeline(this.makeState()).ifPresent((pipeline) -> {
				this.graphicsPipeline.set(pipeline);
				this.graphicsPipeline.update((p) -> cmdBuffer.bindPipeline(BindPoint.GRAPHICS, p));

				PipelineLayout layout = pipeline.getLayout();

				this.program.update((program) -> {
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
				
		this.vertexBuffers.update((buffers) -> {
			cmdBuffer.bindVertexBuffers(0, buffers.stream().map((pair) -> IPair.of(pair.right(), 0L)).toList());
		});
		
		this.indexBuffer.update((buffer) -> {
			cmdBuffer.bindIndexBuffer(buffer, 0, IndexType.UINT32);
		});
		
		this.lastProj.set(this.proj);
		this.lastView.set(this.view);
		this.lastModel.set(this.model);
	}
	
	private GraphicsState makeState() {
		return new GraphicsState(
			this.getProgram(),
			this.vertexBuffers.getValue().stream().map(IPair::left).toArray(VertexFormat[]::new),
			this.getMode(),
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
