package upsimulator.exceptions;

public class UnsupportedDataTypeException extends RuntimeException {
	private static final long serialVersionUID = -6360378767769786150L;

	private String wantedType, type;

	public UnsupportedDataTypeException(String msg, String wantedType, String type) {
		super(msg + " Wanted: " + wantedType + " Recieved:" + type);
		this.wantedType = wantedType;
		this.type = type;
	}

}
