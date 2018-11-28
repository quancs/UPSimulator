package upsimulator.rules.results;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import upsimulator.exceptions.TimesException;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.rules.conditions.ObjectCondition;
import upsimulator.rules.conditions.ObjectConditionsWithTarget;

public class AntiportResult extends ObjectConditionsWithTarget implements Result {

	private ArrayList<ObjectResult> ors = new ArrayList<>();

	public AntiportResult() {

	}

	public AntiportResult(AntiportResult antiportResult) {
		super(antiportResult);

		dimensions = antiportResult.dimensions;
		for (ObjectResult or : antiportResult.ors)
			ors.add(or.deepClone());
	}

	public AntiportResult(ObjectConditionsWithTarget oct) {
		super(oct);
		dimensions = new ArrayList<>(oct.getDimensions());
	}

	public void addObjectResult(ObjectResult or) {
		ors.add(or);
		if (dimensions == null)
			dimensions = new ArrayList<>();
		if (!or.isFixed())
			dimensions.addAll(or.getDimensions());
	}

	@Override
	public void fix(Map<String, Object> mappedValues) {
		super.fix(mappedValues);
		for (ObjectResult or : ors)
			or.fix(mappedValues);
	}

	@Override
	public boolean isFixed() {
		if (super.isFixed() == false)
			return false;
		for (ObjectResult or : ors)
			if (or.isFixed() == false)
				return false;
		return true;
	}

	@Override
	public AntiportResult deepClone() {
		return new AntiportResult(this);
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException, TimesException {
		for (Entry<Membrane, Integer> entry : fetMemMap.entrySet()) {
			Membrane target = entry.getKey();
			Integer setTimes = entry.getValue();
			for (ObjectResult or : ors) {
				or.setResult(target, setTimes);
			}
		}
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		return current.getTunnel(TunnelType.Here, null);
	}

	public ArrayList<ObjectResult> getObjectResults() {
		return ors;
	}

	@Override
	public String toString() {
		return toConditionString() + " -> " + toResultString();
	}

	public String toConditionString() {
		StringBuilder sBuilder = new StringBuilder();
		switch (move) {
		case Out:
			sBuilder.append("out{ ");
			break;
		case Go:
			sBuilder.append("go." + getNameDim() + "{ ");
			break;
		case In:
			sBuilder.append("in." + getNameDim() + "{ ");
			break;
		default:
		}
		for (ObjectCondition oc : ocList)
			sBuilder.append(oc.toString() + " ");
		sBuilder.append("}");
		return sBuilder.toString();
	}

	public String toResultString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("{ ");
		for (ObjectResult or : ors)
			sBuilder.append(or.toString() + " ");
		sBuilder.setCharAt(sBuilder.length() - 1, ',');

		switch (move) {
		case Out:
			sBuilder.append("out");
			break;
		case Go:
			sBuilder.append("go." + getNameDim());
			break;
		case In:
			sBuilder.append("in." + getNameDim());
			break;
		default:
		}

		sBuilder.append(" }");
		return sBuilder.toString();
	}

}
