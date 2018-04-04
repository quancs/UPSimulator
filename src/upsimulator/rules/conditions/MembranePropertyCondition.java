package upsimulator.rules.conditions;

import org.apache.log4j.Logger;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

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
	public boolean satisfy(Membrane membrane) {
		if (membrane.getProperty(name) == null) {
			logger.error("膜" + membrane + "不存在属性" + name);
			return false;
		} else
			return membrane.getProperty(name).equals(value);
	}

	@Override
	public boolean fetch(Membrane membrane) {
		return true;
	}

	@Override
	public void withdrawFetch(Membrane membrane) {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "m." + getName() + "==" + getValue();
	}

}
