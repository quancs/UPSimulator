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
	 * @return satisfied times. return {@code 0} if not satisfied.
	 */
	public int satisfy(Membrane membrane);

	/**
	 * fetch objects from membrane
	 * 
	 * @param membrane
	 *            the membrane where to fetch objects
	 * @param tryTimes
	 *            how many times going to fetch
	 * @return the times successfully fetch
	 */
	public int fetch(Membrane membrane, int tryTimes);

	/**
	 * If some conditions in one rule cannot fetch the objects then the conditions
	 * which have fetched the objects they need withdraw the objects they have
	 * fetched.
	 * 
	 * @param membrane
	 *            the membrane current condition withdraw objects to
	 * @param times
	 *            withdraw fetch times
	 */
	public void withdrawFetch(Membrane membrane, int times);

	/**
	 * Deep clone this condition
	 * 
	 * @return the cloned condition
	 */
	public Condition deepClone();
}
