package upsimulator.rules.results;

import java.util.ArrayList;

import upsimulator.core.PTunnel;
import upsimulator.exceptions.TimesException;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.MembraneListener;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.interfaces.UPSLogger;

public class MembraneDivisionResult implements Result, Condition, MembraneListener {

	private ArrayList<ObjectResult> oResults1, oResults2;// source cloned
	private ArrayList<MembranePropertyResult> mpResults1, mpResults2;

	public MembraneDivisionResult() {
		oResults1 = new ArrayList<>();
		oResults2 = new ArrayList<>();
		mpResults1 = new ArrayList<>();
		mpResults2 = new ArrayList<>();
	}

	public void addObjectResult1(ObjectResult oResult) {
		oResults1.add(oResult);
	}

	public void addObjectResult2(ObjectResult oResult) {
		oResults2.add(oResult);
	}

	public void addMembranePropertyResult1(MembranePropertyResult mpResult) {
		mpResults1.add(mpResult);
	}

	public void addMembranePropertyResult2(MembranePropertyResult mpResult) {
		mpResults2.add(mpResult);
	}

	private int times;

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException, TimesException {
		this.times = times;
	}

	@Override
	public MembraneDivisionResult deepClone() {
		try {
			return (MembraneDivisionResult) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			UPSLogger.error(this, e);
			return null;
		}
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		return current.getTunnel(TunnelType.Here, null);
	}

	@Override
	public int satisfy(Membrane membrane) {
		return Integer.MAX_VALUE;
	}

	@Override
	public int fetch(Membrane membrane, int tryTimes) {
		// Check if all the MembranePropertyResult are satisfied in source membrane
		int i;
		for (i = 0; i < mpResults1.size(); i++) {
			if (mpResults1.get(i).fetch(membrane, tryTimes) == 0)
				break;
		}
		if (i != mpResults1.size()) {
			for (i = i - 1; i >= 0; i--) {
				mpResults1.get(i).withdrawFetch(membrane, 1);
			}
			return 0;
		}

		Object prop = membrane.getProperty("$divide");
		if (prop != null && prop != this)
			return 0;
		else {
			membrane.setProperty("$divide", this);
			membrane.addListener(this);
			return tryTimes;
		}
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
		if (membrane.getProperty("$divide") == this) {
			membrane.setProperty("$divide", null);
		}
		for (MembranePropertyResult mp : mpResults1)
			mp.withdrawFetch(membrane, 1);
		membrane.removeListener(this);
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
		try {
			Membrane cloned = membrane.deepClone();
			for (Result or : oResults1)
				or.setResult(membrane, times);
			for (Result or : mpResults1)
				or.setResult(membrane, times);

			for (Result or : oResults2)
				or.setResult(cloned, times);
			for (Result or : mpResults2)
				or.setResult(cloned, times);

			membrane.getParent().addChild(PTunnel.class, cloned);
			membrane.removeListener(this);
		} catch (TimesException | UnknownMembraneClassException e) {
			e.printStackTrace();
			UPSLogger.error(this, e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder("divide({");
		for (Result or : mpResults1)
			sBuilder.append(" " + or.toString());
		for (Result or : oResults1)
			sBuilder.append(" " + or.toString());
		sBuilder.append(" },{");

		for (Result or : mpResults2)
			sBuilder.append(" " + or.toString());
		for (Result or : oResults2)
			sBuilder.append(" " + or.toString());
		sBuilder.append(" })");
		return sBuilder.toString();
	}
}