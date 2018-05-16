package upsimulator.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Defines the functions of classes which have dimensions
 * 
 * @author quan
 *
 */
public interface Dimensional {

	/**
	 * Add new dimensions to an instance of {@code Dimensional}. For example, a[i+1]
	 * have a dimension {@code i+1} and {@code i+1} should be added to {@code a}.
	 * 
	 * @param dimension
	 *            dimensions
	 */
	public void addDimension(Dimension dimension);

	/**
	 * Add new fixed dimensions to an instance of {@code Dimensional}. For example,
	 * a[1] have a dimension {@code 1} and {@code 1} should be added to {@code a}.
	 * 
	 * @param dimension
	 *            dimensions
	 */
	public void addDimension(Long dimension);

	/**
	 * Add new formula dimensions to an instance of dimension. For example, a[i+1]
	 * have a dimension {@code i+1} and {@code i+1} should be added to {@code a}.
	 * 
	 * @param dimension
	 *            dimensions
	 */
	public void addDimension(String dimension);

	/**
	 * Get all the dimensions of an instance
	 * 
	 * @return dimensions
	 */
	public List<Dimension> getDimensions();

	/**
	 * Fix all the dimension by calculating them using the mapped values
	 * 
	 * @param mappedValues
	 *            mapped values
	 */
	public void fix(Map<String, Object> mappedValues);

	/**
	 * Check if all the dimensions are fixed
	 * 
	 * @return {@code true} if all the dimensions are fixed, and if
	 *         {@code getDimensionSize()==0 } return {@code true}, else return
	 *         {@code false}
	 */
	public boolean isFixed();

	/**
	 * Return the number of total dimensions
	 * 
	 * @return the number of total dimensions
	 */
	public int getDimensionSize();

	/**
	 * Get the i-th dimension
	 * 
	 * @param i
	 *            index of dimension wanted
	 * @return i-th dimension
	 */
	public Dimension get(int i);

	/**
	 * Deep clone a {@code Dimensional}
	 * 
	 * @return cloned Dimensional
	 */
	public Dimensional deepClone();

	/**
	 * Predict the possible values in this membrane
	 * 
	 * @param membrane
	 *            targetMembrane
	 * @param dList
	 *            the list of dimensions which want to be predicted their value
	 * @return possible value list for all dimensions. If dimension is fixed, null
	 *         will be returned
	 */
	public List<Long[]> predictPossibleValue(Membrane membrane, List<Dimension> dList);
}
