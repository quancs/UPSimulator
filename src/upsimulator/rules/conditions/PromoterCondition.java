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

	public PromoterCondition(PObject obj) {
		super(obj);
	}

	public PromoterCondition(String type, String... dims) {
		super(type, dims);
	}

	public PromoterCondition() {
	}

	@Override
	public boolean satisfy(Membrane membrane) {
		if (membrane.getNumOf(this) > 0) {
			return true;
		} else
			return false;
	}

	@Override
	public boolean fetch(Membrane membrane) {
		// there is nothing need to fetch.
		return true;
	}

	@Override
	public void withdrawFetch(Membrane membrane) {
		// there is nothing need to withdrawFetch.
		return;
	}

	@Override
	public String toString() {
		return "@" + getNameDim();
	}

	@Override
	public PromoterCondition deepClone() {
		return (PromoterCondition) super.deepClone();
	}
}
