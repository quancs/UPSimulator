
package upsimulator.rules.results;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.MembraneListener;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * Dissolve a membrane by calling {@code Membrane.delete()}. After calling all
 * the tunnels inside will be closed, and tunnels to this membrane will be
 * closed, too.
 * 
 * @author quan
 *
 */
public class MembraneDissolveAllResult implements Result, MembraneListener {

	@Override
	public MembraneDissolveAllResult deepClone() {
		return this;
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException {
		membrane.delete();
		membrane.addListener(this);
	}

	@Override
	public String toString() {
		return "dissolve(all)";
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		Tunnel tunnel = current.getTunnel(TunnelType.Here, null);
		if (tunnel == null) {
			throw new TunnelNotExistException(current, TunnelType.Here);
		} else {
			return tunnel;
		}
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
		membrane.removeTunnel(membrane.getTunnel(TunnelType.Out, null));
		membrane.removeListener(this);
	}
}
