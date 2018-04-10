package upsimulator.recognizer.actions;

import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.Tunnel;

/**
 * Check if every {@code Target} in {@code PositionResult} has defined its
 * tunnel in current membrane
 * 
 * @author quan
 *
 */
public class RuleTunnelChecker extends RecognizerAction {

	private String targetName;// the name of target membrane
	private Rule currRule;// where the target in
	private Membrane currMembrane;// where the rule belongs to

	public RuleTunnelChecker(String targetName, Rule currRule, Membrane currMembrane) {
		super();
		this.targetName = targetName;
		this.currRule = currRule;
		this.currMembrane = currMembrane;
	}

	@Override
	public void init() {

	}

	@Override
	public boolean ready() {
		for (Tunnel tunnel : currMembrane.getTunnels()) {
			if (tunnel.getTargets().get(0).getNameDim().equals(targetName))
				return true;
		}
		return false;
	}

	@Override
	public void doAction() {

	}

	@Override
	public String toString() {
		return "Warning: Tunnel " + targetName + " used in " + currRule.getNameDim() + " does not exist in Membrane " + currMembrane.getNameDim();
	}
}
