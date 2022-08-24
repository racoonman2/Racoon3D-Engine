package racoonman.r3d.render.api.objects.sync;

import racoonman.r3d.render.api.objects.IImage;
import racoonman.r3d.render.api.vulkan.types.Access;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;

public class ImageFence {
	private int srcAccess;
	private int dstAccess;
	private ImageLayout oldLayout;
	private ImageLayout newLayout;
	private int srcQueueIndex;
	private int dstQueueIndex;
	private IImage image;
	private int baseLayer;
	private int baseMipLevel;
	
	public ImageFence() {
		this.srcQueueIndex = -1;
		this.dstQueueIndex = -1;
	}
	
	public int getSrcAccess() {
		return this.srcAccess;
	}
	
	public int getDstAccess() {
		return this.dstAccess;
	}
	
	public ImageLayout getOldLayout() {
		return this.oldLayout;
	}
	
	public ImageLayout getNewLayout() {
		return this.newLayout;
	}
	
	public int getSrcQueue() {
		return this.srcQueueIndex;
	}
	
	public int getDstQueue() {
		return this.dstQueueIndex;
	}
	
	public IImage getImage() {
		return this.image;
	}
	
	public int getBaseLayer() {
		return this.baseLayer;
	}
	
	public int getBaseMipLevel() {
		return this.baseMipLevel;
	}
	
	public ImageFence withSrc(Access... accesses) {
		for(Access access : accesses) {
			this.srcAccess |= access.getVkType();
		}
		return this;
	}
	
	public ImageFence withDst(Access... accesses) {
		for(Access access : accesses) {
			this.dstAccess |= access.getVkType();
		}
		return this;
	}
	
	public ImageFence withOld(ImageLayout layout) {
		this.oldLayout = layout;
		return this;
	}
	
	public ImageFence withNew(ImageLayout layout) {
		this.newLayout = layout;
		return this;
	}
	
	public ImageFence withSrcQueue(int queue) {
		this.srcQueueIndex = queue;
		return this;
	}
	
	public ImageFence withDstQueue(int queue) {
		this.dstQueueIndex = queue;
		return this;
	}
	
	public ImageFence withImage(IImage image) {
		this.image = image;
		return this;
	}
	
	public ImageFence withBase(int layer) {
		this.baseLayer = layer;
		return this;
	}
	
	public ImageFence withBaseMip(int level) {
		this.baseMipLevel = level;
		return this;
	}
	
	public static ImageFence of(IImage image) {
		return new ImageFence().withImage(image);
	}
}