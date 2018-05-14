package upsimulator.interfaces;

/**
 * Conditions and results can get the process of membrane evolution by
 * implementing this interface
 * 
 * @author quan
 *
 */
public interface MembraneListener {

	/**
	 * Start to check all rules if some of them are satisfied
	 * 
	 * @param membrane
	 *            this membrane
	 */
	public void startChecking(Membrane membrane);

	/**
	 * End checking all rules if some of them are satisfied
	 * 
	 * @param membrane
	 *            this membrane
	 */
	public void endChecking(Membrane membrane);

	/**
	 * Start to fetch all the satisfied rules
	 * 
	 * @param membrane
	 *            this membrane
	 */
	public void startFetching(Membrane membrane);

	/**
	 * End fetching all the satisfied rules
	 * 
	 * @param membrane
	 *            this membrane
	 */
	public void endFetching(Membrane membrane);

	/**
	 * Start to set product in this membrane
	 * 
	 * @param membrane
	 *            this membrane
	 */
	public void startSetting(Membrane membrane);

	/**
	 * End setting product in this membrane include all its sub-membranes. Listeners
	 * only can be removed in this function
	 * 
	 * @param membrane
	 *            this membrane
	 */
	public void endSetting(Membrane membrane);
}