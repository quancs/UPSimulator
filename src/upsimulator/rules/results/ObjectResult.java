package upsimulator.rules.results;

import upsimulator.core.PObject;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * Represents the object product in rules
 * 
 * @author quan
 *
 */
public class ObjectResult extends PObject implements Result {
	private int num = 1;

	public ObjectResult() {
		super();
	}

	public ObjectResult(String name, String... dims) {
		super(name, dims);
	}

	public ObjectResult(ObjectResult object) {
		super(object);
		this.num = object.num;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public ObjectResult(PObject obj) {
		super(obj);
	}

	public ObjectResult(String type, int num, String... dims) {
		super(type, dims);
		this.num = num;
	}

	@Override
	public void setResult(Membrane membrane, int times) {
		membrane.addObject(this, num * times);
	}

	@Override
	public String toString() {
		if (num == 1) {
			return super.toString();
		} else {
			return super.toString() + "^" + num;
		}
	}

	@Override
	public ObjectResult deepClone() {
		if (isFixed())
			return this;
		else
			return new ObjectResult(this);
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
