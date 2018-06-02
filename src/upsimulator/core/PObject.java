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
	private boolean anti = false;

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
		anti = object.anti;
	}

	@Override
	public void setName(String name) {
		if (name.startsWith("-")) {
			anti = true;
			name = name.replaceFirst("-", "");
		}
		super.setName(name);
	}

	@Override
	public PObject deepClone() {
		if (isFixed())
			return this;
		return new PObject(this);
	}

	@Override
	public String toString() {
		if (anti)
			return "-" + getNameDim();
		else
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

	@Override
	public boolean isAntiObject() {
		return anti;
	}

	public boolean isAnti() {
		return anti;
	}

	public void setAnti(boolean anti) {
		this.anti = anti;
	}
}
