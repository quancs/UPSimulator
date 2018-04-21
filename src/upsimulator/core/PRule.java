package upsimulator.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sourceforge.jeval.Evaluator;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.UPSLogger;
import upsimulator.rules.conditions.InhibitorCondition;
import upsimulator.rules.conditions.MembranePropertyCondition;
import upsimulator.rules.conditions.ObjectCondition;
import upsimulator.rules.conditions.PriorityCondition;
import upsimulator.rules.results.MembranePropertyResult;
import upsimulator.rules.results.MembraneStatusResult;
import upsimulator.speedup.PossibleValueCombiner;
import upsimulator.speedup.RuleChecker;

/**
 * PRule represents the rule in P systems.<br>
 * PRule is the basic implementation of Rule.
 * 
 * @author quan
 *
 */
public class PRule implements Rule {
	private static final long serialVersionUID = -3360118464623511714L;

	private static Logger logger = Logger.getLogger(PRule.class);

	private List<Condition> conditions;
	private List<Result> results;

	private Evaluator evaluator;

	public PRule(String name, String... dims) {
		conditions = new ArrayList<>();
		dimensions = new LinkedList<>();
		for (String dim : dims)
			dimensions.add(dim);
		results = new ArrayList<>();
		this.name = name;
		evaluator = new Evaluator();
	}

	public PRule() {
		conditions = new ArrayList<>();
		dimensions = new LinkedList<>();
		results = new ArrayList<>();
		this.name = "NoType";
		evaluator = new Evaluator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PRule deepClone() {
		try {
			PRule cloned = (PRule) super.clone();

			cloned.evaluator = new Evaluator();

			cloned.conditions = new ArrayList<>();
			for (Condition condition : conditions) {
				Condition conditionCloned = (Condition) condition.deepClone();
				if (conditionCloned instanceof Dimension)
					((Dimension) conditionCloned).setEval(cloned.evaluator);

				cloned.conditions.add(conditionCloned);
			}

			cloned.results = new ArrayList<>();
			for (Result result : results) {
				Result resultCloned = (Result) result.deepClone();
				if (resultCloned instanceof Dimension)
					((Dimension) resultCloned).setEval(cloned.evaluator);
				cloned.results.add(resultCloned);
			}

			cloned.dimensions = (LinkedList<String>) dimensions.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int satisfy(Membrane membrane) {
		int min = Integer.MAX_VALUE;
		for (Condition condition : conditions) {
			int st = condition.satisfy(membrane);
			if (st == 0)
				return 0;
			if (st < min)
				min = st;
		}
		return min;
	}

	private LinkedList<Integer[]> findValues(DimensionInfo dInfo, Membrane membrane) {
		LinkedList<Integer[]> pValues = new LinkedList<>();
		Iterator<Obj> iter = membrane.getObjects().keySet().iterator();
		while (iter.hasNext()) {
			Obj obj = iter.next();
			if (obj.getName().equals(dInfo.getObj().getName()) && obj.dimensionCount() == dInfo.getObj().dimensionCount()) {
				Integer pValue[] = new Integer[dimensionCount()];

				for (int i = 0; i < dInfo.size(); i++) {
					try {
						pValue[dInfo.getRuleDim(i)] = obj.getIntDimensions().get(dInfo.getObjDim(i));
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(membrane.toString());
						throw e;
					}
				}
				pValues.add(pValue);
			}
		}

		return pValues;
	}

	private LinkedList<Integer[]> combineValue(List<Integer[]> vs1, List<Integer[]> vs2, int[] dimCompare) {
		LinkedList<Integer[]> newPValues = new LinkedList<>();
		for (Integer[] pv1 : vs1) {
			for (Integer[] pv2 : vs2) {
				boolean canCombine = true;
				if (dimCompare != null) {
					for (int dim : dimCompare) {
						if (!pv1[dim].equals(pv2[dim])) {
							canCombine = false;
							break;
						}
					}
				}
				if (canCombine) {
					Integer[] pvNew = new Integer[dimensionCount()];
					for (int k = 0; k < pv1.length; k++) {
						if (pv1[k] != null)
							pvNew[k] = pv1[k];
						else
							pvNew[k] = pv2[k];
					}
					newPValues.add(pvNew);
				}
			}
		}
		return newPValues;
	}

	/**
	 * Get the possible values of current rule
	 * 
	 * @param membrane
	 *            the membrane current rule in
	 * @return All the possible value
	 */
	private List<Integer[]> getPossibleValues(Membrane membrane) {
		LinkedList<LinkedList<Integer[]>> pValuesList = new LinkedList<>();
		LinkedList<int[]> pValuesDim = new LinkedList<>();
		for (DimensionInfo dInfo : dInfos) {
			LinkedList<Integer[]> pList = findValues(dInfo, membrane);
			if (pList.size() == 0)
				return new LinkedList<>();
			pValuesList.add(pList);
			int[] pvds = new int[dInfo.size()];
			for (int i = 0, size = dInfo.size(); i < size; i++) {
				pvds[i] = dInfo.getRuleDim(i);
			}
			Arrays.sort(pvds);
			pValuesDim.add(pvds);
		}

		LinkedList<PossibleValueCombiner> workers = new LinkedList<>();
		for (int total = pValuesList.size() - 1, current = 1; workers.size() != 0 || pValuesList.size() > 1;) {
			for (; pValuesList.size() > 1;) {
				LinkedList<Integer[]> last = pValuesList.removeFirst(), lastComp = null;
				int[] lastDims = pValuesDim.removeFirst(), lastDimsComp = null;

				for (int i = 0; i < pValuesDim.size(); i++) {
					int[] dims = pValuesDim.get(i);
					boolean hasSameDim = false;
					for (int d : dims) {
						for (int dlast : lastDims) {
							if (d == dlast) {
								hasSameDim = true;
								break;
							}
						}
						if (hasSameDim)
							break;
					}
					if (hasSameDim) {
						lastComp = pValuesList.remove(i);
						lastDimsComp = pValuesDim.remove(i);
						break;
					}
				}

				if (lastComp == null) {
					lastComp = pValuesList.removeFirst();
					lastDimsComp = pValuesDim.removeFirst();
				}

				PossibleValueCombiner worker = PossibleValueCombiner.getWorker(last, lastComp, lastDims, lastDimsComp);
				worker.start();
				workers.add(worker);
				UPSLogger.info(this, "Thread " + worker.getWorkerId() + " " + getNameDim() + " search possible values : " + current + "/" + total + " " + last.size() + "*" + lastComp.size());
				current++;
			}
			for (int i = 0; i < workers.size(); i++) {
				PossibleValueCombiner worker = workers.get(i);
				if (worker.finished()) {
					pValuesList.add(worker.getPValues());
					pValuesDim.add(worker.getDims());
					workers.remove(i);
					i--;
					UPSLogger.info(this, "Thread " + worker.getWorkerId() + " finished, time spend= " + worker.getTimeSpend() + " find " + worker.getPValues().size() + " new possible values.");
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return pValuesList.get(0);
	}

	private ArrayList<DimensionInfo> dInfos;
	private ArrayList<ArrayList<DimensionInfo>> graph;

	private void initDimInfos() throws UnpredictableDimensionException {
		dInfos = new ArrayList<>();
		graph = new ArrayList<>(dimensions.size());

		for (int i = 0; i < dimensionCount(); i++)
			graph.add(new ArrayList<>());

		for (Condition condition : conditions) {
			if ((condition instanceof Obj) && !(condition instanceof InhibitorCondition)) {
				DimensionInfo dInfo = new DimensionInfo((Obj) condition);
				for (int j = 0; j < ((Obj) condition).dimensionCount(); j++) {
					String cDim = ((Obj) condition).getDimensions().get(j);
					for (int i = 0; i < dimensions.size(); i++) {
						String dimension = dimensions.get(i);
						if (cDim.equals("#{" + dimension + "}")) {
							dInfo.addDimMap(j, i);
							if (!graph.get(i).contains(dInfo))
								graph.get(i).add(dInfo);
							break;
						}
					}
				}
				dInfos.add(dInfo);
			}
		}

		dInfos.sort(new Comparator<DimensionInfo>() {
			@Override
			public int compare(DimensionInfo o1, DimensionInfo o2) {
				if (o1.size() > o2.size())
					return 1;
				else if (o2.size() > o1.size()) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		for (int i = 0; i < dInfos.size(); i++) {
			boolean delete = true;
			DimensionInfo d = dInfos.get(i);
			for (ArrayList<DimensionInfo> p : graph) {
				if (p.contains(d) && p.size() == 1) {
					delete = false;
				}
			}
			if (delete) {
				dInfos.remove(i);
				i--;
				for (ArrayList<DimensionInfo> p : graph) {
					p.remove(d);
				}
			}
		}

		for (int i = 0; i < graph.size(); i++) {
			if (graph.get(i).size() == 0) {
				throw new UnpredictableDimensionException(this, dimensions.get(i));// Possible wrong form: Rule r1[i][j] = e[i] -> e[j]; Rule r1[i][j] = e[i] -> ;
																					// Rule r1[i][j] = e[i] -> | !e[j]; ");
			}
		}
	}

	@Override
	public int fetch(Membrane membrane, int times) {
		if (dimensions.size() > 0 && !fixed) {
			logger.error("Rule " + this + " has uncomputed dimension.");
			return 0;
		} else {
			return doFetch(membrane, times);
		}
	}

	/**
	 * Let conditions fetch, and PriorityCondition & MembraneStatusCondition will
	 * fetch first to make sure their semantic meaning right.
	 * 
	 * @param membrane
	 * @param times
	 *            try to fetch
	 * @return times really fetched
	 */
	private int doFetch(Membrane membrane, int times) {
		int fetched = 0;

		int i = conditions.size();
		boolean done = false;
		for (; done == false;) {
			for (i = i - 1; i >= 0; i--) {
				Condition criteria = conditions.get(i);
				fetched = criteria.fetch(membrane, times);
				if (fetched == 0) {
					done = true;
					break;
				}
				if (fetched < times)
					break;
			}
			if (i >= 0) {// 撤销之前的fetch并继续下一个循环的fetch
				for (int j = i + 1; j < conditions.size(); j++) {
					Condition criteria = conditions.get(j);
					criteria.withdrawFetch(membrane, times - fetched);
				}
			} else
				done = true;
			times = fetched;
		}
		return fetched;
	}

	@Override
	public void addCondition(Condition condition) {
		if (conditions.contains(condition))
			return;

		if (condition instanceof PriorityCondition) {
			conditions.add(0, condition);
		} else if (condition instanceof MembraneStatusResult) {
			conditions.add(condition);
		} else {
			if (conditions.size() > 0 && conditions.get(0) instanceof PriorityCondition)
				conditions.add(1, condition);
			else
				conditions.add(0, condition);
		}
		if (condition instanceof Dimension)
			((Dimension) condition).setEval(evaluator);

		if (condition instanceof Result)
			addResult((Result) condition);
	}

	@Override
	public void addResult(Result result) {
		if (results.contains(result))
			return;

		results.add(result);
		if (result instanceof Condition)
			addCondition((Condition) result);

		if (result instanceof Dimension)
			((Dimension) result).setEval(evaluator);
	}

	private LinkedList<String> dimensions;

	@Override
	public void addDimension(String... formulas) {
		for (int i = 0; i < formulas.length; i++) {
			dimensions.add(formulas[i]);
		}
	}

	@Override
	public void addDimension(Integer... dimensions) {
		throw new RuntimeException("Integer dimensions in rule is not allowed.  " + toString());
	}

	@Override
	public int dimensionCount() {
		return dimensions.size();
	}

	private String name;

	@Override
	public String getNameDim() {
		String name = this.name + "";
		for (int i = 0; i < dimensions.size(); i++) {
			if (i == 0)
				name += dimensions.get(i);
			else
				name += "," + dimensions.get(i);
		}
		return name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getDimensions() {
		return dimensions;
	}

	private class DimensionInfo {
		public class Pair<E extends Object, F extends Object> {
			private E first;
			private F second;

			public Pair() {

			}

			public Pair(E e, F f) {
				first = e;
				second = f;
			}

			public E getFirst() {
				return first;
			}

			public void setFirst(E first) {
				this.first = first;
			}

			public F getSecond() {
				return second;
			}

			public void setSecond(F second) {
				this.second = second;
			}

			@Override
			public String toString() {
				return "(" + first + "," + second + ")";
			}
		}

		public DimensionInfo(Obj obj) {
			super();
			this.obj = obj;
		}

		private ArrayList<Pair<Integer, Integer>> infos = new ArrayList<>();// <objDim,ruleDim>

		private Obj obj;

		public Obj getObj() {
			return obj;
		}

		public void setObj(Obj obj) {
			this.obj = obj;
		}

		public Pair<Integer, Integer> getDimMap(int i) {
			return infos.get(i);
		}

		public int size() {
			return infos.size();
		}

		public int getObjDim(int i) {
			return infos.get(i).getFirst();
		}

		public int getRuleDim(int i) {
			return infos.get(i).getSecond();
		}

		public void addDimMap(int objDim, int ruleDim) {
			infos.add(new Pair<Integer, Integer>(objDim, ruleDim));
		}

		@Override
		public String toString() {
			return obj.getName() + infos;
		}
	}

	@Override
	public void setEval(Evaluator evaluator) {
		throw new RuntimeException("PRule doesnot need evaluator. it creates one.");
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder("Rule ");
		sBuilder.append(getName());
		for (String d : dimensions) {
			sBuilder.append("[");
			sBuilder.append(d);
			sBuilder.append("]");
		}
		sBuilder.append(" = ");

		for (Condition condition : conditions) {
			if (condition instanceof MembranePropertyCondition) {
				sBuilder.append(condition + " ");
			}
		}
		for (Condition condition : conditions) {
			if (condition instanceof ObjectCondition) {
				sBuilder.append(condition + " ");
			}
		}
		sBuilder.append("-> ");
		for (Result result : results) {
			if (result instanceof MembranePropertyResult) {
				sBuilder.append(result + " ");
			}
		}
		for (Result result : results) {
			if (!(result instanceof MembranePropertyResult)) {
				sBuilder.append(result + " ");
			}
		}

		StringBuilder otherBuilder = new StringBuilder("| ");
		for (Condition condition : conditions) {
			if (condition instanceof MembranePropertyCondition || condition instanceof ObjectCondition || condition instanceof Result || condition instanceof PriorityCondition)
				continue;
			otherBuilder.append(condition + " ");
		}
		if (otherBuilder.length() > 2)
			sBuilder.append(otherBuilder);

		sBuilder.replace(sBuilder.length() - 1, sBuilder.length(), "");
		for (Condition condition : conditions) {
			if (condition instanceof PriorityCondition)
				sBuilder.append(condition.toString());
		}

		sBuilder.append(";");
		return sBuilder.toString();
	}

	@Override
	public List<Condition> getConditions() {
		return conditions;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	private boolean fixed = false;

	@Override
	public void fixDimension() {
		for (Condition condition : conditions)
			if (condition instanceof Dimension)
				((Dimension) condition).fixDimension();
		for (Result result : results)
			if (result instanceof Dimension)
				((Dimension) result).fixDimension();
		fixed = true;
	}

	/**
	 * Rule with dimensions check all the satisfied rule of it.
	 * 
	 * @throws UnpredictableDimensionException
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Map<Rule, Integer> satisfiedRules(Membrane membrane) throws UnpredictableDimensionException, CloneNotSupportedException {
		if (dInfos == null)
			initDimInfos();
		Calendar t1 = Calendar.getInstance();
		List<Integer[]> pValues = getPossibleValues(membrane);
		Calendar t2 = Calendar.getInstance();

		HashMap<Rule, Integer> satisfiedRules = new HashMap<>();

		LinkedList<RuleChecker> checkers = new LinkedList<>();
		int n = pValues.size() / 400;
		if (n <= 0)
			n = 1;
		n = pValues.size() / n;
		for (int start = 0; start < pValues.size();) {
			int end = start + n;
			if (end > pValues.size())
				end = pValues.size();
			RuleChecker checker = new RuleChecker(this, pValues.subList(start, end), membrane);
			checker.start();
			checkers.add(checker);
			start = end;
		}

		for (RuleChecker checker : checkers) {
			try {
				checker.join();
				satisfiedRules.putAll(checker.getSatisfiedRules());
				UPSLogger.info(this, "RuleChecker " + checker.getWorkerId() + " checked " + checker.getSatisfiedRules().size() + "/" + checker.getTotal() + "  " + checker.getTimeSpend() + "ms");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Calendar t3 = Calendar.getInstance();
		UPSLogger.info(this, getNameDim() + " getPossibleValues=" + (t2.getTimeInMillis() - t1.getTimeInMillis()) + "ms\t\t" + " satisfyCheck=" + (t3.getTimeInMillis() - t2.getTimeInMillis()) + "ms");
		return satisfiedRules;
	}

	@Override
	public Evaluator getEval() {
		return evaluator;
	}

}
