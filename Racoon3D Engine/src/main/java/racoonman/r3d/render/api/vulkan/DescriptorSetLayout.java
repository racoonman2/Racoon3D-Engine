package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateDescriptorSetLayout;
import static org.lwjgl.vulkan.VK10.vkDestroyDescriptorSetLayout;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

import racoonman.r3d.render.api.vulkan.types.DescriptorType;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.ICodec;
import racoonman.r3d.resource.codec.IField;
import racoonman.r3d.resource.codec.PrimitiveCodec;

public class DescriptorSetLayout {
	private Device device;
	private int flags;
	private DescriptorBinding[] bindings;
	private long handle;

	public DescriptorSetLayout(Device device, int flags, DescriptorBinding... bindings) {
		try (MemoryStack stack = stackPush()) {
			this.device = device;
			this.flags = flags;
			this.bindings = bindings;

			VkDescriptorSetLayoutBinding.Buffer bindingsBuf = VkDescriptorSetLayoutBinding.calloc(bindings.length, stack);
			for (int i = 0; i < bindings.length; i++) {
				DescriptorBinding binding = bindings[i];

				bindingsBuf.get(i)
					.binding(binding.binding())
					.descriptorType(binding.type().getVkType())
					.descriptorCount(binding.count())
					.stageFlags(IVkType.bitMask(binding.stageFlags()));
			}

			VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
				.pBindings(bindingsBuf)
				.flags(flags);

			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateDescriptorSetLayout(device.get(), info, null, pointer), "Error creating descriptor set layout");
			this.handle = pointer.get(0);
		}
	}

	public int getFlags() {
		return this.flags;
	}
	
	public DescriptorBinding[] getBindings() {
		return this.bindings;
	}

	public long getHandle() {
		return this.handle;
	}

	public void free() {
		vkDestroyDescriptorSetLayout(this.device.get(), this.handle, null);
	}
	
	public static ICodec<DescriptorSetLayout> codec(Device device) {
		return ICodec.simple(
			IField.refer("device", () -> device),
			PrimitiveCodec.INT.fetch("flags", DescriptorSetLayout::getFlags),
			ArrayCodec.of(DescriptorBinding.CODEC, DescriptorBinding[]::new).fetch("bindings", DescriptorSetLayout::getBindings),
			DescriptorSetLayout::new
		);
	}

	public static record DescriptorBinding(int binding, DescriptorType type, int count, ShaderStage[] stageFlags) {
		public static final ICodec<DescriptorBinding> CODEC = ICodec.simple(
			PrimitiveCodec.INT.fetch("binding", DescriptorBinding::binding),
			DescriptorType.ORDINAL_CODEC.fetch("type", DescriptorBinding::type),
			PrimitiveCodec.INT.fetch("count", DescriptorBinding::count),
			ArrayCodec.of(ShaderStage.ORDINAL_CODEC, ShaderStage[]::new).fetch("stage_flags", DescriptorBinding::stageFlags),
			DescriptorBinding::new
		);
	}
}
