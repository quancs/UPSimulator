package upsimulator.speedup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import upsimulator.interfaces.Dimension;
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
	private List<Dimension> dims;
	private List<Long[]> pValues;
	private Membrane target;
	private HashMap<Rule, Integer> satisfiedRules;

	public RuleChecker(Rule source, List<Long[]> list, Membrane target) {
		super();
		this.source = source;
		this.pValues = list;
		this.target = target;
		this.dims = source.getDimensions();
		satisfiedRules = new HashMap<>();
	}

	@Override
	public void doWork() {
		HashMap<String, Object> map = new HashMap<>();
		for (Long[] pv : pValues) {
			Rule rule = source.deepClone();
			for (int i = 0; i < dims.size(); i++)
				map.put(dims.get(i).getText(), pv[i]);
			rule.fix(map);

			int satisfy = rule.satisfy(target);
			if (satisfy > 0) {
				satisfiedRules.put(rule, satisfy);
//				System.err.println(map);
//				System.err.println(rule);
//				System.err.println(satisfy);
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
