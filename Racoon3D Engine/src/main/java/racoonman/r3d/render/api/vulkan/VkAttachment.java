package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.TextureState;
import racoonman.r3d.render.api.vulkan.Image.ImageBuilder;
import racoonman.r3d.render.api.vulkan.ImageView.ImageViewBuilder;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.util.Util;

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
			.aspectMask(VkUtils.getAspect(usage).getVkType())
			.image(ImageBuilder.create()
				.width(width)
				.height(height)
				.usage(Util.add(usage, ImageUsage.SAMPLED, ImageUsage[]::new))
				.format(format)
				.arrayLayers(layers)
				.build(device))
			.viewType(viewType)
			.layerCount(layers)
			.build(device), state, device);
	}

	public VkAttachment(ImageView imageView, Device device) {
		this(imageView, TextureState.DEFAULT.copy(), device);
	}
	
	public VkAttachment(ImageView imageView, TextureState state, Device device) {
		this.width = imageView.getWidth();
		this.height = imageView.getHeight();
		this.layers = imageView.getLayerCount();
		this.format = imageView.getFormat();
		this.viewType = imageView.getViewType();
		this.usage = imageView.getUsage();
		this.format = imageView.getFormat();
		this.state = state;
		this.device = device;
		this.imageView = imageView;
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
	public IAttachment copy() {
		return new VkAttachment(this.width, this.height, this.layers, this.format, this.viewType, this.usage, this.state.copy(), this.device);
	}

	@Override
	public void free() {
		this.imageView.free();
		this.imageView.getImage().free();
	}
}
