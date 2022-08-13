package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineLayout;
import static org.lwjgl.vulkan.VK10.vkDestroyPipelineLayout;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;

import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.ICodec;
import racoonman.r3d.resource.codec.IField;

public class PipelineLayout implements IHandle {
	private Device device;
	private DescriptorSetLayout[] layouts;
	private long handle;
	
	public PipelineLayout(Device device, DescriptorSetLayout... layouts) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.layouts = layouts;
			
			LongBuffer pLayouts = stack.mallocLong(layouts.length);
			
			for(int i = 0; i < layouts.length; i++) {
				pLayouts.put(i, layouts[i].asLong());
			}
			
			VkPipelineLayoutCreateInfo layoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
				.pSetLayouts(pLayouts);
	
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreatePipelineLayout(device.get(), layoutInfo, null, pointer), "Error creating pipeline layout");
			this.handle = pointer.get(0);
		}
	}
	
	public DescriptorSetLayout[] getDescriptorLayouts() {
		return this.layouts;
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vkDestroyPipelineLayout(this.device.get(), this.handle, null);
	}
	
	public static ICodec<PipelineLayout> codec(Device device) {
		return ICodec.simple(
			IField.refer("device", () -> device),
			ArrayCodec.of(DescriptorSetLayout.codec(device), DescriptorSetLayout[]::new).fetch("descriptor_set_layouts", PipelineLayout::getDescriptorLayouts),
			PipelineLayout::new
		);
	}
}
