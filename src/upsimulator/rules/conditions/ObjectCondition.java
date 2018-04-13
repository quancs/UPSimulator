package upsimulator.rules.conditions;

import upsimulator.core.PObject;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Represents the object that will be consumed in reaction.
 * 
 * @author quan
 *
 */
public class ObjectCondition extends PObject implements Condition {
	int num = 1;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public ObjectCondition() {
		super();
	}

	public ObjectCondition(PObject object, int num) {
		super(object);
		this.num = num;
	}

	public ObjectCondition(String name, int num, String... dims) {
		super(name, dims);
		this.num = num;
	}

	@Override
	public boolean satisfy(Membrane membrane) {
		if (membrane.getNumOf(this) >= num) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean fetch(Membrane membrane) {
		if (membrane.reduceObject(this, num))
			return true;
		else
			return false;
	}

	@Override
	public void withdrawFetch(Membrane membrane) {
		membrane.addObject(new PObject(this), num);
	}

	@Override
	public String toString() {
		if (num > 1) {
			return getNameDim() + "^" + num;
		} else {
			return getNameDim();
		}
	}

	@Override
	public ObjectCondition deepClone() {
		return (ObjectCondition) super.deepClone();
	}

}
