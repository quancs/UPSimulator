package upsimulator.exceptions;

public class UnknownTargetMembraneException extends Exception {
	private static final long serialVersionUID = -417203572128137441L;

	public UnknownTargetMembraneException() {
		super();
	}

	public UnknownTargetMembraneException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnknownTargetMembraneException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownTargetMembraneException(String message) {
		super(message);
	}

	public UnknownTargetMembraneException(Throwable cause) {
		super(cause);
	}

}
