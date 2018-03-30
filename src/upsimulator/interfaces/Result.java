package upsimulator.interfaces;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownTargetMembraneException;

public interface Result extends Cloneable {
	/**
	 * 执行第二步：设置结果
	 * 
	 * @param membrane
	 * @throws UnknownTargetMembraneException
	 *             当设置出错的时候，抛出异常；比如，执行out的时候没有父膜
	 */
	public void setResult(Membrane membrane) throws UnknownTargetMembraneException;

	/**
	 * 用于实现对象的深拷贝，因为膜的类型声明，所以声明一个膜的时候需要深拷贝
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Result deepClone() throws CloneNotSupportedException;

	/**
	 * 优先级越高，数字越小
	 * 
	 * @return 结果的优先级
	 */
	public int getPriority();

	/**
	 * Select which tunnel this result will go through
	 * 
	 * @param current
	 *            current membrane
	 * @return the tunnel selected. If none has been selected, null will be
	 *         returned.
	 * @throws TunnelNotExistException
	 */
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException;

	/**
	 * 比较两个结果的优先级
	 * 
	 * @param result1
	 * @param result2
	 * @return 返回降序
	 */
	public static int compare(Result result1, Result result2) {
		return result1.getPriority() - result2.getPriority();
	}

}