package upsimulator.rules.conditions;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Represent minimum priority condition in P system.
 * <p>
 * Priority condition must not be put at the front of any object conditions in
 * multi-thread context. PriorityCondition must be put at the tail of
 * condition's list, or there will be some mistake.
 * 
 * @author quan
 *
 */
public class PriorityCondition implements Condition {

	private static boolean exist = false;// if exist PriorityCondition

	public static boolean exist() {
		return exist;
	}

	private static String priProp = "$pri";

	private int priority = 1;

	public PriorityCondition(int priority) {
		this.priority = priority;
		exist = true;
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
		return true;
	}

	/**
	 * If the highest priority equals to {@code priority}, this rule will fetch.
	 */
	@Override
	public boolean fetch(Membrane membrane) {
		return true;
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
		return " ," + priority;
	}

}
