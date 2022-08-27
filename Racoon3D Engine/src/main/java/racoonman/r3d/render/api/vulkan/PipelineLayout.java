package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineLayout;
import static org.lwjgl.vulkan.VK10.vkDestroyPipelineLayout;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;

import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.ICodec;
import racoonman.r3d.resource.codec.IField;
import racoonman.r3d.resource.codec.PrimitiveCodec;

class PipelineLayout implements IHandle {
	private Device device;
	private DescriptorSetLayout[] layouts;
	private PushConstantRange[] pushRanges;
	private long handle;
	
	public PipelineLayout(Device device, DescriptorSetLayout[] layouts, PushConstantRange[] ranges) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.layouts = layouts;
			this.pushRanges = ranges;
			
			LongBuffer pLayouts = stack.mallocLong(layouts.length);
			
			for(int i = 0; i < layouts.length; i++) {
				pLayouts.put(i, layouts[i].asLong());
			}
			
			VkPushConstantRange.Buffer pushRanges = VkPushConstantRange.calloc(ranges.length, stack);
			
			for(int i = 0; i < ranges.length; i++) {
				PushConstantRange range = ranges[i];
				pushRanges.get(i)
					.stageFlags(IVkType.bitMask(range.stageFlags()))
					.offset(range.offset())
					.size(range.size());
			}
			
			VkPipelineLayoutCreateInfo layoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
				.pSetLayouts(pLayouts)
				.pPushConstantRanges(pushRanges);
	
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreatePipelineLayout(device.get(), layoutInfo, null, pointer), "Error creating pipeline layout");
			this.handle = pointer.get(0);
		}
	}
	
	public DescriptorSetLayout[] getDescriptorLayouts() {
		return this.layouts;
	}
	
	public PushConstantRange[] getPushConstantRanges() {
		return this.pushRanges;
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vkDestroyPipelineLayout(this.device.get(), this.handle, null);
	}
	
	public static record PushConstantRange(ShaderStage[] stageFlags, int offset, int size) {
		public static final ICodec<PushConstantRange> CODEC = ICodec.simple(
			ArrayCodec.of(ShaderStage.ORDINAL_CODEC, ShaderStage[]::new).fetch("stage_flags", PushConstantRange::stageFlags), 
			PrimitiveCodec.INT.fetch("offset", PushConstantRange::offset),
			PrimitiveCodec.INT.fetch("size", PushConstantRange::size),
			PushConstantRange::new
		);
	}
	
	public static ICodec<PipelineLayout> codec(Device device) {
		return ICodec.simple(
			IField.refer("device", () -> device),
			ArrayCodec.of(DescriptorSetLayout.codec(device), DescriptorSetLayout[]::new).fetch("descriptor_set_layouts", PipelineLayout::getDescriptorLayouts),
			ArrayCodec.of(PushConstantRange.CODEC, PushConstantRange[]::new).fetch("push_constant_ranges", PipelineLayout::getPushConstantRanges),
			PipelineLayout::new
		);
	}
}
