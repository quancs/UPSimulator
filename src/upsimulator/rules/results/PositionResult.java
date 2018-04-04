package upsimulator.rules.results;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jeval.Evaluator;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownTargetMembraneException;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.rules.conditions.MembranePropertyCondition;

//既是结果也是条件
public class PositionResult implements Result, Dimension, Condition {

	public static final int in = 1;
	public static final int here = 0;
	public static final int out = 2;

	private int move;

	private String membraneName;
	private ArrayList<ObjectResult> ors = new ArrayList<>();
	private ArrayList<MembranePropertyCondition> mpConditions = new ArrayList<>();// 子膜应当满足的膜属性条件

	public PositionResult() {
		super();
	}

	public PositionResult(int move, String membraneName) {
		this.move = move;
		this.membraneName = membraneName;
	}

	public void addObjectResult(ObjectResult or) {
		if (or == null) {
			System.err.println(" null ");
		}
		ors.add(or);
	}

	public void addMemPropCondition(MembranePropertyCondition mpc) {
		mpConditions.add(mpc);
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
				cloned.dimensions = (ArrayList<String>) dimensions.clone();
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

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public String getMembraneName() {
		return membraneName;
	}

	public void setMembraneName(String membraneName) {
		this.membraneName = membraneName;
	}

	@Override
	public String toString() {
		String mString = null;
		switch (move) {
		case in:
			mString = "in " + getNameDim();
			break;
		case out:
			mString = "out";
			break;
		case here:
			mString = "here";
			break;
		default:
			break;
		}

		String string = "( ";
		for (ObjectResult or : ors)
			string += or + " ";
		string += " ," + mString + ")";
		return string;
	}

	private Evaluator evaluator;

	@Override
	public void setEval(Evaluator evaluator) {
		this.evaluator = evaluator;
		for (ObjectResult or : ors)
			or.setEval(evaluator);
	}

	private ArrayList<String> dimensions = new ArrayList<>();

	@Override
	public void addDimension(Integer... dimensions) {
		for (Integer d : dimensions)
			this.dimensions.add(String.valueOf(d));
	}

	@Override
	public void addDimension(String... formulas) {
		for (String d : formulas)
			dimensions.add(d);
	}

	@Override
	public int dimensionCount() {
		return dimensions.size();
	}

	@Override
	public List<String> getDimensions() {
		return dimensions;
	}

	private String getNameDim() {
		StringBuilder name = new StringBuilder(membraneName);
		if (evaluator != null && dimensions != null) {
			try {
				for (int i = 0; i < dimensions.size(); i++) {
					String formula = dimensions.get(i);
					int dimension;
					dimension = (int) Double.parseDouble(evaluator.evaluate(formula));
					name.append("[" + dimension + "]");
				}
			} catch (Exception e) {
				name.delete(membraneName.length(), name.length());
				for (int j = 0; j < dimensions.size(); j++)
					name.append("[" + dimensions.get(j) + "]");
			}
		} else if (dimensions != null) {
			for (int i = 0; i < dimensions.size(); i++)
				name.append("[" + dimensions.get(i) + "]");
		}
		return name.toString();
	}

	@Override
	public void fixDimension() {
		for (ObjectResult or : ors)
			if (or instanceof Dimension)
				or.fixDimension();

		Dimension.super.fixDimension();
	}

	@Override
	public Evaluator getEval() {
		return evaluator;
	}

	private ArrayList<Membrane> satisfiedSons = new ArrayList<>();

	@Override
	public boolean satisfy(Membrane membrane) {
		if (move == in) {
			satisfiedSons.clear();
			String nameDim = getNameDim();// 有可能是带有维度的膜
			for (Membrane son : membrane.getChildren()) {
				if (son.getNameDim().equals(nameDim)) {// 允许重名膜的出现
					boolean satisfied = true;
					for (MembranePropertyCondition mpc : mpConditions) {
						if (!mpc.satisfy(son)) {
							satisfied = false;
							break;
						}
					}
					if (satisfied)
						satisfiedSons.add(son);
				}
			}
			return satisfiedSons.size() > 0;
		} else {
			return true;
		}
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
		case in:
			int index = (int) Math.round(Math.random() * (satisfiedSons.size() - 1));
			for (Tunnel t : current.getTunnels())
				if (t.getTargetsName() == satisfiedSons.get(index).getNameDim())
					return t;
			throw new TunnelNotExistException(this.toString() + " wants " + satisfiedSons.get(index).getNameDim() + ", which is not in " + current.getTunnels());
		case out:
			Tunnel tunnel = current.getTunnel(TunnelType.Out, null);
			if (tunnel == null) {
				throw new TunnelNotExistException(TunnelType.Out + " tunnel does not exist in " + current);
			} else {
				return tunnel;
			}
		case here:
			Tunnel tunnel1 = current.getTunnel(TunnelType.Here, null);
			if (tunnel1 == null) {
				throw new TunnelNotExistException(TunnelType.Here + " tunnel does not exist in " + current);
			} else {
				return tunnel1;
			}
		default:
			break;
		}

		return null;
	}
}
