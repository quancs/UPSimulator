package upsimulator.rules.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import upsimulator.interfaces.BasicName;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

public class ObjectConditionsWithTarget extends BasicName implements Condition {
	protected TunnelType move;
	protected ArrayList<ObjectCondition> ocList = new ArrayList<>();
	protected ArrayList<Dimension> dimensions;

	public ObjectConditionsWithTarget(TunnelType target) {
		super();
		this.move = target;
	}

	public ObjectConditionsWithTarget() {
		super();
	}

	public ObjectConditionsWithTarget(ObjectConditionsWithTarget oct) {
		super(oct);
		move = oct.move;
		dimensions = oct.dimensions;
		for (ObjectCondition oc : oct.ocList)
			ocList.add(oc.deepClone());
		// temporary data will be cloned.
		if (oct.satMemMap != null)
			satMemMap = new HashMap<>();
		if (oct.fetMemMap != null)
			fetMemMap = new HashMap<>(oct.fetMemMap);
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

	public TunnelType getMove() {
		return move;
	}

	public void setMove(TunnelType target) {
		this.move = target;
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

	protected HashMap<Membrane, Integer> satMemMap;

	@Override
	public int satisfy(Membrane membrane) {// 此时已经是fix的了
		if (satMemMap == null)
			satMemMap = new HashMap<>();
		satMemMap.clear();

		List<Tunnel> tunnels;

		if (move == TunnelType.In) {
			tunnels = membrane.getTunnels(TunnelType.In, this, true);
		} else if (move == TunnelType.Go) {
			tunnels = membrane.getTunnels(TunnelType.Go, this, true);
		} else {
			tunnels = new ArrayList<>();
			tunnels.add(membrane.getTunnel(TunnelType.Out, null));
		}

		int total = 0;
		for (Tunnel tunnel : tunnels) {
			Membrane targetMembrane = tunnel.getTargets().get(0);
			int satisfy = Integer.MAX_VALUE;
			for (ObjectCondition oc : ocList) {
				int ocs = oc.satisfy(targetMembrane);
				if (ocs < satisfy)
					satisfy = ocs;
			}
			satMemMap.put(targetMembrane, satisfy);
			total += satisfy;
		}

		return total;
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
		List<Tunnel> tunnels;

		if (move == TunnelType.In) {
			tunnels = membrane.getTunnels(TunnelType.In, this, false);
		} else if (move == TunnelType.Go) {
			tunnels = membrane.getTunnels(TunnelType.Go, this, false);
		} else {
			tunnels = new ArrayList<>();
			tunnels.add(membrane.getTunnel(TunnelType.Out, null));
		}

		if (tunnels.size() == 0)
			return null;

		ArrayList<Long[]> pValues = new ArrayList<>();

		// 1.predict values by using membrane names
		switch (move) {
		case Go:
			List<Long[]> pValues2 = predictPossibleValueOfNeighbor(membrane, dList);
			if (pValues2 != null)
				pValues.addAll(pValues2);
			break;
		case In:
			List<Long[]> pValues3 = predictPossibleValueOfSubmembrane(membrane, dList);
			if (pValues3 != null)
				pValues.addAll(pValues3);
			break;
		default:
			break;
		}

		// 2.predict values by using objects
		for (Tunnel tunnel : tunnels) {
			Membrane targetMembrane = tunnel.getTargets().get(0);
			for (ObjectCondition oc : ocList) {
				List<Long[]> pValues4 = oc.predictPossibleValue(targetMembrane, dList);
				if (pValues4 != null)
					pValues.addAll(pValues4);
			}
		}

		return pValues;
	}

	protected HashMap<Membrane, Integer> fetMemMap;

	@Override
	public int fetch(Membrane membrane, int times) {
		if (satMemMap == null)
			throw new RuntimeException("ObjectConditionsWithTarget cannot fetch because no satisfied membranes");
		if (satMemMap.size() == 0)
			return 0;

		if (fetMemMap == null)
			fetMemMap = new HashMap<>();
		fetMemMap.clear();

		ArrayList<Entry<Membrane, Integer>> entries = new ArrayList<>(satMemMap.entrySet());
		Collections.shuffle(entries);

		int total = 0;
		for (; entries.size() != 0;) {
			Entry<Membrane, Integer> first = entries.remove(0);
			Membrane target = first.getKey();
			int tryTimes;
			if (first.getValue() <= times)
				tryTimes = (int) (1 + first.getValue() * Math.random());
			else
				tryTimes = (int) (1 + times * Math.random());

			int fetchTimes = doFetch(target, tryTimes);
			if (fetchTimes == 0)
				continue;

			if (!fetMemMap.containsKey(target))
				fetMemMap.put(target, fetchTimes);
			else
				fetMemMap.put(target, fetMemMap.get(target) + fetchTimes);
			total += fetchTimes;
			if (tryTimes == fetchTimes && fetchTimes < first.getValue() && fetchTimes < times)
				entries.add(first);
		}

		return total;
	}

	public int doFetch(Membrane membrane, int times) {
		int fetched = 0;
		int i = ocList.size();
		boolean done = false;
		for (; done == false;) {
			for (i = i - 1; i >= 0; i--) {
				Condition criteria = ocList.get(i);
				fetched = criteria.fetch(membrane, times);
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
					criteria.withdrawFetch(membrane, times - fetched);
				}
			} else
				done = true;
			times = fetched;
		}
		return fetched;
	}

	@Override
	public void withdrawFetch(Membrane membrane, int times) {
		for (Entry<Membrane, Integer> entry : fetMemMap.entrySet()) {
			Membrane target = entry.getKey();
			int fetTimes = entry.getValue();

			for (ObjectCondition oc : ocList)
				oc.withdrawFetch(target, times);
		}
		fetMemMap.clear();
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

	public boolean isTheSameTarget(ObjectConditionsWithTarget oct) {
		if (move == oct.move && getNameDim().equals(oct.getNameDim()))
			return true;
		else
			return false;
	}

	public void combine(ObjectConditionsWithTarget oct) {
		for (ObjectCondition oc : oct.ocList)
			addObjectCondition(oc);
		oct.ocList.clear();
	}
}
