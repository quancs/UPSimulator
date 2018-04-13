package upsimulator.recognizer.actions;

import java.util.concurrent.ConcurrentHashMap;

import upsimulator.core.PMembrane;
import upsimulator.interfaces.Membrane;

/**
 * Actions that need to be delayed, will be done after the document recognize
 * has been finished.
 * 
 * @author quan
 *
 */
public abstract class RecognizerAction {

	/**
	 * Membranes not ready
	 */
	protected static ConcurrentHashMap<String, Integer> unreadyMembranes = new ConcurrentHashMap<>();

	/**
	 * Initial action
	 */
	public abstract void init();

	/**
	 * Check if this action is ready to execute
	 * 
	 * @return {@code true} if ready, {@code false} if not
	 */
	public abstract boolean ready();

	/**
	 * Do action
	 */
	public abstract void doAction();

	/**
	 * Minus the unready count of one membrane
	 * 
	 * @param membrane
	 *            membrane which is ready
	 */
	protected void minusUnreadyCount(Membrane membrane) {
		for (; membrane != null;) {
			String memName = membrane.getName();
			minusUnreadyCount(memName);
			membrane = ((PMembrane) membrane).getParent();
		}
	}

	private void minusUnreadyCount(String memName) {
		Integer count = unreadyMembranes.get(memName);
		if (count != null) {
			if (count != 1)
				unreadyMembranes.put(memName, --count);
			else {
				unreadyMembranes.remove(memName);
				count--;
			}
		} else {
			throw new RuntimeException("Count error : " + memName);
		}
	}

	/**
	 * Add the unready count of one membrane
	 * 
	 * @param membrane
	 *            membrane which is not ready
	 */
	protected void addUnreadyCount(Membrane membrane) {
		for (; membrane != null;) {
			String memName = membrane.getName();
			addUnreadyCount(memName);
			membrane = ((PMembrane) membrane).getParent();
		}
	}

	private void addUnreadyCount(String memName) {
		Integer count = 0;
		if (unreadyMembranes.containsKey(memName))
			count = unreadyMembranes.get(memName);
		count++;
		unreadyMembranes.put(memName, count);
	}

	/**
	 * Check if one membrane is ready
	 * 
	 * @param membrane
	 *            membrane to check
	 * @return {@code true} if membrane is ready, {@code false} in reverse
	 */
	protected boolean ready(String memName) {
		if (unreadyMembranes.containsKey(memName)) {
			return false;
		} else {
			return true;
		}
	}
}
