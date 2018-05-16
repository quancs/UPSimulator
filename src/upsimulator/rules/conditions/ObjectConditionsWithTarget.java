package upsimulator.rules.conditions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import upsimulator.interfaces.BasicName;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

public class ObjectConditionsWithTarget extends BasicName implements Condition {
	public enum Target {
		Parent, Child, Neighbor
	}

	private Target target;
	private ArrayList<ObjectCondition> ocList = new ArrayList<>();
	private ArrayList<Dimension> dimensions;

	public ObjectConditionsWithTarget(Target target) {
		super();
		this.target = target;
	}

	public ObjectConditionsWithTarget() {
		super();
	}

	public ObjectConditionsWithTarget(ObjectConditionsWithTarget oct) {
		super(oct);
		target = oct.target;
		dimensions = oct.dimensions;
		for (ObjectCondition oc : oct.ocList)
			ocList.add(oc.deepClone());
	}

	@Override
	public void addDimension(Dimension dimension) {
		super.addDimension(dimension);
		if (dimensions == null)
			dimensions = new ArrayList<>();
		dimensions.add(super.get(super.getDimensionSize() - 1));
	}

	@Override
	public void addDimension(Long dimension) {
		super.addDimension(dimension);
		if (dimensions == null)
			dimensions = new ArrayList<>();
		dimensions.add(super.get(super.getDimensionSize() - 1));
	}

	@Override
	public void addDimension(String dimension) {
		super.addDimension(dimension);
		if (dimensions == null)
			dimensions = new ArrayList<>();
		dimensions.add(super.get(super.getDimensionSize() - 1));
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public void addObjectCondition(ObjectCondition oc) {
		ocList.add(oc);
		if (dimensions == null)
			dimensions = new ArrayList<>();
		if (!oc.isFixed())
			dimensions.addAll(oc.getDimensions());
	}

	public List<ObjectCondition> getObjectConditions() {
		return ocList;
	}

	private Membrane targetMembrane;
	private boolean targetSelectedInPredict = false;

	@Override
	public int satisfy(Membrane membrane) {
		if (!targetSelectedInPredict) {
			Tunnel tunnel = null;
			if (target == Target.Child) {
				tunnel = membrane.getTunnel(TunnelType.In, getNameDim());
			} else if (target == Target.Neighbor) {
				tunnel = membrane.getTunnel(TunnelType.Go, getNameDim());
			} else {
				tunnel = membrane.getTunnel(TunnelType.Out, getNameDim());
			}

			if (tunnel == null)// target membrane doesn't exist
				return 0;
			targetMembrane = tunnel.getTargets().get(0);
		}
		targetSelectedInPredict = false;

		int satisfy = Integer.MAX_VALUE;
		for (ObjectCondition oc : ocList) {
			int ocs = oc.satisfy(targetMembrane);
			if (ocs < satisfy)
				satisfy = ocs;
		}
		return satisfy;
	}

	@Override
	public ArrayList<Dimension> getDimensions() {

		return dimensions;
	}

	@Override
	public Dimension get(int i) {
		return dimensions.get(i);
	}

	@Override
	public int getDimensionSize() {
		return dimensions.size();
	}

	@Override
	public List<Long[]> predictPossibleValue(Membrane membrane, List<Dimension> dList) {
		Tunnel tunnel = null;
		if (target == Target.Child) {
			tunnel = membrane.getTunnel(TunnelType.In, getNameDim());
		} else if (target == Target.Neighbor) {
			tunnel = membrane.getTunnel(TunnelType.Go, getNameDim());
		} else {
			tunnel = membrane.getTunnel(TunnelType.Out, getNameDim());
		}

		if (tunnel == null)// target membrane doesn't exist
			return null;
		targetMembrane = tunnel.getTargets().get(0);
		targetSelectedInPredict = true;

		List<Long[]> pValues = new LinkedList<>();

		switch (target) {
		case Neighbor:
			List<Long[]> pValues2 = predictPossibleValueOfNeighbor(membrane, dList);
			if (pValues2 != null)
				pValues.addAll(pValues2);
			break;
		case Child:
			List<Long[]> pValues3 = predictPossibleValueOfSubmembrane(membrane, dList);
			if (pValues3 != null)
				pValues.addAll(pValues3);
			break;
		default:
			break;
		}

		for (ObjectCondition oc : ocList) {
			List<Long[]> pValues4 = oc.predictPossibleValue(targetMembrane, dList);
			if (pValues4 != null)
				pValues.addAll(pValues4);
		}

		return pValues;
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		int fetched = 0;
		int i = ocList.size();
		boolean done = false;
		for (; done == false;) {
			for (i = i - 1; i >= 0; i--) {
				Condition criteria = ocList.get(i);
				fetched = criteria.fetch(targetMembrane, times);
				if (fetched == 0) {
					done = true;
					break;
				}
				if (fetched < times)
					break;
			}
			if (i >= 0) {// 撤销之前的fetch并继续下一个循环的fetch
				for (int j = i + 1; j < ocList.size(); j++) {
					Condition criteria = ocList.get(j);
					criteria.withdrawFetch(targetMembrane, times - fetched);
				}
			} else
				done = true;
			times = fetched;
		}
		return fetched;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
		for (ObjectCondition oc : ocList)
			oc.withdrawFetch(targetMembrane, times);
	}

	@Override
	public void fix(Map<String, Object> mappedValues) {
		super.fix(mappedValues);
		for (ObjectCondition oc : ocList)
			oc.fix(mappedValues);
	}

	@Override
	public ObjectConditionsWithTarget deepClone() {
		return new ObjectConditionsWithTarget(this);
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		switch (target) {
		case Parent:
			sBuilder.append("out{ ");
			break;
		case Neighbor:
			sBuilder.append("go." + getNameDim() + "{ ");
			break;
		case Child:
			sBuilder.append("in." + getNameDim() + "{ ");
			break;
		default:
		}
		for (ObjectCondition oc : ocList)
			sBuilder.append(oc.toString() + " ");
		sBuilder.append("}");
		return sBuilder.toString();
	}
}
