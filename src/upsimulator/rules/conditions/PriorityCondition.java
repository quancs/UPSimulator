package upsimulator.rules.conditions;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Priority condition must not be put at the front of any object conditions in
 * multi-thread context. PriorityCondition must be put at the tail of
 * condition's list, or there will be some mistake.
 * 
 * @author quan
 *
 */
public class PriorityCondition implements Condition {
	private static String priProp = "$pri";

	private int priority = 1;

	public PriorityCondition(int priority) {
		this.priority = priority;
	}

	@Override
	public PriorityCondition deepClone() {
		return this;
	}

	/**
	 * Save the highest priority.
	 */
	@Override
	public boolean satisfy(Membrane membrane) {
		Integer priHighest = (Integer) membrane.getProperty(priProp);
		if (priHighest == null || priHighest > priority) {
			synchronized (membrane.getProperties()) {
				priHighest = (Integer) membrane.getProperty(priProp);
				if (priHighest == null || priHighest > priority) {
					membrane.setProperty(priProp, priority);
					return true;
				} else if (priHighest < priority) {
					return false;
				} else {
					return true;
				}
			}
		} else if (priHighest < priority) {
			return false;
		} else {// priHighest == priority
			return true;
		}
	}

	/**
	 * If the highest priority equals to {@code priority}, this rule will fetch.
	 */
	@Override
	public boolean fetch(Membrane membrane) {
		Integer priHighest = (Integer) membrane.getProperty(priProp);
		if (priHighest == priority) {
			return true;
		} else if (priHighest < priority) {
			return false;
		} else {
			throw new RuntimeException("Highest priority is less than the priority of this rule");
		}
	}

	@Override
	public void withdrawFetch(Membrane membrane) {

	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "pri=" + priority;
	}

}
