package upsimulator.speedup;

import java.util.LinkedList;
import java.util.List;

import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;

public class RuleChecker extends Worker {

	private Rule source;
	private List<String> dims;
	private List<Integer[]> pValues;
	private Membrane target;
	private LinkedList<Rule> satisfiedRules;

	public RuleChecker(Rule source, List<Integer[]> list, Membrane target) {
		super();
		this.source = source;
		this.pValues = list;
		this.target = target;
		this.dims = source.getDimensions();
		satisfiedRules = new LinkedList<>();
	}

	@Override
	public void doWork() {
		try {
			for (Integer[] pv : pValues) {
				Rule rule = source.deepClone();
				for (int i = 0; i < pv.length; i++)
					rule.getEval().putVariable(dims.get(i), pv[i].toString());

				if (rule.satisfy(target)) {
					rule.fixDimension();
					satisfiedRules.add(rule);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<Rule> getSatisfiedRules() {
		return satisfiedRules;
	}

	public int getTotal() {
		return pValues.size();
	}

}
