package upsimulator.interfaces;

/**
 * Condition interface is used to describe the functions conditions need.
 * 
 * @author quan
 *
 */
public interface Condition extends Cloneable {

	/**
	 * check if condition satisfy the situation in membrane.
	 * 
	 * @param membrane
	 *            the membrane where to check the condition
	 * @return satisfied or not
	 */
	public boolean satisfy(Membrane membrane);

	/**
	 * fetch objects condition need from membrane
	 * 
	 * @param membrane
	 *            the membrane where to fetch objects
	 * @return success or not
	 */
	public boolean fetch(Membrane membrane);

	/**
	 * If some conditions in one rule cannot fetch the objects then the conditions
	 * which have fetched the objects they need withdraw the objects they have
	 * fetched.
	 * 
	 * @param membrane
	 *            the membrane current condition withdraw objects to
	 */
	public void withdrawFetch(Membrane membrane);

	/**
	 * Deep clone this condition
	 * 
	 * @return the cloned condition
	 */
	public Condition deepClone();
}
