package upsimulator.exceptions;

/**
 * Unpredictable dimension: formula dimensions of one rule appear in the
 * {@code Inhibitor} only, then this dimension cannot be predicted
 * 
 * @author quan
 *
 */
public class UnpredictableDimensionException extends Exception {
	public UnpredictableDimensionException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
