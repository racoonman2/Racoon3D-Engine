package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_PREINITIALIZED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

//TODO add layouts from VK12 and VK13
public enum ImageLayout implements IVkType {
	UNDEFINED(VK_IMAGE_LAYOUT_UNDEFINED),
	GENERAL(VK_IMAGE_LAYOUT_GENERAL),
	COLOR_OPTIMAL(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL),
	DEPTH_STENCIL_OPTIMAL(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL),
	DEPTH_STENCIL_READ_ONLY_OPTIMAL(VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL),
	SHADER_READ_ONLY_OPTIMAL(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL),
	TRANSFER_SRC_OPTIMAL(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL),
	TRANSFER_DST_OPTIMAL(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL),
	PREINIT(VK_IMAGE_LAYOUT_PREINITIALIZED),
	PRESENT(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

	public static final ICodec<ImageLayout> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<ImageLayout> NAME_CODEC = EnumCodec.byName(ImageLayout::valueOf);
	
	private int vkType;
	
	private ImageLayout(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
