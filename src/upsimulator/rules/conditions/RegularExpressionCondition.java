package upsimulator.rules.conditions;

import java.util.regex.Pattern;

import upsimulator.core.PObject;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

public class RegularExpressionCondition implements Condition {
	private Pattern pattern;
	private boolean positive;
	public static PObject positiveObject = new PObject("a"), negativeObject = new PObject("-a");

	public RegularExpressionCondition(String pattern) {
		super();
		if (pattern.contains(negativeObject.toString()))
			positive = false;
		else if (pattern.contains(positiveObject.toString()))
			positive = true;
		else
			throw new IllegalArgumentException("Spike symbol must be " + positiveObject + " or " + negativeObject + ": " + pattern);

		this.pattern = Pattern.compile(pattern);
	}

	private String genStr(int length, String cha) {
		StringBuilder sBuilder = new StringBuilder(length * cha.length());
		for (int i = 0; i < length; i++)
			sBuilder.append(cha);
		return sBuilder.toString();
	}

	@Override
	public int satisfy(Membrane membrane) {
		boolean match;
		if (positive) {
			int num = (Integer) membrane.getNumOf(positiveObject);
			match = pattern.matcher(genStr(num, positiveObject.toString())).matches();
		} else {
			int num = (Integer) membrane.getNumOf(negativeObject);
			match = pattern.matcher(genStr(num, negativeObject.toString())).matches();
		}
		if (match)
			return Integer.MAX_VALUE;
		else
			return 0;
	}

	@Override
	public int fetch(Membrane membrane, int tryTimes) {
		return tryTimes;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {

	}

	@Override
	public Condition deepClone() {
		return this;
	}

	@Override
	public String toString() {
		return pattern.pattern();
	}
}
