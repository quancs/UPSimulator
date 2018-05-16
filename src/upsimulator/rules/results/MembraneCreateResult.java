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
import java.util.Map;

import upsimulator.core.PTunnel;
import upsimulator.exceptions.TimesException;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnknownMembraneClassException;
import upsimulator.interfaces.BasicName;
import upsimulator.interfaces.Dimensional;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * Create a sub-membrane in one membrane
 * 
 * @author quan
 *
 */
public class MembraneCreateResult extends BasicName implements Result {

	private String templateMemName;

	private ArrayList<Result> extraResults = new ArrayList<>();

	public MembraneCreateResult() {
	}

	public MembraneCreateResult(String template, String name) {
		this.templateMemName = template;
		setName(name);
	}

	public MembraneCreateResult(MembraneCreateResult mcr) {
		super(mcr);
		this.templateMemName = mcr.templateMemName;
		setName(mcr.getName());
		extraResults = new ArrayList<>(mcr.extraResults.size());
		for (Result result : mcr.extraResults)
			extraResults.add(result.deepClone());
	}

	@Override
	public void setResult(Membrane membrane, int times) throws UnknownMembraneClassException, TimesException {
		if (times > 1)
			throw new TimesException(this, membrane, times, 1);

		Membrane sonMembrane = Membrane.getMemInstanceOf(templateMemName);
		if (sonMembrane == null)
			throw new UnknownMembraneClassException(templateMemName);

		sonMembrane.setName(getNameDim());
		PTunnel.addChildParentTunnel(membrane, sonMembrane);
		for (Result result : extraResults)
			result.setResult(sonMembrane, 1);
	}

	@Override
	public MembraneCreateResult deepClone() {
		MembraneCreateResult cloned = new MembraneCreateResult(this);
		return cloned;
	}

	@Override
	public String toString() {
		String str = templateMemName;
		if (getName() != null)
			str += ":" + getNameDim();
		if (extraResults.size() > 0) {
			str += "{ ";
			for (Result result : extraResults)
				str += result + " ";
			str += "}";
		}
		return str;
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
	public void fix(Map<String, Object> env) {
		super.fix(env);
		for (Result result : extraResults)
			if (result instanceof Dimensional)
				((Dimensional) result).fix(env);
	}

	@Override
	public Tunnel selectTunnel(Membrane current) throws TunnelNotExistException {
		Tunnel tunnel = current.getTunnel(TunnelType.Here, null);
		if (tunnel == null) {
			throw new TunnelNotExistException(current, TunnelType.Here);
		} else {
			return tunnel;
		}
	}
}
