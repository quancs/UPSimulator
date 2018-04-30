package upsimulator.rules.results;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import upsimulator.core.PTunnel;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.interfaces.BaseDimensional;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Dimensional;
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
public class PositionResult extends BaseDimensional implements Result, Condition {

	public class Target implements Cloneable {
		public String name;
		public String nameDim;
		public ArrayList<String> formulaDims = new ArrayList<>();
		public ArrayList<MembranePropertyCondition> conditions = new ArrayList<>();

		private String unfixedNameDim;

		public String getNameDim() {
			if (nameDim == null)
				return name;
			return nameDim;
		}

		public Target deepClone() {
			try {
				Target cloned = (Target) this.clone();
				cloned.conditions = new ArrayList<>(conditions.size());
				for (MembranePropertyCondition condition : conditions)
					cloned.conditions.add(condition.deepClone());
				return cloned;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public String toString() {
			if (nameDim != null) {
				String string = nameDim;
				for (MembranePropertyCondition mpc : conditions)
					string += mpc.toString();
				return string;
			}

			if (unfixedNameDim == null) {
				unfixedNameDim = name;
				for (String fString : formulaDims)
					unfixedNameDim += "[" + fString + "]";
				for (MembranePropertyCondition mpc : conditions)
					unfixedNameDim += mpc.toString();
			}
			return unfixedNameDim;
		}
	}

	private TunnelType move;
	private ArrayList<Target> targets = new ArrayList<>();
	private ArrayList<ObjectResult> ors = new ArrayList<>();
	private boolean checkSubmembraneProp;
	private boolean fixed;

	public PositionResult() {
		super();
		move = TunnelType.Here;
		checkSubmembraneProp = false;
		fixed = false;
	}

	public PositionResult(TunnelType move) {
		this.move = move;
		checkSubmembraneProp = false;
		fixed = false;
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

	private PositionResult cloned = null;

	@Override
	public PositionResult deepClone() {
		try {
			/* 因为PositionResult既是Result、又是Condition，因此此方法会被调用两次，第一次clone，第二次直接返回 */
			if (cloned == null) {
				// MemPropertyCondition没必要clone
				cloned = (PositionResult) super.clone();
				cloned.ors = new ArrayList<>();
				for (ObjectResult or : ors)
					cloned.addObjectResult(or.deepClone());
				cloned.evaluator = null;
				cloned.targets = new ArrayList<>(targets.size());
				for (Target target : targets)
					cloned.targets.add(target.deepClone());
				return cloned;
			} else {
				PositionResult temp = cloned;
				cloned = null;
				return temp;
			}
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
				sBuilder.append("&");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		case Go_all_of_specified: {
			StringBuilder sBuilder = new StringBuilder("go ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append("&");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		case In_one_of_specified: {
			StringBuilder sBuilder = new StringBuilder("in ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append("|");
			}
			sBuilder.append(targets.get(targets.size() - 1).toString());
			position = sBuilder.toString();
			break;
		}
		case Go_one_of_specified: {
			StringBuilder sBuilder = new StringBuilder("go ");
			for (int i = 0; i < targets.size() - 1; i++) {
				sBuilder.append(targets.get(i).toString());
				sBuilder.append("|");
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

	private Evaluator evaluator;

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
		try {
			return getTargetsName();
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void fix(Map<String, Object> mappedValues) {
		if (fixed)
			return;

		for (ObjectResult or : ors)
			if (or instanceof Dimensional)
				or.fix(mappedValues);

		ArrayList<String> nameDims = new ArrayList<>();
		for (Target target : targets) {
			target.nameDim = target.name;
			for (String dim : target.formulaDims) {
				try {
					target.nameDim += "[" + evaluator.evaluate(dim) + "]";
				} catch (EvaluationException e) {
					e.printStackTrace();
					UPSLogger.error(this, e);
				}
			}
			nameDims.add(target.nameDim);
		}
		nameDims.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		fixed = true;
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
				if (target.nameDim.equals(membrane.getNameDim())) {
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
				if (target.nameDim.equals(membrane.getNameDim())) {
					int satisfy = satisfy(membrane, target.conditions);
					if (satisfy > 0)
						return satisfy;
					break;
				}
			}
		}
		return 0;
	}

	// private Membrane satisfiedMem;

	// Check MembranePropertyCondition
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

	private final String getTargetsName() throws EvaluationException {
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
			try {
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
			} catch (EvaluationException e) {
				e.printStackTrace();
				UPSLogger.error(this, e);
			}

		default:
			break;
		}
		if (tunnel == null) {
			try {
				UPSLogger.error(this, "Tunnel does not exist and cannot be created : " + getTargetsName());
				throw new TunnelNotExistException(current, move, getTargetsName());
			} catch (EvaluationException e) {
				e.printStackTrace();
				UPSLogger.error(this, e);
			}
			return null;
		} else
			return tunnel;
	}
}
