package racoonman.r3d.resource.errors;

public class DecoderException extends RuntimeException {
	private static final long serialVersionUID = -1622126083353705362L;

	public DecoderException() {
        super();
    }

    public DecoderException(String message) {
        super(message);
    }
	
	public DecoderException(Throwable cause) {
		super(cause);
	}
	
	public static DecoderException invalidType() {
		return new DecoderException("Element is of invalid type");
	}
}
