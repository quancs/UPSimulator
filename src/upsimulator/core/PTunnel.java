package upsimulator.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.UPSLogger;
import upsimulator.rules.results.DelayedResult;

/**
 * PTunnel is a connection between membranes, used to transfer results.
 * 
 * @author quan
 *
 */
public class PTunnel implements Tunnel {
	private static final long serialVersionUID = -7893633024003440550L;

	private Membrane source;
	private ArrayList<Membrane> targets;
	private String name;
	private boolean isOpen;
	private TunnelType type;
	private HashMap<Result, Integer> heldResults;
	private HashMap<DelayedResult, Integer> delayedResults;

	public PTunnel(TunnelType type) {
		heldResults = new HashMap<>();
		delayedResults = new HashMap<>();
		targets = new ArrayList<Membrane>();
		this.type = type;
		open();
	}

	public PTunnel() {
		heldResults = new HashMap<>();
		delayedResults = new HashMap<>();
		targets = new ArrayList<Membrane>();
		this.type = TunnelType.Here;
		open();
	}

	@Override
	public void setType(TunnelType type) {
		this.type = type;
	}

	@Override
	public TunnelType getType() {
		return type;
	}

	@Override
	public String getName() {
		if (name == null && source != null && targets.size() > 0) {
			name = source.getNameDim() + "->" + getTargetsName();
		}
		return name;
	}

	@Override
	public Membrane getSource() {
		return source;
	}

	@Override
	public void setSource(Membrane source) {
		this.source = source;
	}

	@Override
	public List<Membrane> getTargets() {
		return targets;
	}

	@Override
	public void addTarget(Membrane target) {
		targets.add(target);
		targets.sort(new Comparator<Membrane>() {
			@Override
			public int compare(Membrane o1, Membrane o2) {
				return o1.getNameDim().compareTo(o2.getNameDim());
			}
		});
	}

	@Override
	public void holdResult(Result result, int times) {
		if (result instanceof DelayedResult && ((DelayedResult) result).getDelay() > 0) {
			if (delayedResults.containsKey(result)) {
				delayedResults.put((DelayedResult) result, delayedResults.get(result) + times);
			} else
				delayedResults.put((DelayedResult) result, times);
		} else if (heldResults.containsKey(result)) {
			heldResults.put(result, heldResults.get(result) + times);
		} else
			heldResults.put(result, times);
	}

	@Override
	public Map<Result, Integer> getHeldResults() {
		return heldResults;
	}

	private final void pushResultsTo(Membrane membrane) {
		try {
			Membrane targetMembrane = membrane;
			if (membrane.isDeleted()) {// push result to parent if membrane is deleted
				for (targetMembrane = targetMembrane.getParent(); targetMembrane != null
						&& targetMembrane.isDeleted() == true; targetMembrane = targetMembrane.getParent())
					;
				if (targetMembrane == null)
					targetMembrane = membrane;
			}

			for (Map.Entry<Result, Integer> entry : heldResults.entrySet()) {
				Result result = entry.getKey();
				result.setResult(targetMembrane, entry.getValue());
			}
		} catch (UnknownMembraneClassException e) {
			e.printStackTrace();
			UPSLogger.error(this, e);
		}
	}

	private final void pushResultsToAll(List<Membrane> membranes) {
		for (Membrane sm : membranes)
			pushResultsTo(sm);
	}

	private final void pushResultsToOne(List<Membrane> membranes) {
		// TODO maybe have some bugs in the function
		// 因为与含义不符合，多个结果只能到达一个膜
		Membrane target = membranes.get((int) (Math.random() * membranes.size()));
		pushResultsTo(target);
	}

	@Override
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
		for (Iterator<Map.Entry<DelayedResult, Integer>> it = delayedResults.entrySet().iterator(); it.hasNext();) {
			Map.Entry<DelayedResult, Integer> item = it.next();
			item.getKey().reduceDelay();
			if (item.getKey().getDelay() == 0) {
				heldResults.put(item.getKey(), item.getValue());
				it.remove();
			}
		}

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

	@Override
	public void open() {
		isOpen = true;
	}

	@Override
	public void close() {
		isOpen = false;
	}

	@Override
	public boolean isOpen() {
		if (isOpen == false)
			return false;
		else {
			for (Membrane target : targets) {
				if (target.isDeleted()) {
					isOpen = false;
					return false;
				}
			}
		}
		return isOpen;
	}

	private String targetsName;

	@Override
	public String getTargetsName() {
		if (targetsName != null)
			return targetsName;
		if (type == TunnelType.In_all || type == TunnelType.Go_all || type == TunnelType.Go_one_of_all
				|| type == TunnelType.In_one_of_all || type == TunnelType.Out || type == TunnelType.Here) {
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

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(getTargetsName());
		return sBuilder.toString();
	}
}