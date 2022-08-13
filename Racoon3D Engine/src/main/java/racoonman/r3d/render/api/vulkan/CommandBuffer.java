package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_SECONDARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkBeginCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdBindIndexBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdBindPipeline;
import static org.lwjgl.vulkan.VK10.vkCmdBindVertexBuffers;
import static org.lwjgl.vulkan.VK10.vkCmdCopyBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdDraw;
import static org.lwjgl.vulkan.VK10.vkCmdDrawIndexed;
import static org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier;
import static org.lwjgl.vulkan.VK10.vkCmdSetScissor;
import static org.lwjgl.vulkan.VK10.vkCmdSetViewport;
import static org.lwjgl.vulkan.VK10.vkEndCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkFreeCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkResetCommandBuffer;
import static org.lwjgl.vulkan.VK13.vkCmdBeginRendering;
import static org.lwjgl.vulkan.VK13.vkCmdEndRendering;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferMemoryBarrier;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkMemoryBarrier;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderingInfo;
import org.lwjgl.vulkan.VkViewport;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BindPoint;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.IndexType;
import racoonman.r3d.render.natives.IHandle;

public class CommandBuffer implements IHandle {
	private CommandPool pool;
	private SubmitMode usage;
	private VkCommandBuffer buffer;
	private boolean recording;
	
	public CommandBuffer(CommandPool pool, Level level, SubmitMode usage) {
		try(MemoryStack stack = stackPush()) {
			this.pool = pool;
			this.usage = usage;
			
			VkDevice device = pool.getDevice().get();
			
			VkCommandBufferAllocateInfo info = VkCommandBufferAllocateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
				.commandPool(pool.asLong())
				.level(level.getVkType())
				.commandBufferCount(1);
			PointerBuffer pointer = stack.mallocPointer(1);
			vkAssert(vkAllocateCommandBuffers(device, info, pointer), "Error allocating command buffer");
			this.buffer = new VkCommandBuffer(pointer.get(0), device);
		}
	}

	public VkCommandBuffer get() {
		return this.buffer;
	}

	@Override
	public long asLong() {
		return this.buffer.address();
	}
	
	public void begin() {
		if(!this.recording) {
			try(MemoryStack stack = stackPush()) {
				VkCommandBufferBeginInfo info = VkCommandBufferBeginInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
					.flags(this.usage.getVkType());
				vkAssert(vkBeginCommandBuffer(this.buffer, info), "Error beginning command buffer recording");
			}
			
			this.recording = true;
		}
	}
	
	public void end() {
		if(this.recording) {
			vkAssert(vkEndCommandBuffer(this.buffer), "Error ending command buffer recording");
			this.recording = false;
		}
	}
	
	public void reset() {
		vkResetCommandBuffer(this.buffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
	}
	
	@Override
	public void free() {
		vkFreeCommandBuffers(this.pool.getDevice().get(), this.pool.asLong(), this.buffer);
	}
	
	public void draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
		vkCmdDraw(this.buffer, vertexCount, instanceCount, firstVertex, firstInstance);
	}
	
	public void bindVertexBuffers(int firstBinding, LongBuffer buffers, LongBuffer offsets) {
		vkCmdBindVertexBuffers(this.buffer, firstBinding, buffers, offsets);
	}
	
	public void beginRendering(VkRenderingInfo info) {
		vkCmdBeginRendering(this.buffer, info);
	}
	
	public void endRendering() {
		vkCmdEndRendering(this.buffer);
	}
	
	public void setViewport(int first, VkViewport.Buffer viewports) {
		vkCmdSetViewport(this.buffer, first, viewports);
	}
	
	public void setScissor(int first, VkRect2D.Buffer scissors) {
		vkCmdSetScissor(this.buffer, first, scissors);
	}
	
	public void bindIndexBuffer(IDeviceBuffer buffer, int offset, IndexType indexType) {
		vkCmdBindIndexBuffer(this.buffer, buffer.asLong(), offset, indexType.getVkType());
	}
	
	public void drawIndexed(int indexCount, int instanceCount, int firstIndex, int vertexOffset, int firstInstance) {
		vkCmdDrawIndexed(this.buffer, indexCount, instanceCount, firstIndex, vertexOffset, firstInstance);
	}
	
	public void pipelineBarrier(int srcStageMask, int dstStageMask, int dependencyFlags, VkMemoryBarrier.Buffer memoryBarriers, VkBufferMemoryBarrier.Buffer bufferMemoryBarriers, VkImageMemoryBarrier.Buffer imageMemoryBarriers) {
		vkCmdPipelineBarrier(this.buffer, srcStageMask, dstStageMask, dependencyFlags, memoryBarriers, bufferMemoryBarriers, imageMemoryBarriers);
	}
	
	public void copyBuffer(IDeviceBuffer src, IDeviceBuffer dst, VkBufferCopy.Buffer copy) {
		vkCmdCopyBuffer(this.buffer, src.asLong(), dst.asLong(), copy);
	}
	
	public void bindPipeline(IPipeline pipeline, BindPoint bindPoint) {
		vkCmdBindPipeline(this.buffer, bindPoint.getVkType(), pipeline.asLong());
	}
	
	public static enum Level implements IVkType {
		PRIMARY(VK_COMMAND_BUFFER_LEVEL_PRIMARY),
		SECONDARY(VK_COMMAND_BUFFER_LEVEL_SECONDARY);
		
		private int vkType;
		
		private Level(int vkType) {
			this.vkType = vkType;
		}
		
		@Override
		public int getVkType() {
			return this.vkType;
		}
	}
	
	public static enum SubmitMode implements IVkType {
		SINGLE(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT),
		CONTINUE(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT),
		ASYNC(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
		
		private int vkType;
		
		private SubmitMode(int vkType) {
			this.vkType = vkType;
		}
		
		@Override
		public int getVkType() {
			return this.vkType;
		}
	}
}