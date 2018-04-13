package upsimulator.rules.conditions;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import upsimulator.exceptions.UnsupportedDimensionException;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;

/**
 * The boolean relationship of dimensions of rule
 * 
 * @author quan
 *
 */
public class BooleanCondition implements Dimension, Condition {

	@Override
	public void addDimension(Integer... dimensions) {
		throw new UnsupportedDimensionException(this, dimensions);
	}

	@SuppressWarnings("unchecked")
	public Condition deepClone() {
		// no need to deep clone dimensions
		try {
			BooleanCondition cloned = (BooleanCondition) super.clone();
			cloned.evaluator = null;
			cloned.dimensions = (LinkedList<String>) dimensions.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private LinkedList<String> dimensions = new LinkedList<>();

	@Override
	public void addDimension(String... formulas) {
		dimensions.add(formulas[0]);
		// for (String formula : formulas) {
		// dimensions.add(formula);
		// }
	}

	@Override
	public int dimensionCount() {
		return 1;
	}

	@Override
	public List<String> getDimensions() {
		return dimensions;
	}

	@Override
	public boolean satisfy(Membrane membrane) {
		boolean satisfied = true;
		String formula = dimensions.get(0);
		try {
			evaluator.parse("");
			if (!evaluator.getBooleanResult(formula)) {
				satisfied = false;
			}
		} catch (EvaluationException e) {
			System.err.println("bad expr=" + formula);
			e.printStackTrace();
		}

		return satisfied;
	}

	@Override
	public boolean fetch(Membrane membrane) {
		return true;
	}

	@Override
	public void withdrawFetch(Membrane membrane) {

	}

	private Evaluator evaluator;

	@Override
	public void setEval(Evaluator evaluator) {
		this.evaluator = evaluator;
	}

	@Override
	public String toString() {
		String string = dimensions.getFirst();
		if (dimensions.size() > 1)
			string += "&";
		for (int i = 1; i < dimensions.size(); i++)
			string += "&" + dimensions.get(i);
		return string;
	}

	@Override
	public Evaluator getEval() {
		return evaluator;
	}
}