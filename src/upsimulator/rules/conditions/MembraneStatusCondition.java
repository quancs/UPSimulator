package upsimulator.rules.conditions;

public class MembraneStatusCondition extends MembranePropertyCondition {

	public MembraneStatusCondition(String value) {
		super("status", value);
	}

	@Override
	public MembraneStatusCondition deepClone() {
		return (MembraneStatusCondition) super.deepClone();
	}

	@Override
	public String toString() {
		return getValue();
	}
}
