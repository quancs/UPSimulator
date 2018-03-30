package upsimulator.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 * 维度对象的接口
 * 
 * @author quan
 *
 */
public interface Dimension {
	
	/**
	 * 设置计算器，每条规则使用同一个计算器
	 * 
	 * @param evaluator
	 *            计算器
	 */
	public void setEval(Evaluator evaluator);

	/**
	 * 获取计算器
	 * 
	 * @return 计算器
	 */
	public Evaluator getEval();

	/**
	 * 为对象新增维度，例如A1，此时可以加入1作为A的一个维度
	 * 
	 * @param dimensions
	 *            新增维度【纯数字】
	 */
	public default void addDimension(Integer... dimensions) {
		for (Integer dim : dimensions)
			getDimensions().add(String.valueOf(dim));
	}

	/**
	 * 为对象新增维度，例如Ai+1，此时可以加入i+1作为A的一个维度
	 * 
	 * @param formulas
	 *            新增维度【的计算方式】
	 */
	public default void addDimension(String... formulas) {
		for (String dim : formulas)
			getDimensions().add(dim);
	}

	/**
	 * 
	 * @return 返回维度数
	 */
	public default int dimensionCount() {
		return getDimensions().size();
	}

	/**
	 * 获取全部的维度
	 * 
	 * @return 获取到的维度
	 */
	public List<String> getDimensions();

	public default List<Integer> getIntDimensions() {
		List<String> dims = getDimensions();
		ArrayList<Integer> intDims = new ArrayList<>();
		for (String dim : dims)
			intDims.add(Integer.parseInt(dim));
		return intDims;
	}

	// 如果存在一个没有使用的维度，会出现死循环？
	/**
	 * 将维度固定下来,r[i] 在i=1时，固定后为r1 。如果维度的内部包含有含有维度的对象，则此方法不适用
	 */
	public default void fixDimension() {
		List<String> fixedDim = new ArrayList<>();
		for (String dim : getDimensions()) {
			fixedDim.add(fix(dim));
		}
		getDimensions().clear();
		getDimensions().addAll(fixedDim);
	}

	public default String fix(String str) {
		try {
			return getEval().evaluate(str);
		} catch (EvaluationException e) {
			e.printStackTrace();
			return str;
		}
	}
}
