package upsimulator.interfaces;

public interface Name extends Dimensional {
	/**
	 * Set the name
	 * 
	 * @param name
	 *            name of object, rule, or membrane
	 */
	public void setName(String name);

	/**
	 * Get the name with dimensions. For example, object a has a dimension
	 * {@code 2}, then the return value is {@code a[2]}.
	 * 
	 * @return string combined name and dimensions
	 */
	public String getNameDim();

	/**
	 * Get the name
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Clone the name
	 * 
	 * @return name
	 */
	public Name deepClone();
}
