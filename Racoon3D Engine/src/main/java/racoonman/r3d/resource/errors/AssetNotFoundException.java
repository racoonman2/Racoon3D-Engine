package racoonman.r3d.resource.errors;

public class AssetNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -6484415445606532328L;

	public AssetNotFoundException() {
	}
	
	public AssetNotFoundException(String message) {
        super(message);
    }

    public AssetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
