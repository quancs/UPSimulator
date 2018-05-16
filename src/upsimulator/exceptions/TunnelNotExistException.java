package upsimulator.exceptions;

import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * If tunnel does not exist, this exception will be thrown.
 * 
 * @author quan
 *
 */
public class TunnelNotExistException extends RuntimeException {
	private static final long serialVersionUID = -3679463803307171954L;

	private Membrane membrane;// which membrane
	private String tunnel;// which tunnel
	private TunnelType type;

	public TunnelNotExistException(Membrane membrane, TunnelType type, String tunnel) {
		super("Couldn't find tunnel (" + type.toString() + " " + tunnel + ") in " + membrane.getNameDim());
		this.membrane = membrane;
		this.tunnel = tunnel;
		this.type = type;
	}

	public TunnelNotExistException(Membrane membrane, TunnelType type, String tunnel, Result result) {
		super("Couldn't find tunnel (" + type.toString() + " " + tunnel + ") in " + membrane.getNameDim() + " while setting result " + result.toString());
		this.membrane = membrane;
		this.tunnel = tunnel;
		this.type = type;
	}

	public TunnelNotExistException(Membrane membrane, TunnelType type) {
		super("Couldn't find tunnel (" + type.toString() + ") in " + membrane.getNameDim());
		this.membrane = membrane;
		this.tunnel = "";
		this.type = type;
	}
}
