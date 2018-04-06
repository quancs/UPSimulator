package upsimulator.rules.results;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownTargetMembraneException;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.UPSLogger;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.rules.conditions.MembranePropertyCondition;

//既是结果也是条件
public class PositionResult implements Result, Dimension, Condition {

	public class Target {
		public String name;
		public String nameDim;
		public ArrayList<String> formulaDims = new ArrayList<>();
		public ArrayList<MembranePropertyCondition> conditions = new ArrayList<>();

		private String unfixedNameDim;

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
	public void setResult(Membrane membrane) throws UnknownTargetMembraneException {
		for (ObjectResult or : ors)
			or.setResult(membrane);
	}

	private PositionResult cloned = null;

	@SuppressWarnings("unchecked")
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

	@Override
	public int getPriority() {
		return 1;
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
	public void setEval(Evaluator evaluator) {
		this.evaluator = evaluator;
		for (ObjectResult or : ors)
			or.setEval(evaluator);
	}

	@Override
	public void addDimension(Integer... dimensions) {

	}

	@Override
	public void addDimension(String... formulas) {

	}

	@Override
	public int dimensionCount() {
		return 1;
	}

	@Override
	public List<String> getDimensions() {
		return null;
	}

	private String getNameDim() throws EvaluationException {
		return getTargetsName();
	}

	@Override
	public void fixDimension() {
		if (fixed)
			return;

		for (ObjectResult or : ors)
			if (or instanceof Dimension)
				or.fixDimension();

		ArrayList<String> nameDims = new ArrayList<>();
		for (Target target : targets) {
			target.nameDim = target.name;
			for (String dim : target.formulaDims) {
				try {
					target.nameDim += "[" + evaluator.evaluate(dim) + "]";
				} catch (EvaluationException e) {
					e.printStackTrace();
					UPSLogger.error(this, e.getStackTrace().toString());
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

	@Override
	public Evaluator getEval() {
		return evaluator;
	}

	private final boolean satisfy(Membrane membrane, List<MembranePropertyCondition> mpcs) {
		for (MembranePropertyCondition mpc : mpcs) {
			if (!mpc.satisfy(membrane))
				return false;
		}
		return true;
	}

	private final boolean satisfyAll(List<Target> targets, List<Membrane> membranes) {
		for (Membrane membrane : membranes) {
			boolean satisfy = false;
			for (Target target : targets) {
				if (target.nameDim.equals(membrane.getNameDim())) {
					satisfy = satisfy(membrane, target.conditions);
					break;
				}
			}
			if (satisfy == false)
				return false;
		}
		return true;
	}

	private final boolean satisfyOne(List<Target> targets, List<Membrane> membranes) {
		for (Membrane membrane : membranes) {
			boolean satisfy = false;
			for (Target target : targets) {
				if (target.nameDim.equals(membrane.getNameDim())) {
					satisfy = satisfy(membrane, target.conditions);
					break;
				}
			}
			if (satisfy)
				return true;
		}
		return false;
	}

	// private Membrane satisfiedMem;

	// Check MembranePropertyCondition
	@Override
	public boolean satisfy(Membrane membrane) {
		if (checkSubmembraneProp == false || move == TunnelType.Here || move == TunnelType.Out)
			return true;

		switch (move) {
		case Go_one_of_all:
		case Go_all:
		case In_one_of_all:
		case In_all:
			return true;
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
		return false;
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
			return targets.get(0).nameDim;
		case In_all_of_specified:
		case Go_all_of_specified: {
			String targetsName = "";
			for (int i = 0; i < targets.size() - 1; i++) {
				Target target = targets.get(i);
				targetsName += target.nameDim + "&";
			}
			targetsName += targets.get(targets.size() - 1).nameDim;
			return targetsName;
		}
		case In_one_of_specified:
		case Go_one_of_specified: {
			String targetsName = "";
			for (int i = 0; i < targets.size() - 1; i++) {
				Target target = targets.get(i);
				targetsName += target.nameDim + "|";
			}
			targetsName += targets.get(targets.size() - 1).nameDim;
			return targetsName;
		}
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean fetch(Membrane membrane) {
		return true;
	}

	@Override
	public void withdrawFetch(Membrane membrane) {
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		switch (move) {
		case Here:
		case Out:
		case In_all:
		case In_one_of_all:
		case Go_all:
		case Go_one_of_all:
			return current.getTunnel(move, null);

		case Go:
		case In:
		case In_all_of_specified:
		case In_one_of_specified:
		case Go_all_of_specified:
		case Go_one_of_specified:
			try {
				return current.getTunnel(move, getTargetsName());
			} catch (EvaluationException e) {
				e.printStackTrace();
				UPSLogger.error(this, e.toString());
			}

		default:
			break;
		}
		return null;
	}
}
