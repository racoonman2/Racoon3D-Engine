package racoonman.r3d.render.api.objects;

public interface IAttachment extends ITexture {
	IAttachment copy(int newWidth, int newHeight);
	
	default IAttachment copy() {
		return this.copy(this.getWidth(), this.getHeight());
	}
}
