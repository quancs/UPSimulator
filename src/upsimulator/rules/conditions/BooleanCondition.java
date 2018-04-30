package upsimulator.rules.conditions;

import upsimulator.interfaces.BaseDimensional;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * The boolean relationship of dimensions of rule
 * 
 * @author quan
 *
 */
public class BooleanCondition extends BaseDimensional implements Condition {

	public BooleanCondition() {

	}

	public BooleanCondition(BooleanCondition booleanCondition) {
		super(booleanCondition);
	}

	public BooleanCondition(String formula) {
		addDimension(formula);
	}

	@Override
	public int satisfy(Membrane membrane) {
		if (get(0).getBooleanValue())
			return Integer.MAX_VALUE;
		else
			return 0;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		return times;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {

	}

	@Override
	public String toString() {
		return get(0).toString();
	}

	@Override
	public BooleanCondition deepClone() {
		if (isFixed())
			return this;
		BooleanCondition bCondition = new BooleanCondition(this);
		return bCondition;
	}
}