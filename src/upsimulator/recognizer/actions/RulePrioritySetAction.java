package upsimulator.recognizer.actions;

import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;
import upsimulator.rules.conditions.PriorityCondition;

/**
 * Set the priority of rule to make the priority condition meaning right.
 * 
 * @author quan
 *
 */
public class RulePrioritySetAction extends RecognizerAction {

	private Rule rule;
	private Membrane to;
	private int priority;

	public RulePrioritySetAction(Rule rule, Membrane to, int priority) {
		this.rule = rule;
		this.to = to;
		this.priority = priority;
	}

	@Override
	public void init() {
		addUnreadyCount(to);
	}

	@Override
	public boolean ready() {
		return true;
	}

	@Override
	public void doAction() {
		PriorityCondition pCondition = new PriorityCondition(priority);
		rule.addCondition(pCondition);
		minusUnreadyCount(to);
	}

	@Override
	public String toString() {
		return "RulePrioritySetAction : add rule with priority to " + to.getNameDim() + ", where priority= " + priority;
	}
}
