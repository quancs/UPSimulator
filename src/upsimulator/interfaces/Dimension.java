package upsimulator.interfaces;

import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

public class Dimension {
	private String text;
	private Expression expression;
	private Object value;

	public Dimension(String text, Object value) {
		super();
		expression = AviatorEvaluator.compile(text, true);
		this.value = value;
		this.text = text;
	}

	public Dimension(String text) {
		super();
		expression = AviatorEvaluator.compile(text, true);
		this.text = text;
		if (expression.getVariableNames().size() == 0)
			value = expression.execute();
	}

	public Dimension(Dimension dimension) {
		super();
		this.expression = dimension.expression;
		this.value = dimension.value;
		this.text = dimension.text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		expression = AviatorEvaluator.compile(text, true);
		if (expression.getVariableNames().size() == 0)
			value = expression.execute();
	}

	public Object getValue() {
		return value;
	}

	public Long getLongValue() {
		return (Long) value;
	}

	public Boolean getBooleanValue() {
		return (Boolean) value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void fix(Map<String, Object> env) {
		try {
			value = expression.execute(env);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("bad expression: " + text);
			System.err.println("value map:" + env);
		}
	}

	public Dimension fixClone(Map<String, Object> env) {
		Dimension clone = new Dimension(this);
		clone.fix(env);
		return clone;
	}

	public boolean isFixed() {
		return value != null;
	}

	@Override
	public String toString() {
		if (isFixed()) {
			return value.toString();
		} else {
			return text;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dimension other = (Dimension) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
