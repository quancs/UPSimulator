package upsimulator.rules.conditions;

import java.util.Random;

import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;

public class ProbabilisticCondition implements Condition {

	private double probability;

	public ProbabilisticCondition(double probability) {
		super();
		setProbability(probability);
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		if (probability >= 1 || probability < 0)
			throw new IllegalArgumentException("Probability cannot greater than 1 and cannot less than 0: " + probability);
		this.probability = probability;
	}

	@Override
	public int satisfy(Membrane membrane) {
		return Integer.MAX_VALUE;
	}

	private Random random = new Random(System.nanoTime());

	@Override
	public int fetch(Membrane membrane, int tryTimes) {
		if (tryTimes < 10) {
			return checkRandom(tryTimes);
		} else {
			int percent = random.nextInt(10);
			int tenPercent = tryTimes / 10;
			int times = tenPercent * checkRandom(percent);
			times += fetch(membrane, tryTimes - tenPercent * percent);
			return times;
		}
	}

	private int checkRandom(int tryTimes) {
		Random random = new Random(System.nanoTime());
		int times = 0;
		for (int i = 0; i < tryTimes; i++) {
			if (random.nextDouble() <= this.probability)
				times++;
		}
		return times;
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
		return "probability=" + probability;
	}

}
