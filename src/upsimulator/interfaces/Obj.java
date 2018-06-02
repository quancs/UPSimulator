package upsimulator.interfaces;

public interface Obj extends Name, Cloneable {

	/**
	 * Return if this object is anti-object
	 * 
	 * @return true if yes, false if no
	 */
	public boolean isAntiObject();

	/**
	 * Deep clone a {@code Obj}. If this {@code Obj} has dimensions then dimensions
	 * will be cloned
	 * 
	 * @return the cloned value
	 */
	public Obj deepClone();
}
