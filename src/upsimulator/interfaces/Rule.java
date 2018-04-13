package upsimulator.interfaces;

import java.util.HashMap;
import java.util.List;

import upsimulator.exceptions.UnpredictableDimensionException;

/**
 * 规则：规则的优先级不一定会被使用，只有当规则中存在优先级条件的时候才会被使用
 * 
 * @author quan
 *
 */
public interface Rule extends Dimension, Name, Cloneable {
	static HashMap<String, List<Rule>> ruleSet = new HashMap<>();// 规则集

	/**
	 * Register a rule set
	 * 
	 * @param name
	 *            the name of rule set
	 * @param ruleSet1
	 *            rules in the registered rule set
	 */
	public static void registRuleSet(String name, List<Rule> ruleSet1) {
		ruleSet.put(name, ruleSet1);
	}

	/**
	 * Get rule set
	 * 
	 * @param name
	 *            the name of rule set
	 * @return rule set or {@code null} if rule set does not exist
	 */
	public static List<Rule> getRuleSet(String name) {
		return ruleSet.get(name);
	}

	/**
	 * Check if all the conditions are satisfied at once.
	 * 
	 * @param membrane
	 *            the test membrane
	 * @return if satisfied
	 */
	public boolean satisfy(Membrane membrane);

	/**
	 * Check if rule with dimensions is satisfied, return the satisfied rules which
	 * have fixed their dimensions.
	 * 
	 * @param membrane
	 *            the test membrane
	 * @return All the satisfied rules
	 * @throws UnpredictableDimensionException
	 *             if rule has dimension whose value cannot be predicted
	 * @throws CloneNotSupportedException
	 *             if clone failed
	 */
	public List<Rule> satisfiedRules(Membrane membrane) throws UnpredictableDimensionException, CloneNotSupportedException;

	/**
	 * Fetch objects from membrane
	 * 
	 * @param membrane
	 *            target membrane
	 * @return return {@code true} if all the conditions inside have fetched the
	 *         objects they need
	 */
	public boolean fetch(Membrane membrane);

	/**
	 * Add new condition
	 * 
	 * @param condition
	 *            new condition
	 */
	public void addCondition(Condition condition);

	/**
	 * Add new result
	 * 
	 * @param result
	 *            new result
	 */
	public void addResult(Result result);

	/**
	 * Get the result list of this rule
	 * 
	 * @return result list
	 */
	public List<Result> getResults();

	/**
	 * Get the condition list of this rule
	 * 
	 * @return condition list
	 */
	public List<Condition> getConditions();

	/**
	 * Deep clone one rule
	 * 
	 * @return the cloned rule
	 */
	public Rule deepClone();
}
