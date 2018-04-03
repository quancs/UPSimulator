package upsimulator.rules.conditions;

import upsimulator.core.PObject;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

public class InhibitorCondition extends PObject implements Condition {

	@Override
	public InhibitorCondition deepClone() {
		InhibitorCondition cloned = new InhibitorCondition(super.deepClone());
		return cloned;
	}

	public InhibitorCondition(PObject object) {
		super(object);
	}

	public InhibitorCondition(String type, String... dims) {
		super(type, dims);
	}

	public InhibitorCondition() {
	}

	@Override
	public boolean satisfy(Membrane membrane) {
		if (membrane.getNumOf(this) == 0) {
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
		return "!" + getNameDim();
	}

}
