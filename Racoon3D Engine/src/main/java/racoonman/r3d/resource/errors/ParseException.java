package racoonman.r3d.resource.errors;

public class ParseException extends Exception {
	private static final long serialVersionUID = 2334861980242281843L;

	public ParseException() {
	}
	
	public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
