package upsimulator.interfaces;

public interface Name {
	/**
	 * 为名称对象设置名称
	 * @param name
	 */
	public void setName(String name);

	/**
	 * 获取对象维度名称 例如对象a2的名称是a，维度名称=名称+维度
	 * 
	 * @return
	 */
	public String getNameDim();

	/**
	 * 获取对象名称 例如对象a2的名称是a
	 * 
	 * @return
	 */
	public String getName();
}
