package upsimulator.rules.results;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Condition;
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
public class MembranePropertyResult implements Result, Condition {

	private String endPropStr = "$end", endPropCountStr = "$endStatusCount";

	private String property;
	private String value;

	public MembranePropertyResult() {
	}

	public MembranePropertyResult(String property, String value) {
		this.property = property;
		this.value = value;
		endPropStr = "$end" + property;
		endPropCountStr = "$end" + property + "Count";
	}

	@Override
	public MembranePropertyResult deepClone() {
		return this;
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException {
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

	@Override
	public int satisfy(Membrane membrane) {
		return Integer.MAX_VALUE;
	}

	@Override
	public int fetch(Membrane membrane, int tryTimes) {
		Object endProp = membrane.getProperty(endPropStr);

		if (endProp != null) {
			synchronized (endProp) {
				if (endProp.equals(getValue())) {
					membrane.setProperty(endPropCountStr, (Integer) membrane.getProperty(endPropCountStr) + 1);
					return tryTimes;
				} else {
					return 0;
				}
			}
		} else {
			synchronized (membrane.getProperties()) {
				endProp = membrane.getProperty(endPropStr);
				if (endProp == null) {
					membrane.setProperty(endPropStr, getValue());
					membrane.setProperty(endPropCountStr, 1);
					return tryTimes;
				} else {
					synchronized (endProp) {
						if (endProp.equals(getValue())) {
							membrane.setProperty(endPropCountStr, (Integer) membrane.getProperty(endPropCountStr) + 1);
							return tryTimes;
						} else {
							return 0;
						}
					}
				}
			}
		}
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
		Object endStatus = membrane.getProperty(endPropStr);
		Integer count = (Integer) membrane.getProperty(endPropCountStr);
		synchronized (endStatus) {
			if (count.equals(1)) {
				membrane.setProperty(endPropStr, null);
				membrane.setProperty(endPropCountStr, null);
			} else {
				membrane.setProperty(endPropCountStr, count - 1);
			}
		}
	}
}
