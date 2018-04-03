package upsimulator.interfaces;

public interface Obj extends Dimension, Name, Cloneable {

	/**
	 * 用于实现对象的深拷贝，因为膜的类型声明，所以声明一个膜的时候需要深拷贝
	 * 
	 * @return
	 */
	public Obj deepClone();
}
