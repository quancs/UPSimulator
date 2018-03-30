package upsimulator.rules.conditions;

import upsimulator.core.PClock;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

public class PriorityCondition implements Condition {
	private PClock clock;
	private int priority = 1;

	public PriorityCondition(int priority) {
		this.priority = priority;
		this.clock=PClock.getClock();
	}

	@Override
	public PriorityCondition deepClone() throws CloneNotSupportedException {
		return (PriorityCondition) this.clone();
	}

	@Override
	public boolean satisfy(Membrane membrane) {
		String rulePriPropStr="$rulepri"+clock.getStep();
		Integer priHighest= (Integer) membrane.getProperty(rulePriPropStr);
		if(priHighest==null) {
			membrane.setProperty(rulePriPropStr, priority);
			return true;
		}else if(priHighest<priority) {
			return false;
		}else if(priHighest==priority) {
			return true;
		}else {
			throw new RuntimeException("存在优先级比当前规则的优先级低的规则在可使用规则列表里面");
		}
	}

	@Override
	public boolean fetch(Membrane membrane) {
		return true;
	}

	@Override
	public void withdrawFetch(Membrane membrane) {

	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "pri=" + priority;
	}

}
