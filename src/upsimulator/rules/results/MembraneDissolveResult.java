package upsimulator.rules.results;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import upsimulator.core.PTunnel;
import upsimulator.exceptions.TimesException;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.MembraneListener;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

public class MembraneDissolveResult implements Result, MembraneListener {

	public MembraneDissolveResult() {

	}

	@Override
	public String toString() {
		return "dissolve";
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException, TimesException {
		membrane.delete();
		membrane.addListener(this);
	}

	@Override
	public MembraneDissolveResult deepClone() {
		return this;
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		return current.getTunnel(TunnelType.Here, null);
	}

	@Override
	public void startChecking(Membrane membrane) {

	}

	@Override
	public void endChecking(Membrane membrane) {

	}

	@Override
	public void startFetching(Membrane membrane) {

	}

	@Override
	public void endFetching(Membrane membrane) {

	}

	@Override
	public void startSetting(Membrane membrane) {

	}

	@Override
	public void endSetting(Membrane membrane) {
		List<Membrane> children = membrane.getChildren();
		Membrane parent = membrane.getParent();

		// remove from parent
		Iterator<Tunnel> tIter = parent.getTunnels().iterator();
		for (; tIter.hasNext();) {
			Tunnel t = tIter.next();
			t.pushResult();

			if (t.getTargets().contains(membrane))
				t.close();
		}

		// move sub-membrane to parent
		for (Membrane child : children) {
			Tunnel tunnel = membrane.getTunnel(TunnelType.Out, null);
			tunnel.pushResult();
			tunnel.close();
			PTunnel.addChildParentTunnel(parent, child);
		}

		// move objects to parent
		Iterator<Map.Entry<Obj, Number>> iterator = membrane.getObjects().entrySet().iterator();
		for (; iterator.hasNext();) {
			Map.Entry<Obj, Number> entry = iterator.next();
			parent.getObjects().put(entry.getKey(), entry.getValue());
		}

		membrane.removeListener(this);
	}

}
