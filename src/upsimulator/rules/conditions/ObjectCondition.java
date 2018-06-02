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
	private int num = 1;

	public ObjectCondition() {
		super();
	}

	public ObjectCondition(ObjectCondition objectCondition) {
		super(objectCondition);
		setNum(objectCondition.getNum());
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
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
	public int satisfy(Membrane membrane) {
		return (Integer) membrane.getNumOf(this) / num;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		if (membrane.reduceObject(this, num * times))
			return times;
		else if (times > 1) {
			int satisfy = satisfy(membrane);
			if (satisfy > 0) {
				return fetch(membrane, satisfy);
			} else
				return 0;
		} else
			return 0;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
		membrane.addObject(this, num * times);
	}

	@Override
	public String toString() {
		if (num == 1) {
			return getNameDim();
		} else {
			return getNameDim() + "^" + num;
		}
	}

	@Override
	public ObjectCondition deepClone() {
		if (isFixed())
			return this;
		return new ObjectCondition(this);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
