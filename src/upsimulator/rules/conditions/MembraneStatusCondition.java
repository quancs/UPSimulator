package upsimulator.rules.conditions;

/**
 * Represents the membrane status condition in tissue-like P system
 * 
 * @author quan
 *
 */
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
		return "<" + getValue() + ">";
	}
}
