package upsimulator.exceptions;

import upsimulator.interfaces.Membrane;

public class TimesException extends RuntimeException {
	private static final long serialVersionUID = 5467419210476630212L;
	
	private Object who;
	private Membrane membrane;
	private int currentTimes, maxTimes;

	public TimesException(Object who, Membrane membrane, int currentTimes, int maxTimes) {
		super(who.toString() + " cannot set or fetch " + currentTimes + " times, which is no more than " + maxTimes + " in " + membrane.getNameDim());
		this.who = who;
		this.membrane = membrane;
		this.currentTimes = currentTimes;
		this.maxTimes = maxTimes;
	}

}
