package upsimulator.recognizer.actions;

import java.util.List;

import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;

public class RuleSetDeclareAction extends RecognizerAction {

	private String ruleSetName, prefix;
	private Membrane to;// add ruleset to 'to'

	public RuleSetDeclareAction(String ruleSetName, String prefix, Membrane to) {
		super();
		this.ruleSetName = ruleSetName;
		this.prefix = prefix;
		this.to = to;
	}

	@Override
	public void init() {
		addUnreadyCount(to);
	}

	@Override
	public boolean ready() {
		if (Rule.getRuleSet(ruleSetName) == null)
			throw new RuntimeException("需要的规则集不存在：" + ruleSetName);
		return true;
	}

	@Override
	public void doAction() {
		List<Rule> targetRuleSet = Rule.getRuleSet(ruleSetName);
		for (int i = 0; i < targetRuleSet.size(); i++) {
			Rule rule = (Rule) targetRuleSet.get(i).deepClone();
			if (prefix != null)
				rule.setName(prefix + rule.getName());
			to.addRule(rule);
		}
		minusUnreadyCount(to);
	}

	@Override
	public String toString() {
		return "RulePrioritySetAction : add ruleset with name prefix to " + to.getNameDim() + ", where name's prefix= " + prefix;
	}
}
