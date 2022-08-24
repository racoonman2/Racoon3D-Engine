package racoonman.r3d.render.api.objects;

public interface IAttachment extends ITexture {
	IAttachment makeChild(int newWidth, int newHeight);
	
	default IAttachment makeChild() {
		return this.makeChild(this.getWidth(), this.getHeight());
	}
}
