package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.render.TextureState;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.api.types.ViewType;
import racoonman.r3d.render.api.vulkan.ImageView.ImageViewBuilder;
import racoonman.r3d.render.api.vulkan.VkImage.ImageBuilder;
import racoonman.r3d.util.ArrayUtil;

class VkAttachment implements IAttachment {
	protected int width;
	protected int height;
	protected int layers;
	protected Format format;
	protected ViewType viewType;
	protected ImageUsage[] usage;
	protected TextureState state;
	protected Device device;
	protected ImageView imageView;

	public VkAttachment(int width, int height, int layers, Format format, ViewType viewType, ImageUsage[] usage, Device device) {
		this(width, height, layers, format, viewType, usage, TextureState.DEFAULT.copy(), device);
	}
	
	public VkAttachment(int width, int height, int layers, Format format, ViewType viewType, ImageUsage[] usage, TextureState state, Device device) {
		this(ImageViewBuilder.create()
			.format(format)
			.aspects(VkUtils.getAspect(usage))
			.image(ImageBuilder.create()
				.width(width)
				.height(height)
				.usage(ArrayUtil.add(usage, ImageUsage.SAMPLED))
				.format(format)
				.arrayLayers(layers)
				.build(device))
			.viewType(viewType)
			.layerCount(layers)
			.build(device), state);
	}

	public VkAttachment(ImageView imageView) {
		this(imageView, TextureState.DEFAULT.copy());
	}
	
	public VkAttachment(ImageView imageView, TextureState state) {
		this.width = imageView.getWidth();
		this.height = imageView.getHeight();
		this.layers = imageView.getLayerCount();
		this.format = imageView.getFormat();
		this.viewType = imageView.getViewType();
		this.usage = imageView.getUsage();
		this.format = imageView.getFormat();
		this.state = state;
		this.imageView = imageView;
		this.device = imageView.getDevice();
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getLayerCount() {
		return this.layers;
	}

	@Override
	public long asLong() {
		return this.imageView.asLong();
	}

	@Override
	public Format getFormat() {
		return this.format;
	}

	@Override
	public TextureState getState() {
		return this.state;
	}
	
	@Override
	public IAttachment copy(int newWidth, int newHeight) {
		return new VkAttachment(newWidth, newHeight, this.layers, this.format, this.viewType, this.usage, this.state.copy(), this.device);
	}

	@Override
	public long getHandle() {
		return this.imageView.getImage().getHandle();
	}

	@Override
	public ImageUsage[] getUsage() {
		return this.imageView.getUsage();
	}

	@Override
	public int getMipLevels() {
		return this.imageView.getMipLevels();
	}
	
	//TODO
	 /* this only compares formats because thats all that matters to the pipeline cache, however this ignores cases where it would actually be helpful to have a true comparison,
	 *  so the pipeline cache should use a different method for comparing attachments instead
	 */
	@Override
	public boolean equals(Object o) {
		return o == this ? true : o instanceof IAttachment other && other.getFormat().equals(this.format);
	}
	
	@Override
	public void free() {
		this.imageView.free();
		this.imageView.getImage().free();
	}
}
