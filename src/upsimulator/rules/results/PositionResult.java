package upsimulator.rules.results;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import upsimulator.core.PTunnel;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.interfaces.BasicName;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.interfaces.UPSLogger;
import upsimulator.rules.conditions.MembranePropertyCondition;

/**
 * A object result to a target membrane
 * 
 * @author quan
 *
 */
public class PositionResult extends BasicName implements Result, Condition {

	public class Target extends BasicName implements Cloneable {
		public ArrayList<MembranePropertyCondition> conditions = new ArrayList<>();

		public Target() {
		}
		
		public Target(Target target) {
			super(target);

			conditions = new ArrayList<>(target.conditions.size());
			for (MembranePropertyCondition condition : target.conditions)
				conditions.add(condition.deepClone());
		}
		
		@Override
		public Target deepClone() {
			if(isFixed())
				return this;
			return new Target(this);
		}

		@Override
		public String toString() {
			String string = getNameDim();
			for (MembranePropertyCondition mpc : conditions)
				string += mpc.toString();
			return string;
		}
	}

	private TunnelType move;
	private ArrayList<Target> targets = new ArrayList<>();
	private ArrayList<ObjectResult> ors = new ArrayList<>();
	private boolean checkSubmembraneProp;

	public PositionResult() {
		super();
		move = TunnelType.Here;
		checkSubmembraneProp = false;
	}

	public PositionResult(TunnelType move) {
		this.move = move;
		checkSubmembraneProp = false;
	}

	public void addObjectResult(ObjectResult or) {
		ors.add(or);
	}

	public void addTarget(Target target) {
		targets.add(target);
		if (target.conditions.size() > 0)
			checkSubmembraneProp = true;
	}

	@Override
	public void setResult(Membrane membrane, int times) {
		for (ObjectResult or : ors)
			or.setResult(membrane, times);
	}

	@Override
	public PositionResult deepClone() {
		try {
			// MemPropertyCondition没必要clone
			PositionResult cloned = (PositionResult) super.clone();
			cloned.ors = new ArrayList<>();
			for (ObjectResult or : ors)
				cloned.addObjectResult(or.deepClone());
			cloned.targets = new ArrayList<>(targets.size());
			for (Target target : targets)
				cloned.targets.add(target.deepClone());
			return cloned;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TunnelType getMove() {
		return move;
	}

	public void setMove(TunnelType move) {
		this.move = move;
	}

	public ArrayList<Target> getTargets() {
		return targets;
	}

	@Override
	public String toString() {
		String position = null;
		switch (move) {
		case Here:
			position = "here";
			break;
		case Out:
			position = "out";
			break;
		case In_all:
			position = "in all";
			break;
		case Go_all:
			position = "go all";
			break;
		case Go_one_of_all:
			position = "go random";
			break;
		case In_one_of_all:
			position = "in random";
			break;
		case In:
			position = "in " + targets.get(0).toString();
			break;
		case Go:
			position = "go " + targets.get(0).toString();
			break;
		case In_all_of_specified: {
			StringBuilder sBuilder = new StringBuilder("in ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append(" & ");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		case Go_all_of_specified: {
			StringBuilder sBuilder = new StringBuilder("go ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append(" & ");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		case In_one_of_specified: {
			StringBuilder sBuilder = new StringBuilder("in ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append(" | ");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		case Go_one_of_specified: {
			StringBuilder sBuilder = new StringBuilder("go ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append(" | ");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		default:
			break;
		}

		String string = "(";
		for (ObjectResult or : ors)
			string += " " + or;
		string += ", " + position + ")";
		return string;
	}

	@Override
	public void addDimension(Long dimensions) {

	}

	@Override
	public void addDimension(String formulas) {

	}

	@Override
	public int getDimensionSize() {
		return 1;
	}

	@Override
	public ArrayList<Dimension> getDimensions() {
		return null;
	}

	@Override
	public String getNameDim() {
		return getTargetsName();
	}

	@Override
	public boolean isFixed() {
		boolean allFixed = true;
		for (Target target : targets) {
			if (!target.isFixed())
				allFixed = false;
		}

		for (ObjectResult objectResult : ors)
			if (!objectResult.isFixed())
				allFixed = false;

		if (!super.isFixed())
			allFixed = false;
		return allFixed;
	}

	@Override
	public void fix(Map<String, Object> mappedValues) {
		if (isFixed())
			return;

		for (ObjectResult or : ors)
			or.fix(mappedValues);

		for (Target target : targets)
			target.fix(mappedValues);

	}

	private final int satisfy(Membrane membrane, List<MembranePropertyCondition> mpcs) {
		int min = Integer.MAX_VALUE;

		for (MembranePropertyCondition mpc : mpcs) {
			int mpcc = mpc.satisfy(membrane);
			if (mpcc < min)
				min = mpcc;
			if (mpcc == 0)
				return 0;
		}
		return min;
	}

	private final int satisfyAll(List<Target> targets, List<Membrane> membranes) {
		int min = Integer.MAX_VALUE;
		for (Membrane membrane : membranes) {
			int satisfy = 0;
			for (Target target : targets) {
				if (target.getNameDim().equals(membrane.getNameDim())) {
					satisfy = satisfy(membrane, target.conditions);
					break;
				}
			}
			if (satisfy == 0)
				return 0;
			if (satisfy < min)
				min = satisfy;
		}
		return min;
	}

	private final int satisfyOne(List<Target> targets, List<Membrane> membranes) {
		for (Membrane membrane : membranes) {
			for (Target target : targets) {
				if (target.getNameDim().equals(membrane.getNameDim())) {
					int satisfy = satisfy(membrane, target.conditions);
					if (satisfy > 0)
						return satisfy;
					break;
				}
			}
		}
		return 0;
	}

	@Override
	public int satisfy(Membrane membrane) {
		if (checkSubmembraneProp == false || move == TunnelType.Here || move == TunnelType.Out)
			return Integer.MAX_VALUE;

		switch (move) {
		case Go_one_of_all:
		case Go_all:
		case In_one_of_all:
		case In_all:
			return Integer.MAX_VALUE;
		case In:
			return satisfyAll(targets, membrane.getChildren());
		case Go:
			return satisfyAll(targets, membrane.getNeighbors());
		case In_all_of_specified:
			return satisfyAll(targets, membrane.getChildren());
		case Go_all_of_specified:
			return satisfyAll(targets, membrane.getNeighbors());
		case In_one_of_specified:
			return satisfyOne(targets, membrane.getChildren());
		case Go_one_of_specified:
			return satisfyOne(targets, membrane.getNeighbors());

		default:
			break;
		}
		return 0;
	}

	private final String getTargetsName() {
		switch (move) {
		case Here:
		case Out:
		case In_all:
		case Go_all:
		case Go_one_of_all:
		case In_one_of_all:
			return move.toString();
		case In:
		case Go:
			return targets.get(0).getNameDim();
		case In_all_of_specified:
		case Go_all_of_specified: {
			String targetsName = "";
			for (int i = 0; i < targets.size() - 1; i++) {
				Target target = targets.get(i);
				targetsName += target.getNameDim() + " & ";
			}
			targetsName += targets.get(targets.size() - 1).getNameDim();
			return targetsName;
		}
		case In_one_of_specified:
		case Go_one_of_specified: {
			String targetsName = "";
			for (int i = 0; i < targets.size() - 1; i++) {
				Target target = targets.get(i);
				targetsName += target.getNameDim() + " | ";
			}
			targetsName += targets.get(targets.size() - 1).getNameDim();
			return targetsName;
		}
		default:
			break;
		}
		return null;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		return times;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		Tunnel tunnel = null;
		switch (move) {
		case Here:
		case Out:
		case In_all:
		case In_one_of_all:
		case Go_all:
		case Go_one_of_all:
			tunnel = current.getTunnel(move, null);
			if (tunnel == null && move != TunnelType.Out) {
				tunnel = new PTunnel(move);
				tunnel.setSource(current);
				if (move == TunnelType.Here)
					tunnel.addTarget(current);
				current.addTunnel(tunnel);
			}
			break;
		case In:
		case In_all_of_specified:
		case In_one_of_specified:
		case Go:
		case Go_all_of_specified:
		case Go_one_of_specified:
			tunnel = current.getTunnel(move, getTargetsName());
			if (tunnel == null) {
				List<Membrane> scope;
				if (move == TunnelType.In || move == TunnelType.In_all_of_specified || move == TunnelType.In_one_of_specified) {
					scope = current.getChildren();
				} else {
					scope = current.getNeighbors();
				}
				LinkedList<Membrane> targetsMem = new LinkedList<>();
				for (Target target : targets) {
					for (Membrane m : scope) {
						if (target.getNameDim().equals(m.getNameDim())) {
							targetsMem.add(m);
							break;
						}
					}
				}
				if (targetsMem.size() == targets.size()) {
					tunnel = new PTunnel(move);
					tunnel.setSource(current);
					for (Membrane target : targetsMem)
						tunnel.addTarget(target);
					current.addTunnel(tunnel);
				}
			}
			break;

		default:
			break;
		}
		if (tunnel == null) {
			System.err.println(current.toString());
			System.err.println(move);
			System.err.println(getTargetsName());
			System.err.println(this.toString());
			TunnelNotExistException exception = new TunnelNotExistException(current, move, getTargetsName(), this);
			UPSLogger.error(this, exception.getMessage());
			throw exception;
		} else
			return tunnel;
	}
}
