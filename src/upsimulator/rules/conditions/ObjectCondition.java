package upsimulator.rules.conditions;

import upsimulator.core.PObject;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

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
		return getNameDim() + "^" + num;
	}

	@Override
	public ObjectCondition deepClone() throws CloneNotSupportedException {
		return (ObjectCondition) super.deepClone();
	}

}
