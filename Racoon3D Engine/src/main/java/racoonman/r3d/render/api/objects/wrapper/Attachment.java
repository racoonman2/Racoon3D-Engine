package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.TextureState;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.ImageLayout;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.api.types.ViewType;
import racoonman.r3d.render.core.Driver;

public class Attachment implements IAttachment {
	private IAttachment delegate;
	
	public Attachment(int width, int height, int layers, ImageLayout layout, Format format, ViewType viewType, ImageUsage... usage) {
		this.delegate = Driver.createAttachment(width, height, layers, layout, format, viewType, usage);
	}

	@Override
	public int getWidth() {
		return this.delegate.getWidth();
	}

	@Override
	public int getHeight() {
		return this.delegate.getHeight();
	}

	@Override
	public int getLayerCount() {
		return this.delegate.getLayerCount();
	}

	@Override
	public Format getFormat() {
		return this.delegate.getFormat();
	}

	@Override
	public TextureState getState() {
		return this.delegate.getState();
	}

	@Override
	public void free() {
		this.delegate.free();
	}

	@Override
	public long asLong() {
		return this.delegate.asLong();
	}

	@Override
	public long getHandle() {
		return this.delegate.getHandle();
	}

	@Override
	public ImageUsage[] getUsage() {
		return this.delegate.getUsage();
	}

	@Override
	public int getMipLevels() {
		return this.delegate.getMipLevels();
	}

	@Override
	public IAttachment makeChild(int newWidth, int newHeight) {
		return this.delegate.makeChild(newWidth, newHeight);
	}
}
