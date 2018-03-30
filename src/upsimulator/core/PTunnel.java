package upsimulator.core;

import java.util.ArrayList;
import java.util.List;

import upsimulator.exceptions.UnknownTargetMembraneException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/*
 * Membrane's connection between A and B.
 * Connection can be a protein tunnel between A and B.
 * */

public class PTunnel implements Tunnel {
	private static final long serialVersionUID = -7893633024003440550L;
	
	private Membrane source;
	private ArrayList<Membrane> targets;
	private String name;
	private boolean isOpen;
	private TunnelType type;
	private ArrayList<Result> heldResults;

	public PTunnel(TunnelType type) {
		heldResults = new ArrayList<Result>();
		targets = new ArrayList<Membrane>();
		this.type = type;
		open();
	}

	public PTunnel() {
		heldResults = new ArrayList<Result>();
		targets = new ArrayList<Membrane>();
		open();
	}

	public void setType(TunnelType type) {
		this.type = type;
	}

	public TunnelType getType() {
		return type;
	}

	public String getName() {
		if (name == null && source != null && targets.size() > 0) {
			name = source.getNameDim() + "->";
			for (int i = 0; i < targets.size() - 1; i++)
				name += targets.get(i).getNameDim() + ",";
			name += targets.get(targets.size() - 1).getNameDim();
		}
		return name;
	}

	public Membrane getSource() {
		return source;
	}

	public void setSource(Membrane source) {
		this.source = source;
	}

	public List<Membrane> getTargets() {
		return targets;
	}

	public void addTarget(Membrane target) {
		targets.add(target);
	}

	public void holdResult(Result result) {
		heldResults.add(result);
	}

	public List<Result> getHeldResults() {
		return heldResults;
	}

	public void pushResult() {
		if (getType() == TunnelType.One2ManyRandom) {
			Membrane target = targets.get(((int) (Math.random() * targets.size()) % targets.size()));
			for (Result result : heldResults) {
				try {
					result.setResult(target);
				} catch (UnknownTargetMembraneException e) {
					e.printStackTrace();
				}
			}
		} else {
			for (Result result : heldResults) {
				try {
					for (Membrane target : targets)
						result.setResult(target);
				} catch (UnknownTargetMembraneException e) {
					e.printStackTrace();
				}
			}
		}
		heldResults.clear();

		// Check if need to close this Tunnel
		if (source.isDeleted())
			close();

		for (int i = 0; i < targets.size();) {
			if (targets.get(i).isDeleted()) {
				targets.remove(i);
			} else {
				i++;
			}
		}
		if (targets.size() == 0)
			close();
	}

	public void open() {
		isOpen = true;
	}

	public void close() {
		isOpen = false;
	}

	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public String getTargetsName() {
		if (targets.size() == 0) {
			return null;
		} else if (targets.size() == 1) {
			return targets.get(0).getNameDim();
		} else {
			String split = "?";
			if (getType() == TunnelType.One2Many)
				split = "&";
			else if (getType() == TunnelType.One2ManyRandom) {
				split = "|";
			}
			String name = "";
			for (int i = 0; i < targets.size() - 1; i++) {
				name += targets.get(i).getNameDim() + split;
			}
			name += targets.get(targets.size() - 1).getNameDim();
			return name;
		}
	}

	public static void addChildParentTunnel(Membrane father, Membrane child) {
		Tunnel in = new PTunnel(TunnelType.In);
		in.setSource(father);
		in.addTarget(child);
		father.addTunnel(in);

		Tunnel out = new PTunnel(TunnelType.Out);
		out.setSource(child);
		out.addTarget(father);
		child.addTunnel(out);
	}
}