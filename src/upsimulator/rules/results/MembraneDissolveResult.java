
package upsimulator.rules.results;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
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
public class MembraneDissolveResult implements Result {

	@Override
	public MembraneDissolveResult deepClone() throws CloneNotSupportedException {
		return this;
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException {
		membrane.delete();
	}

	@Override
	public String toString() {
		return "dissolve";
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
}
