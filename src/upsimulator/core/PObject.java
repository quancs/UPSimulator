package upsimulator.core;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import upsimulator.interfaces.Obj;

/**
 * PObject represents the object in P systems.<br>
 * 
 * @author quan
 *
 */
public class PObject implements Obj {
	private static final long serialVersionUID = 918301397917102425L;

	private String name;
	private ArrayList<String> dimensions;
	private List<Integer> intDimensions;
	private Evaluator eval;

	public PObject(String name, String... dims) {
		this.name = name;
		dimensions = new ArrayList<>();
		for (String dim : dims)
			dimensions.add(dim);
		intDimensions = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PObject deepClone() {
		// no need to clone name and dimensions, they won't change.
		PObject cloned = null;
		try {
			cloned = (PObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		cloned.eval = null;
		cloned.dimensions = (ArrayList<String>) dimensions.clone();
		cloned.intDimensions = null;
		return cloned;
	}

	public PObject() {
		this.name = "NoType";
		dimensions = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public PObject(PObject object) {
		this.name = object.name;
		if (object.eval != null) {
			this.dimensions = new ArrayList<>();
			for (String dimension : object.dimensions)
				try {
					this.dimensions.add((int) object.eval.getNumberResult(dimension) + "");
				} catch (EvaluationException e) {
					e.printStackTrace();
				}
		} else {
			this.dimensions = (ArrayList<String>) object.dimensions.clone();
		}
	}

	@Override
	public String toString() {
		return getNameDim();
	}

	/**
	 * 如果不设置eval，则name= name + dimensions 如果设置了eval，则name= name +
	 * eval.evaluate(dimensions)
	 */
	@Override
	public String getNameDim() {
		StringBuilder name = new StringBuilder(getName());
		if (eval != null && dimensions != null) {
			try {
				for (int i = 0; i < dimensions.size(); i++) {
					String formula = dimensions.get(i);
					int dimension;
					dimension = (int) Double.parseDouble(eval.evaluate(formula));
					name.append("[" + dimension + "]");
				}
			} catch (Exception e) {
				name.delete(getName().length(), name.length());
				for (int j = 0; j < dimensions.size(); j++) {
					name.append("[" + dimensions.get(j) + "]");
				}
			}
		} else if (dimensions != null) {
			for (int i = 0; i < dimensions.size(); i++) {
				name.append("[" + dimensions.get(i) + "]");
			}
		}
		return name.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addDimension(String... formulas) {
		for (String f : formulas)
			dimensions.add(f);
	}

	@Override
	public void addDimension(Integer... dimensions) {
		for (Integer d : dimensions)
			addDimension(d + "");
	}

	@Override
	public void setEval(Evaluator eval) {
		this.eval = eval;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Obj))
			return false;
		PObject other = (PObject) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!getNameDim().equals(other.getNameDim()))
			return false;
		return true;
	}

	@Override
	public int dimensionCount() {
		return dimensions.size();
	}

	@Override
	public List<String> getDimensions() {
		return dimensions;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * fix the dimensions to ensure the dimensions are not variables but numbers.
	 */
	@Override
	public void fixDimension() {
		Obj.super.fixDimension();// 对象的维度不能删除，因为带维度的规则需要其维度来判断可用的具体规则
	}

	@Override
	public Evaluator getEval() {
		return eval;
	}

	@Override
	public List<Integer> getIntDimensions() {
		if (intDimensions == null)
			intDimensions = Obj.super.getIntDimensions();
		return intDimensions;
	}
}
