package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TILING_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.vkDestroyImage;

import java.util.Optional;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageCreateInfo;

import racoonman.r3d.render.api.objects.IImage;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.ImageType;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.util.IPair;

class VkImage implements IImage {
	private Device device;
	private ImageType type;
	private Format format;
	private int mipLevels;
	private SampleCount sampleCount;
	private int layerCount;
	private ImageUsage[] usage;
	private int width;
	private int height;
	private int flags;
	private long handle;
	private Optional<Allocation> allocation;

	public VkImage(Device device, ImageType type, Format format, int mipLevels, SampleCount sampleCount, int layerCount, ImageUsage[] usage, int width, int height, int flags, long handle) {
		this.device = device;
		this.type = type;
		this.format = format;
		this.mipLevels = mipLevels;
		this.sampleCount = sampleCount;
		this.layerCount = layerCount;
		this.usage = usage;
		this.width = width;
		this.height = height;
		this.flags = flags;
		this.handle = handle;
		this.allocation = Optional.empty();
	}
	
	public VkImage(Device device, ImageType type, Format format, int mipLevels, SampleCount sampleCount, int layerCount, ImageUsage[] usage, int width, int height, int flags) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.format = format;
			this.mipLevels = mipLevels;
			this.sampleCount = sampleCount;
			this.layerCount = layerCount;
			this.usage = usage;
			this.width = width;
			this.height = height;
			this.flags = flags;
			
			VkImageCreateInfo info = VkImageCreateInfo.calloc(stack)
				.sType$Default()
				.imageType(type.getVkType())
				.format(format.getVkType())
				.extent((it) -> it.width(width).height(height).depth(1))
				.mipLevels(mipLevels)
				.arrayLayers(layerCount)
				.samples(sampleCount.getVkType())
				.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
				.sharingMode(VK_SHARING_MODE_EXCLUSIVE)
				.tiling(VK_IMAGE_TILING_OPTIMAL)
				.flags(flags)
				.usage(IVkType.bitMask(usage));
			VmaAllocationCreateInfo allocInfo = VmaAllocationCreateInfo.calloc(stack)
				.usage(VMA_MEMORY_USAGE_GPU_ONLY); //TODO make this configurable
			
			IPair<Allocation, Long> img =  device.getMemoryAllocator().createImage(info, allocInfo);
			this.allocation = Optional.of(img.left());
			this.handle = img.right();
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
	
	@Override
	public int getLayerCount() {
		return this.layerCount;
	}
	
	@Override
	public long getHandle() {
		return this.handle;
	}
	
	public VkImage copy() {
		return new VkImage(this.device, this.type, this.format, this.mipLevels, this.sampleCount, this.layerCount, this.usage, this.width, this.height, this.flags);
	}
	
	@Override
	public void copy(IMemoryCopier uploader, IImage image) {
	}
	
	public void free() {
		VkDevice vkDevice = this.device.get();

		vkDestroyImage(vkDevice, this.handle, null);
		this.allocation.ifPresent(Allocation::free);
	}
	
	//TODO remove
	public static class ImageBuilder {
		private ImageType type;
		private Format format;
		private int mipLevels;
		private SampleCount sampleCount;
		private int arrayLayers;
		private ImageUsage[] usage;
		private int width;
		private int height;
		private int flags;
		
		private ImageBuilder() {
			this.type = ImageType.TYPE_2D;
			this.format = Format.R8G8B8A8_SRGB;
			this.mipLevels = 1;
			this.sampleCount = SampleCount.COUNT_1;
			this.arrayLayers = 1;
		}
		
		public ImageBuilder type(ImageType type) {
			this.type = type;
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

		public ImageBuilder arrayLayers(int arrayLayers) {
			this.arrayLayers = arrayLayers;
			return this;
		}
		
		public ImageBuilder usage(ImageUsage... usage) {
			this.usage = usage;
			return this;
		}
		
		public VkImage build(Device device) {
			return new VkImage(device, this.type, this.format, this.mipLevels, this.sampleCount, this.arrayLayers, this.usage, this.width, this.height, this.flags);
		}
		
		public static ImageBuilder create() {
			return new ImageBuilder();
		}
	}
}
