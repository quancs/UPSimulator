package upsimulator.exceptions;

public class UnsupportedDimensionException extends RuntimeException {
	private static final long serialVersionUID = -4621988120905249564L;

	private Object object;
	private Object dimension;

	public UnsupportedDimensionException(Object object, Object dimension) {
		super("Unsupported dimension " + dimension.toString() + " is going to be added to " + object.toString());
		this.object = object;
		this.dimension = dimension;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getDimension() {
		return dimension;
	}

	public void setDimension(Object dimension) {
		this.dimension = dimension;
	}
}
