/** 
 * Project Name:UPSimulator 
 * File Name:MembraneCreateResult.java 
 * Package Name:rules.results 
 * Date:2017年10月26日下午3:20:17 
 * Copyright (c) 2017, quancs@qq.com All Rights Reserved. 
 * Todo:TODO
 */
package upsimulator.rules.results;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jeval.Evaluator;
import upsimulator.core.PTunnel;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownTargetMembraneException;
import upsimulator.interfaces.Dimension;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * @author quan
 * @Date 2017年10月26日下午3:20:17
 * @Copyright (c) 2017, quancs@qq.com All Rights Reserved.
 * @Todo 创建膜的结果
 */
public class MembraneCreateResult implements Result, Dimension {

	private String templateMemName;
	private String name;

	private ArrayList<Result> extraResults = new ArrayList<>();

	public MembraneCreateResult() {
	}

	public MembraneCreateResult(String template, String name) {
		this.templateMemName = template;
		this.name = name;
	}

	@Override
	public void setResult(Membrane membrane) throws UnknownTargetMembraneException {
		Membrane sonMembrane = Membrane.getMemInstanceOf(templateMemName);
		if (sonMembrane == null)
			throw new UnknownTargetMembraneException("未找到对应的类或者深度复制失败。");

		sonMembrane.setName(getNameDim());
		PTunnel.addChildParentTunnel(membrane, sonMembrane);
		for (Result result : extraResults)
			result.setResult(sonMembrane);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MembraneCreateResult deepClone() throws CloneNotSupportedException {
		MembraneCreateResult cloned = (MembraneCreateResult) super.clone();
		cloned.evaluator = null;
		cloned.extraResults = new ArrayList<>();
		for (Result result : extraResults)
			cloned.extraResults.add(result.deepClone());
		cloned.dimensions = (ArrayList<String>) dimensions.clone();
		return cloned;
	}

	@Override
	public int getPriority() {
		return 2;
	}

	private String getNameDim() {
		String name = this.name + "";
		if (evaluator != null && dimensions != null) {
			for (int i = 0; i < dimensions.size(); i++) {
				String formula = dimensions.get(i);
				int dimension;
				try {
					dimension = (int) Double.parseDouble(evaluator.evaluate(formula));
					if (i == 0)
						name += dimension;
					else
						name += "," + dimension;
				} catch (Exception e) {
					name = this.name + "";
					for (int j = 0; j < dimensions.size(); j++) {
						if (j == 0)
							name += dimensions.get(j);
						else
							name += "," + dimensions.get(j);
					}
				}
			}
		} else if (dimensions != null) {
			for (int i = 0; i < dimensions.size(); i++) {
				if (i == 0)
					name += dimensions.get(i);
				else
					name += "," + dimensions.get(i);
			}
		}
		return name;
	}

	private Evaluator evaluator;

	@Override
	public void setEval(Evaluator evaluator) {
		this.evaluator = evaluator;
		for (Result result : extraResults)// 如果还存在其他的附带的结果，则一并设置
			if (result instanceof Dimension)
				((Dimension) result).setEval(evaluator);
	}

	private ArrayList<String> dimensions = new ArrayList<>();

	@Override
	public void addDimension(Integer... dimensions) {
		for (Integer dim : dimensions)
			this.dimensions.add(String.valueOf(dim));
	}

	@Override
	public void addDimension(String... formulas) {
		for (String dim : formulas)
			this.dimensions.add(dim);
	}

	@Override
	public int dimensionCount() {
		return dimensions.size();
	}

	@Override
	public List<String> getDimensions() {
		return dimensions;
	}

	@Override
	public String toString() {
		String str = "{ ";
		for (Result result : extraResults)
			str += result + " ";
		str += "}" + templateMemName;
		if (name != null)
			str += ":" + getNameDim();
		return str;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addResult(Result result) {
		this.extraResults.add(result);
	}

	public String getTemplateMemName() {
		return templateMemName;
	}

	public void setTemplateMemName(String templateMemName) {
		this.templateMemName = templateMemName;
	}

	@Override
	public void fixDimension() {
		// 固定自身的维度
		Dimension.super.fixDimension();

		// 固定子结果的维度
		for (Result result : extraResults)
			if (result instanceof Dimension)
				((Dimension) result).fixDimension();
	}

	@Override
	public Evaluator getEval() {
		return evaluator;
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		Tunnel tunnel = current.getTunnel(TunnelType.Here, null);
		if (tunnel == null) {
			throw new TunnelNotExistException(TunnelType.Here + " tunnel does not exist in " + current);
		} else {
			return tunnel;
		}
	}
}
