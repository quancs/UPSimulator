package upsimulator.rules.conditions;

import upsimulator.core.PObject;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Inhibitor condition represents inhibitor of one rule
 * 
 * @author quan
 *
 */
public class InhibitorCondition extends PObject implements Condition {

	@Override
	public InhibitorCondition deepClone() {
		if (isFixed())
			return this;
		return new InhibitorCondition(this);
	}

	public InhibitorCondition(InhibitorCondition inhibitorCondition) {
		super(inhibitorCondition);
	}

	public InhibitorCondition(String type, String... dims) {
		super(type, dims);
	}

	public InhibitorCondition() {
	}

	@Override
	public int satisfy(Membrane membrane) {
		if ((Integer)membrane.getNumOf(this) == 0) {
			return Integer.MAX_VALUE;
		} else
			return 0;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		// there is nothing need to fetch.
		return times;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
		// there is nothing need to withdrawFetch.
		return;
	}

	@Override
	public String toString() {
		return "!" + getNameDim();
	}

}
