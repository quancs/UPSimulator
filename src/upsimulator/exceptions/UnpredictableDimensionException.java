package upsimulator.exceptions;

import upsimulator.interfaces.Rule;

/**
 * Unpredictable dimension: formula dimensions of one rule appear in the
 * {@code Inhibitor} only, then this dimension cannot be predicted
 * 
 * @author quan
 *
 */
public class UnpredictableDimensionException extends Exception {
	private static final long serialVersionUID = 1L;

	private Rule rule;
	private String dim;

	public UnpredictableDimensionException(Rule rule, String dim) {
		super("Couldn't predict the dimension value of " + dim + " in " + rule.toString()
				+ " Possible wrong form: Rule r1[i][j] = e[i] -> e[j]; Rule r1[i][j] = e[i] -> ; Rule r1[i][j] = e[i] -> | !e[j]; ");
		this.rule = rule;
		this.dim = dim;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public String getDim() {
		return dim;
	}

	public void setDim(String dim) {
		this.dim = dim;
	}

}
