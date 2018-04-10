package upsimulator.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import upsimulator.exceptions.UnknownMembraneException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.UPSLogger;

/*
 * Tunnel is a connection between membranes, used to transfer results.
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
		this.type = TunnelType.Here;
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
			name = source.getNameDim() + "->" + getTargetsName();
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
		targets.sort(new Comparator<Membrane>() {
			@Override
			public int compare(Membrane o1, Membrane o2) {
				return o1.getNameDim().compareTo(o2.getNameDim());
			}
		});
	}

	public void holdResult(Result result) {
		heldResults.add(result);
	}

	public List<Result> getHeldResults() {
		return heldResults;
	}

	private final void pushResultsTo(Membrane membrane) {
		try {
			for (Result result : heldResults)
				result.setResult(membrane);
		} catch (UnknownMembraneException e) {
			e.printStackTrace();
			UPSLogger.error(this, e);
		}
	}

	private final void pushResultsToAll(List<Membrane> membranes) {
		for (Membrane sm : membranes)
			pushResultsTo(sm);
	}

	private final void pushResultsToOne(List<Membrane> membranes) {
		Membrane target = membranes.get((int) (Math.random() * membranes.size()));
		pushResultsTo(target);
	}

	public void pushResult() {
		switch (type) {
		case Out:
		case Here:
		case In:
		case Go:
			pushResultsTo(targets.get(0));
			break;
		case In_all:
			pushResultsToAll(source.getChildren());
			break;
		case In_all_of_specified:
			pushResultsToAll(targets);
			break;
		case In_one_of_all:
			pushResultsToOne(source.getChildren());
			break;
		case In_one_of_specified:
			pushResultsToOne(targets);
			break;
		case Go_all:
			pushResultsToAll(source.getNeighbors());
			break;
		case Go_all_of_specified:
			pushResultsToAll(targets);
			break;
		case Go_one_of_all:
			pushResultsToOne(source.getNeighbors());
			break;
		case Go_one_of_specified:
			pushResultsToOne(targets);
			break;

		default:
			break;
		}

		heldResults.clear();

		// Check if need to close this Tunnel
		if (source.isDeleted())
			close();

		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).isDeleted()) {
				close();
				break;
			}
		}
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

	private String targetsName;

	@Override
	public String getTargetsName() {
		if (targetsName != null)
			return targetsName;
		if (type == TunnelType.In_all || type == TunnelType.Go_all || type == TunnelType.Go_one_of_all || type == TunnelType.In_one_of_all || type == TunnelType.Out || type == TunnelType.Here) {
			targetsName = type.toString();
			return targetsName;
		}

		if (targets.size() == 0) {
			return null;
		} else if (targets.size() == 1) {
			targetsName = targets.get(0).getNameDim();
		} else {
			String split = "?";
			if (type == TunnelType.In_all_of_specified || type == TunnelType.In_all_of_specified)
				split = "&";
			else if (type == TunnelType.In_one_of_specified || type == TunnelType.In_one_of_specified) {
				split = "|";
			}
			targetsName = "";
			for (int i = 0; i < targets.size() - 1; i++) {
				targetsName += targets.get(i).getNameDim() + split;
			}
			targetsName += targets.get(targets.size() - 1).getNameDim();
		}
		return targetsName;
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

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(getType().toString() + " " + getTargetsName());
		return sBuilder.toString();
	}
}