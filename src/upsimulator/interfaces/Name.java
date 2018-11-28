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
	 * Get the dimension size of name
	 * 
	 * @return the dimension size
	 */
	public int getNameDimensionSize();

	/**
	 * Check if two names are the same class: names are the same and
	 * dimensions.sizes are the same
	 * 
	 * @param name
	 * @return true if yes
	 */
	public boolean nameEqualsTo(Name name);

	/**
	 * Check if two names are totally the same
	 * 
	 * @param name
	 * @return true if yes
	 */
	public boolean nameDimEqualsTo(Name name);

	/**
	 * Clone the name
	 * 
	 * @return name
	 */
	@Override
	public Name deepClone();
}
