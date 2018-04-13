package upsimulator.recognizer.actions;

import upsimulator.interfaces.Membrane;

/**
 * This class is used for membrane extending if the membrane extended has not
 * been recognized before. Child will extends parent, until parent has been
 * recognized.
 * 
 * @author quan
 *
 */
public class MembraneExtendAction extends RecognizerAction {
	private Membrane to;
	private String from;

	public MembraneExtendAction(Membrane to, String from) {
		this.to = to;
		this.from = from;
	}

	@Override
	public void doAction() {
		Membrane fromMembrane = Membrane.getMemClass(from);
		to.extend(fromMembrane);
		minusUnreadyCount(to);
	}

	@Override
	public boolean ready() {
		return super.ready(from);
	}

	@Override
	public void init() {
		addUnreadyCount(to);
	}

	@Override
	public String toString() {
		return "MembraneExtendAction : " + to.getNameDim() + " extends " + from;
	}
}