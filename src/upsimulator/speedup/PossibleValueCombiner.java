package upsimulator.speedup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This worker is used by rules to speed getting dimension possible values up
 * 
 * @author quan
 *
 */
public class PossibleValueCombiner extends Worker {

	private LinkedList<Long[]> pValues, combine1, combine2;
	int[] combine1Dims, combine2Dims;

	private PossibleValueCombiner(LinkedList<Long[]> combine1, LinkedList<Long[]> combine2, int[] combine1Dims, int[] combine2Dims) {
		super();
		this.combine1 = combine1;
		this.combine2 = combine2;
		this.combine1Dims = combine1Dims;
		this.combine2Dims = combine2Dims;
	}

	public static PossibleValueCombiner getWorker(LinkedList<Long[]> combine1, LinkedList<Long[]> combine2, int[] combine1Dims, int[] combine2Dims) {
		return new PossibleValueCombiner(combine1, combine2, combine1Dims, combine2Dims);
	}

	@Override
	public void doWork() {
		pValues = combineValue(combine1, combine2, getIntersection(combine1Dims, combine2Dims));
	}

	public LinkedList<Long[]> getPValues() {
		return pValues;
	}

	public int[] getDims() {
		return getUnion(combine1Dims, combine2Dims);
	}

	private LinkedList<Long[]> combineValue(List<Long[]> vs1, List<Long[]> vs2, int[] dimCompare) {
		LinkedList<Long[]> newPValues = new LinkedList<>();
		for (Long[] pv1 : vs1) {
			for (Long[] pv2 : vs2) {
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
					Long[] pvNew = new Long[pv1.length];
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

	private int[] getIntersection(int[] a1, int[] a2) {
		LinkedList<Integer> list = new LinkedList<>();
		for (int a : a1) {
			for (int b : a2) {
				if (a == b) {
					list.add(a);
					break;
				}
			}
		}
		int r[] = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			r[i] = list.get(i);
		}
		Arrays.sort(r);
		return r;
	}

	private int[] getUnion(int[] a1, int[] a2) {
		LinkedList<Integer> list = new LinkedList<>();
		int a1i, a2i;
		for (a1i = 0, a2i = 0; a1i < a1.length && a2i < a2.length;) {
			if (a1[a1i] < a2[a2i]) {
				list.add(a1[a1i]);
				a1i++;
			} else if (a1[a1i] == a2[a2i]) {
				list.add(a1[a1i]);
				a1i++;
				a2i++;
			} else {
				list.add(a2[a2i]);
				a2i++;
			}
		}

		for (; a1i < a1.length; a1i++)
			list.add(a1[a1i]);
		for (; a2i < a2.length; a2i++)
			list.add(a2[a2i]);

		int r[] = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			r[i] = list.get(i);
		}
		return r;
	}

}
