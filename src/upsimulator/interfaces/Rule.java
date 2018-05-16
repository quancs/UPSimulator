package upsimulator.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import upsimulator.exceptions.UnpredictableDimensionException;

/**
 * A general implementation of Rule
 * 
 * @author quan
 *
 */
public interface Rule extends Name, Cloneable {
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
	 * @return satisfied times
	 */
	public int satisfy(Membrane membrane);

	/**
	 * Check if rule with dimensions is satisfied, return the satisfied rules which
	 * have fixed their dimensions.
	 * 
	 * @param membrane
	 *            the test membrane
	 * @return All the satisfied rules and satisfied times
	 * @throws UnpredictableDimensionException
	 *             if rule has dimension whose value cannot be predicted
	 * @throws CloneNotSupportedException
	 *             if clone failed
	 */
	public Map<Rule, Integer> satisfiedRules(Membrane membrane) throws UnpredictableDimensionException, CloneNotSupportedException;

	/**
	 * Fetch objects from membrane
	 * 
	 * @param membrane
	 *            target membrane
	 * @param times
	 *            times try to fetch
	 * @return return the times all the conditions have fetched
	 */
	public int fetch(Membrane membrane, int times);

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
