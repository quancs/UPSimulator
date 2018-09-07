package upsimulator.rules.results;

import upsimulator.exceptions.TimesException;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;

public class DelayedResult implements Result {

	private static int maxDelay = 0;

	private Result result;
	private int delay;
	private int delayStored;

	public DelayedResult(Result result, int delay) {
		this.result = result;
		this.delay = delay;
		this.delayStored = delay;
		if (maxDelay < delay)
			maxDelay = delay;
	}

	public DelayedResult(DelayedResult dResult) {
		this.result = dResult.result.deepClone();
		this.delay = dResult.delay;
		this.delayStored = dResult.delayStored;
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException, TimesException {
		result.setResult(membrane, times);
	}

	@Override
	public DelayedResult deepClone() {
		return new DelayedResult(this);
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		return result.selectTunnel(current);
	}

	public int getDelay() {
		return delay;
	}

	public void reduceDelay() {
		delay--;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "delay(" + result.toString() + "," + delayStored + ")";
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public static int getMaxDelay() {
		return maxDelay;
	}
}
