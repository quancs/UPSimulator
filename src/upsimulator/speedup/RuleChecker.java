package upsimulator.speedup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;

/**
 * Speed up checking rule's satisfaction
 * 
 * @author quan
 *
 */
public class RuleChecker extends Worker {

	private Rule source;
	private List<String> dims;
	private List<Integer[]> pValues;
	private Membrane target;
	private HashMap<Rule, Integer> satisfiedRules;

	public RuleChecker(Rule source, List<Integer[]> list, Membrane target) {
		super();
		this.source = source;
		this.pValues = list;
		this.target = target;
		this.dims = source.getDimensions();
		satisfiedRules = new HashMap<>();
	}

	@Override
	public void doWork() {
		for (Integer[] pv : pValues) {
			Rule rule = source.deepClone();
			for (int i = 0; i < pv.length; i++)
				rule.getEval().putVariable(dims.get(i), pv[i].toString());

			int satisfy = rule.satisfy(target);
			if (satisfy > 0) {
				rule.fixDimension();
				satisfiedRules.put(rule, satisfy);
			}
		}
	}

	public Map<Rule, Integer> getSatisfiedRules() {
		return satisfiedRules;
	}

	public int getTotal() {
		return pValues.size();
	}

}
