
package upsimulator.rules.results;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownTargetMembraneException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * @author quan
 * @Date 2017年10月23日下午4:30:40
 * @Copyright (c) 2017, quancs@qq.com All Rights Reserved.
 * @Todo dissolve a membrane, the objects and son membrane's father will be it's
 *       grandfather; and the result must be the last one in a rule, or the
 *       membrane target of setresult function of one rule will be the wrong one
 *       溶解膜：即是关闭所有的通道
 */
public class MembraneDissolveResult implements Result {

	@Override
	public MembraneDissolveResult deepClone() throws CloneNotSupportedException {
		return this;
	}

	@Override
	public void setResult(Membrane membrane) throws UnknownTargetMembraneException {
		membrane.delete();
	}

	@Override
	public int getPriority() {
		return 3;
	}

	@Override
	public String toString() {
		String str = "";
		// TODO 需要确定输出的形式
		return str;
	}

	@Override
	public Tunnel selectTunnel(Membrane current)  throws TunnelNotExistException{
		Tunnel tunnel = current.getTunnel(TunnelType.Here, null);
		if (tunnel == null) {
			throw new TunnelNotExistException(TunnelType.Here + " tunnel does not exist in " + current);
		} else {
			return tunnel;
		}
	}
}
