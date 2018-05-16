package upsimulator.core;

import upsimulator.interfaces.BasicName;
import upsimulator.interfaces.Obj;

/**
 * PObject represents the object in P systems.<br>
 * 
 * @author quan
 *
 */
public class PObject extends BasicName implements Obj {
	private static final long serialVersionUID = 918301397917102425L;

	public PObject(String name, String... dims) {
		setName(name);
		for (String string : dims)
			addDimension(string);
	}

	public PObject() {
		setName("NoType");
	}

	public PObject(PObject object) {
		super(object);
	}

	@Override
	public PObject deepClone() {
		if (isFixed())
			return this;
		return new PObject(this);
	}

	@Override
	public String toString() {
		return getNameDim();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNameDim() == null) ? 0 : getNameDim().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Obj))
			return false;
		Obj other = (Obj) obj;

		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getNameDim().equals(other.getNameDim()))
			return false;
		return true;
	}
}
