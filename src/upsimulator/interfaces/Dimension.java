package upsimulator.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 * Defines the functions of classes which have dimensions
 * 
 * @author quan
 *
 */
public interface Dimension {

	/**
	 * Set evaluator
	 * 
	 * @param evaluator
	 *            evaluator used to calculate dimension
	 */
	public void setEval(Evaluator evaluator);

	/**
	 * Get the evaluator
	 * 
	 * @return the evaluator inside
	 */
	public Evaluator getEval();

	/**
	 * Add new integer dimensions to an instance of dimension.
	 * 
	 * @param dimensions
	 *            new dimensions
	 */
	public default void addDimension(Integer... dimensions) {
		for (Integer dim : dimensions)
			getDimensions().add(String.valueOf(dim));
	}

	/**
	 * Add new formula dimensions to an instance of dimension. For example, a[i+1]
	 * have a dimension {@code i+1} and {@code i+1} should be added to {@code a}.
	 * 
	 * @param formulas
	 *            Formula dimensions
	 */
	public default void addDimension(String... formulas) {
		for (String dim : formulas)
			getDimensions().add(dim);
	}

	/**
	 * Return the number of total dimensions
	 * 
	 * @return the number of total dimensions
	 */
	public default int dimensionCount() {
		return getDimensions().size();
	}

	/**
	 * Get all the dimensions of an instance
	 * 
	 * @return dimensions
	 */
	public List<String> getDimensions();

	/**
	 * Convert all the dimensions from {@code String} to {@code Integer}
	 * 
	 * @return Integer dimension list
	 */
	public default List<Integer> getIntDimensions() {
		List<String> dims = getDimensions();
		ArrayList<Integer> intDims = new ArrayList<>();
		for (String dim : dims)
			intDims.add(Integer.parseInt(dim));
		return intDims;
	}

	/**
	 * Calculate all the formula dimensions and fix them
	 */
	public default void fixDimension() {
		List<String> fixedDim = new ArrayList<>();
		for (String dim : getDimensions()) {
			fixedDim.add(fix(dim));
		}
		getDimensions().clear();
		getDimensions().addAll(fixedDim);
	}

	/**
	 * Fix a formula by calculating it using its own evaluator
	 * 
	 * @param str
	 *            Formula dimension
	 * @return Calculated value
	 */
	public default String fix(String str) {
		try {
			return getEval().evaluate(str);
		} catch (EvaluationException e) {
			e.printStackTrace();
			return str;
		}
	}
}
