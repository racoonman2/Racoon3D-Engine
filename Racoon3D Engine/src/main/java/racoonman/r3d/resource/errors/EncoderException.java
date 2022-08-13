package racoonman.r3d.resource.errors;

public class EncoderException extends RuntimeException {
	private static final long serialVersionUID = -1622126083353705362L;

	public EncoderException() {
		super();
	}

	public EncoderException(String message) {
		super(message);
	}

	public EncoderException(Throwable cause) {
		super(cause);
	}
	
	public static EncoderException unknownContainer() {
		return new EncoderException("Unknown container type");
	}
}
