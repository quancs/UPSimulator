package upsimulator.rules.results;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * Represents a membrane property result
 * 
 * @author quan
 *
 */
public class MembranePropertyResult implements Result {

	private String property;
	private String value;

	public MembranePropertyResult() {
	}

	public MembranePropertyResult(String property, String value) {
		this.property = property;
		this.value = value;
	}

	@Override
	public Result deepClone() {
		return this;
	}

	@Override
	public void setResult(Membrane membrane) throws UnknownMembraneClassException {
		membrane.setProperty(property, value);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	public String toString() {
		return "<" + getProperty() + "=" + getValue() + ">";
	}

}
