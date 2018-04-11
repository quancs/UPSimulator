package upsimulator.rules.results;

import upsimulator.core.PObject;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * 以对象作为结果
 * 
 * @author quan
 *
 */
public class ObjectResult extends PObject implements Result {
	int num = 1;

	public ObjectResult() {
		super();
	}

	public ObjectResult(String name, String... dims) {
		super(name, dims);
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
	public void setResult(Membrane membrane) {
		membrane.addObject(new PObject(this), num);
	}

	@Override
	public String toString() {
		if (num > 1) {
			return getNameDim() + "^" + num;
		} else {
			return getNameDim();
		}
	}

	@Override
	public ObjectResult deepClone() {
		return (ObjectResult) super.deepClone();
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
