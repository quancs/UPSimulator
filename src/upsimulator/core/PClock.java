package upsimulator.core;

public class PClock {

	private static PClock clock;

	public static PClock getClock() {
		if (clock == null)
			clock = new PClock();
		return clock;
	}

	private int step, substep;

	private PClock() {
		this.step = 1;
		this.substep = 0;
	}

	public void nextSubstep() {
		substep++;
	}

	public void nextStep() {
		step++;
		substep = 0;
	}

	public long getStep() {
		return step;
	}

	public long getSubstep() {
		return substep;
	}

	public String getTimeStr() {
		return "" + clock.step + "." + clock.substep;
	}
}
