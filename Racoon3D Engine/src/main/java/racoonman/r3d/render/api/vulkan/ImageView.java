package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateImageView;
import static org.lwjgl.vulkan.VK10.vkDestroyImageView;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import racoonman.r3d.render.api.vulkan.types.Aspect;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.natives.IHandle;

class ImageView implements IHandle {
	private Device device;
	private VkImage image;
	private int aspectMask;
	private int baseMipLevel;
	private int baseArrayLevel;
	private Format format;
	private int layerCount;
	private int mipLevels;
	private ViewType viewType;
	private long handle;
	
	public ImageView(Device device, VkImage image, int aspectMask, int baseMipLevel, int baseArrayLayer, ViewType viewType) {
		this(device, image, aspectMask, baseMipLevel, baseArrayLayer, image.getFormat(), image.getLayers(), image.getMipLevels(), viewType);
	}	
	
	public ImageView(Device device, VkImage image, int aspectMask, int baseMipLevel, int baseArrayLayer, Format format, int layerCount, int mipLevels, ViewType viewType) {
		try(MemoryStack stack = stackPush()) {
			this.image = image;
			this.device = device;
			this.aspectMask = aspectMask;
			this.baseMipLevel = baseMipLevel;
			this.baseArrayLevel = baseArrayLayer;
			this.format = format;
			this.layerCount = layerCount;
			this.mipLevels = mipLevels;
			this.viewType = viewType;
			
			VkImageViewCreateInfo info = VkImageViewCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
				.image(image.getHandle())
				.viewType(viewType.getVkType())
				.format(format.getVkType())
				.subresourceRange((it) -> it
					.aspectMask(aspectMask)
					.baseMipLevel(baseMipLevel)
					.levelCount(mipLevels)
					.baseArrayLayer(baseArrayLayer)
					.layerCount(layerCount));
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateImageView(device.get(), info, null, pointer), "Error creating image view");
			this.handle = pointer.get(0);
		}
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	public int getWidth() {
		return this.image.getWidth();
	}
	
	public int getHeight() {
		return this.image.getHeight();
	}
	
	public VkImage getImage() {
		return this.image;
	}

	public int getAspectMask() {
		return this.aspectMask;
	}
	
	public Format getFormat() {
		return this.format;
	}
	
	public int getMipLevels() {
		return this.mipLevels;
	}

	public ViewType getViewType() {
		return this.viewType;
	}
	
	public ImageUsage[] getUsage() {
		return this.image.getUsage();
	}
	
	public int getLayerCount() {
		return this.layerCount;
	}
	
	public ImageView copy() {
		return new ImageView(this.device, this.image.copy(), this.aspectMask, this.baseMipLevel, this.baseArrayLevel, this.format, this.layerCount, this.mipLevels, this.viewType);
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vkDestroyImageView(this.device.get(), this.handle, null);
	}
	
	//TODO remove
	public static class ImageViewBuilder {
		private int aspectMask;
		private int baseMipLevel;
		private int baseArrayLayer;
		private Format format;
		private int layerCount;
		private int mipLevels;
		private ViewType viewType;
		private VkImage image;
		
		private ImageViewBuilder() {
			this.layerCount = 1;
			this.mipLevels = 1;
			this.viewType = ViewType.TYPE_2D;
		}
		
		public ImageViewBuilder aspects(Aspect... aspects) {
			this.aspectMask = IVkType.bitMask(aspects);
			return this;
		}
		
		public ImageViewBuilder baseMipLevel(int baseMipLevel) {
			this.baseMipLevel = baseMipLevel;
			return this;
		}
		
		public ImageViewBuilder baseArrayLayer(int layer) {
			this.baseArrayLayer = layer;
			return this;
		}
		
		public ImageViewBuilder format(Format format) {
			this.format = format;
			return this;
		}
		
		public ImageViewBuilder layerCount(int layerCount) {
			this.layerCount = layerCount;
			return this;
		}
		
		public ImageViewBuilder mipLevels(int levels) {
			this.mipLevels = levels;
			return this;
		}
		
		public ImageViewBuilder viewType(ViewType type) {
			this.viewType = type;
			return this;
		}
		
		public ImageViewBuilder image(VkImage image) {
			this.image = image;
			return this;
		}
		
		public ImageView build(Device device) {
			return new ImageView(device, this.image, this.aspectMask, this.baseMipLevel, this.baseArrayLayer, this.format, this.layerCount, this.mipLevels, this.viewType);
		}
		
		public static ImageViewBuilder create() {
			return new ImageViewBuilder();
		}
	}
	
}
