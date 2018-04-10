package upsimulator.exceptions;

public class UnknownMembraneException extends Exception {
	private static final long serialVersionUID = -417203572128137441L;

	public UnknownMembraneException() {
		super();
	}

	public UnknownMembraneException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnknownMembraneException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownMembraneException(String message) {
		super(message);
	}

	public UnknownMembraneException(Throwable cause) {
		super(cause);
	}

}
