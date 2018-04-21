package upsimulator.rules.conditions;

import org.apache.log4j.Logger;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

/**
 * Membrane property represents the property condition of one rule
 * 
 * @author quan
 *
 */
public class MembranePropertyCondition implements Condition {

	private static Logger logger = Logger.getLogger(MembranePropertyCondition.class);

	private String name, value;

	public MembranePropertyCondition(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public MembranePropertyCondition deepClone() {
		try {
			return (MembranePropertyCondition) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int satisfy(Membrane membrane) {
		if (membrane.getProperty(name) == null) {
			logger.error("膜" + membrane + "不存在属性" + name);
			return 0;
		} else
			return membrane.getProperty(name).equals(value) ? Integer.MAX_VALUE : 0;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		return times;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
	}

	/**
	 * Return property name of this condition
	 * 
	 * @return property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of property
	 * 
	 * @param name
	 *            property name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the value of property
	 * 
	 * @return property value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the property value of this condition
	 * 
	 * @param value
	 *            property value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "<" + getName() + "=" + getValue() + ">";
	}

}
