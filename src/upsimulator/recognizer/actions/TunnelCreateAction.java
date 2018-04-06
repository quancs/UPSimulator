package upsimulator.recognizer.actions;

import java.util.List;

import upsimulator.core.PTunnel;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Tunnel.TunnelType;

public class TunnelCreateAction extends RecognizerAction {

	private Membrane source;
	private String target;

	public TunnelCreateAction(Membrane source, String target) {
		super();
		this.source = source;
		this.target = target;
	}

	@Override
	public void init() {
		addUnreadyCount(source);
	}

	@Override
	public boolean ready() {
		return super.ready(target);
	}

	@Override
	public void doAction() {
		Membrane parent = source.getParent();
		List<Membrane> brothers = parent.getChildren();
		for (Membrane brother : brothers) {
			if (brother.getNameDim().equals(target)) {
				PTunnel tunnel = new PTunnel(TunnelType.Go);
				tunnel.setSource(source);
				tunnel.addTarget(brother);
				source.addTunnel(tunnel);
				break;
			}
		}
		minusUnreadyCount(source);
	}
}
