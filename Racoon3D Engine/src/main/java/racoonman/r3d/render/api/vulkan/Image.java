package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TILING_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.vkAllocateMemory;
import static org.lwjgl.vulkan.VK10.vkBindImageMemory;
import static org.lwjgl.vulkan.VK10.vkCreateImage;
import static org.lwjgl.vulkan.VK10.vkDestroyImage;
import static org.lwjgl.vulkan.VK10.vkFreeMemory;
import static org.lwjgl.vulkan.VK10.vkGetImageMemoryRequirements;
import static racoonman.r3d.render.api.vulkan.VkUtils.getMemType;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.SampleCount;

public class Image {
	private Device device;
	private Format format;
	private int mipLevels;
	private SampleCount sampleCount;
	private int arrayLayers;
	private ImageUsage[] usage;
	private int width;
	private int height;
	private int flags;
	private long handle;
	private long memory;

	public Image(Device device, Format format, int mipLevels, SampleCount sampleCount, int arrayLayers, ImageUsage[] usage, int width, int height, int flags, long handle) {
		this.device = device;
		this.format = format;
		this.mipLevels = mipLevels;
		this.sampleCount = sampleCount;
		this.arrayLayers = arrayLayers;
		this.usage = usage;
		this.width = width;
		this.height = height;
		this.flags = flags;
		this.handle = handle;
	}
	
	public Image(Device device, Format format, int mipLevels, SampleCount sampleCount, int arrayLayers, ImageUsage[] usage, int width, int height, int flags) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.format = format;
			this.mipLevels = mipLevels;
			this.sampleCount = sampleCount;
			this.arrayLayers = arrayLayers;
			this.usage = usage;
			this.width = width;
			this.height = height;
			this.flags = flags;
			
			VkImageCreateInfo info = VkImageCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
				.imageType(VK_IMAGE_TYPE_2D)
				.format(format.getVkType())
				.extent((it) -> it.width(width).height(height).depth(1))
				.mipLevels(mipLevels)
				.arrayLayers(arrayLayers)
				.samples(sampleCount.getVkType())
				.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
				.sharingMode(VK_SHARING_MODE_EXCLUSIVE)
				.tiling(VK_IMAGE_TILING_OPTIMAL)
				.flags(flags)
				.usage(IVkType.bitMask(usage));
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateImage(device.get(), info, null, pointer), "Error creating image");
			this.handle = pointer.get(0);

			VkDevice vkDevice = device.get();
			
			VkMemoryRequirements memReqs = VkMemoryRequirements.calloc(stack);
			vkGetImageMemoryRequirements(vkDevice, this.handle, memReqs);
			
			VkMemoryAllocateInfo memAlloc = VkMemoryAllocateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
				.allocationSize(memReqs.size())
				.memoryTypeIndex(getMemType(device.getPhysicalDevice(), memReqs.memoryTypeBits(), 0));
			
			vkAssert(vkAllocateMemory(vkDevice, memAlloc, null, pointer), "Error allocating memory");
			this.memory = pointer.get(0);
			
			vkAssert(vkBindImageMemory(vkDevice, this.handle, this.memory, 0), "Error binding image memory");
		}
	}

	public ImageUsage[] getUsage() {
		return this.usage;
	}
	
	public Format getFormat() {
		return this.format;
	}
	
	public int getMipLevels() {
		return this.mipLevels;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getArrayLayers() {
		return this.arrayLayers;
	}
	
	public long getHandle() {
		return this.handle;
	}
	
	public long getMemory() {
		return this.memory;
	}
	
	public Image copy() {
		return new Image(this.device, this.format, this.mipLevels, this.sampleCount, this.arrayLayers, this.usage, this.width, this.height, this.flags);
	}
	
	public void free() {
		VkDevice vkDevice = this.device.get();

		vkDestroyImage(vkDevice, this.handle, null);
		vkFreeMemory(vkDevice, this.memory, null);
	}
	
	public static class ImageBuilder {
		private Format format;
		private int mipLevels;
		private SampleCount sampleCount;
		private int arrayLayers;
		private ImageUsage[] usage;
		private int width;
		private int height;
		private int flags;
		
		private ImageBuilder() {
			this.format = Format.R8G8B8A8_SRGB;
			this.mipLevels = 1;
			this.sampleCount = SampleCount.COUNT_1;
			this.arrayLayers = 1;
		}

		public ImageBuilder arrayLayers(int arrayLayers) {
			this.arrayLayers = arrayLayers;
			return this;
		}
		
		public ImageBuilder format(Format format) {
			this.format = format;
			return this;
		}
		
		public ImageBuilder width(int width) { 
			this.width = width;
			return this;
		}
		
		public ImageBuilder height(int height) {
			this.height = height;
			return this;
		}
		
		public ImageBuilder flags(int flags) {
			this.flags = flags;
			return this;
		}
		
		public ImageBuilder mipLevels(int levels) {
			this.mipLevels = levels;
			return this;
		}
		
		public ImageBuilder sampleCount(SampleCount count) {
			this.sampleCount = count;
			return this;
		}
		
		public ImageBuilder usage(ImageUsage... usage) {
			this.usage = usage;
			return this;
		}
		
		public Image build(Device device) {
			return new Image(device, this.format, this.mipLevels, this.sampleCount, this.arrayLayers, this.usage, this.width, this.height, this.flags);
		}
		
		public static ImageBuilder create() {
			return new ImageBuilder();
		}
	}
}
