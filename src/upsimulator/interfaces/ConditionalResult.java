package upsimulator.interfaces;

public interface ConditionalResult extends Condition, Result {

	@Override
	/**
	 * Deep clone this ConditionalResult
	 * 
	 * @return the cloned ConditionalResult
	 */
	public ConditionalResult deepClone();

	/**
	 * Get string of condition part
	 * 
	 * @return string
	 */
	public String toConditionString();

	/**
	 * Get string of result part
	 * 
	 * @return string
	 */
	public String toResultString();

}
