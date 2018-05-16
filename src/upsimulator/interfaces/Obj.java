package upsimulator.interfaces;

public interface Obj extends Name, Cloneable {

	/**
	 * Deep clone a {@code Obj}. If this {@code Obj} has dimensions then dimensions
	 * will be cloned
	 * 
	 * @return the cloned value
	 */
	public Obj deepClone();
}
