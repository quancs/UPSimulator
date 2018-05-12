package upsimulator.rules.conditions;

import upsimulator.core.PObject;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Represents the promoter condition in one rule
 * 
 * @author quan
 *
 */
public class PromoterCondition extends PObject implements Condition {

	public PromoterCondition(PromoterCondition promoterCondition) {
		super(promoterCondition);
	}

	public PromoterCondition(String type, String... dims) {
		super(type, dims);
	}

	public PromoterCondition() {
	}

	@Override
	public int satisfy(Membrane membrane) {
		if ((Integer)membrane.getNumOf(this) > 0) {
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
		return "@" + getNameDim();
	}

	@Override
	public PromoterCondition deepClone() {
		if (isFixed())
			return this;
		return new PromoterCondition(this);
	}
}
