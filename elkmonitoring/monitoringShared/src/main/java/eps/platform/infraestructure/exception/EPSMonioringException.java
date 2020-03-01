package eps.platform.infraestructure.exception;

public class EPSMonioringException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7718828512143293558L;
	private final ErrorCode code;

	public EPSMonioringException(ErrorCode code) {
		super();
		this.code = code;
	}

	public EPSMonioringException(String message, Throwable cause, ErrorCode code) {
		super(message, cause);
		this.code = code;
	}

	public EPSMonioringException(String message, ErrorCode code) {
		super(message);
		this.code = code;
	}

	public EPSMonioringException(Throwable cause, ErrorCode code) {
		super(cause);
		this.code = code;
	}
	
	public ErrorCode getCode() {
		return this.code;
	}
}