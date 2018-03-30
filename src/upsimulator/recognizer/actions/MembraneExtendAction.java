package upsimulator.recognizer.actions;

import upsimulator.interfaces.Membrane;

/**
 * This class is for membrane extending if the membrane extended has not
 * recognized before, then this class will help son to extend.
 * 在识别的时候，子类先于父类声明，则子类需要等待父类声明出现之后，补全父类内部的内容
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
}