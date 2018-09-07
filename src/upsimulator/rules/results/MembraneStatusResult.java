package upsimulator.rules.results;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Represents the membrane status condition in tissue-like P system.
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
		return this;
	}

	@Override
	public int satisfy(Membrane membrane) {
		return Integer.MAX_VALUE;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		Object endStatus = membrane.getProperty(endStatusStr);

		if (endStatus != null) {
			synchronized (endStatus) {
				if (endStatus.equals(getValue())) {
					membrane.setProperty(endStatusCountStr, (Integer) membrane.getProperty(endStatusCountStr) + 1);
					return times;
				} else {
					return 0;
				}
			}
		} else {
			synchronized (membrane.getProperties()) {
				endStatus = membrane.getProperty(endStatusStr);
				if (endStatus == null) {
					membrane.setProperty(endStatusStr, getValue());
					membrane.setProperty(endStatusCountStr, 1);
					return times;
				} else {
					synchronized (endStatus) {
						if (endStatus.equals(getValue())) {
							membrane.setProperty(endStatusCountStr, (Integer) membrane.getProperty(endStatusCountStr) + 1);
							return times;
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
