package upsimulator.interfaces;

/**
 * 条件
 * 
 * @author quan
 *
 */
public interface Condition extends Cloneable {

	/**
	 * 第一步：是否满足条件（促进剂、抑制剂需要）
	 * 
	 * @param membrane
	 * @return 是否成功
	 */
	public boolean satisfy(Membrane membrane);

	/**
	 * 第二步：取出条件的物质前提
	 * 
	 * @param membrane
	 * @return 是否成功
	 */
	public boolean fetch(Membrane membrane);

	/**
	 * 如果某条件不能成功取出前提，则其前面的已经取出前提的条件应该退回前提
	 * 
	 * @param membrane
	 */
	public void withdrawFetch(Membrane membrane);

	/**
	 * 用于实现对象的深拷贝，因为膜的类型声明，所以声明一个膜的时候需要深拷贝
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Condition deepClone() throws CloneNotSupportedException;
}
