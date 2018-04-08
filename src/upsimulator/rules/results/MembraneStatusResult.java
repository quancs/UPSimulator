package upsimulator.rules.results;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Membrane status result must not be put at the front of object condition in
 * multi-thread context
 * 
 * @author quan
 *
 */
public class MembraneStatusResult extends MembranePropertyResult implements Condition {

	private static String endStatusStr = "$endStatus", endStatusCountStr = "$endStatusCount";

	public MembraneStatusResult(String value) {
		super("status", value);
	}

	@Override
	public MembraneStatusResult deepClone() {
		return (MembraneStatusResult) super.deepClone();
	}

	@Override
	public boolean satisfy(Membrane membrane) {
		return true;
	}

	@Override
	public boolean fetch(Membrane membrane) {
		Object endStatus = membrane.getProperty(endStatusStr);

		if (endStatus != null) {
			synchronized (endStatus) {
				if (endStatus.equals(getValue())) {
					membrane.setProperty(endStatusCountStr, (Integer) membrane.getProperty(endStatusCountStr) + 1);
					return true;
				} else {
					return false;
				}
			}
		} else {
			synchronized (membrane.getProperties()) {
				endStatus = membrane.getProperty(endStatusStr);
				if (endStatus == null) {
					membrane.setProperty(endStatusStr, getValue());
					membrane.setProperty(endStatusCountStr, 1);
					return true;
				} else {
					synchronized (endStatus) {
						if (endStatus.equals(getValue())) {
							membrane.setProperty(endStatusCountStr, (Integer) membrane.getProperty(endStatusCountStr) + 1);
							return true;
						} else {
							return false;
						}
					}
				}
			}
		}
	}

	@Override
	public void withdrawFetch(Membrane membrane) {
		Object endStatus = membrane.getProperty(endStatusStr);
		Integer count = (Integer) membrane.getProperty(endStatusCountStr);
		synchronized (endStatus) {
			if (count.equals(1)) {
				membrane.setProperty(endStatusStr, null);
				membrane.setProperty(endStatusCountStr, null);
			} else {
				membrane.setProperty(endStatusCountStr, count - 1);
			}
		}
	}

	@Override
	public String toString() {
		return "<" + getValue() + ">";
	}
}
