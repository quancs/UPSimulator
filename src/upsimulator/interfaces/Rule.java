package upsimulator.interfaces;

import java.util.HashMap;
import java.util.List;

import upsimulator.exceptions.UnknownTargetMembraneException;
import upsimulator.exceptions.UnpredictableDimensionException;

/**
 * 规则：规则的优先级不一定会被使用，只有当规则中存在优先级条件的时候才会被使用
 * 
 * @author quan
 *
 */
public interface Rule extends Dimension, Name, Cloneable {
	static HashMap<String, List<Rule>> ruleSet = new HashMap<>();// 规则集

	public static void addRuleSet(String name, List<Rule> ruleSet1) {
		ruleSet.put(name, ruleSet1);
	}

	public static List<Rule> getRuleSet(String name) {
		return ruleSet.get(name);
	}

	/**
	 * 第一步：规则的所有前提是否满足条件
	 * 
	 * @param membrane
	 *            测试环境
	 * @return 是否满足条件
	 */
	public boolean satisfy(Membrane membrane);

	/**
	 * 如果当前规则是规则集（带有维度的多条规则的集合），则返回具有具体维度的规则
	 * 
	 * @param membrane
	 *            测试环境
	 * @return 规则集中满足条件的规则
	 * @throws UnpredictableDimensionException
	 * @throws CloneNotSupportedException
	 */
	public List<Rule> satisfiedRules(Membrane membrane) throws UnpredictableDimensionException, CloneNotSupportedException;

	/**
	 * 第二步：执行所有前提
	 * 
	 * @param membrane
	 * @return
	 */
	public boolean fetch(Membrane membrane);

	/**
	 * 新增前提
	 * 
	 * @param condition
	 */
	public void addCondition(Condition condition);

	/**
	 * 新增结果
	 * 
	 * @param result
	 */
	public void addResult(Result result);

	/**
	 * 获取规则的全部结果
	 * 
	 * @return 规则的全部结果
	 */
	public List<Result> getResults();

	/**
	 * 执行第三步：设置结果，由于结果的设置已经在Membrane中做了，因此此方法不需要了
	 * 
	 * @param membrane
	 * @throws UnknownTargetMembraneException
	 *             当设置出错的时候，抛出异常；比如，执行out的时候没有父膜
	 */
	public void setResult(Membrane membrane) throws UnknownTargetMembraneException;

	/**
	 * 获取所有的条件
	 * 
	 * @return
	 */
	public List<Condition> getConditions();

	/**
	 * 用于实现对象的深拷贝，因为膜的类型声明，所以声明一个膜的时候需要深拷贝
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Rule deepClone();

	// /**
	// * 设置规则的优先级
	// *
	// */
	// public void setPriority(int pri);
	//
	// /**
	// * 优先级越高，数字越小
	// *
	// * @return 规则的优先级
	// */
	// public int getPriority();

}
