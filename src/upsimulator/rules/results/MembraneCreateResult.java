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

	private boolean checkNeighbors = false;// 是否检查 邻居膜是否含有到新建膜同名字的膜的Go通道

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
		// if (checkNeighbors) {// membrane.children -> created
		List<Membrane> children = membrane.getChildren();
		Membrane sameNameMembrane = null;
		for (Membrane child : children) {
			if (child.getNameDim().equals(sonMembrane.getNameDim())) {
				sameNameMembrane = child;
				break;
			}
		}

		if (sameNameMembrane != null) {
			for (Membrane child : children) {
				if (child == sameNameMembrane)
					continue;

				List<Tunnel> nTunnels = child.getTunnels();
				for (int i = nTunnels.size() - 1; i >= 0; i--) {
					Tunnel tunnel = nTunnels.get(i);
					if (tunnel.getTargets().contains(sameNameMembrane) && tunnel.getType() != TunnelType.Here) {
						Tunnel clonedTunnel = null;
						try {
							clonedTunnel = tunnel.getClass().newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
						clonedTunnel.setType(tunnel.getType());
						clonedTunnel.setSource(tunnel.getSource());
						for (Membrane target : tunnel.getTargets()) {
							if (target != sameNameMembrane)
								clonedTunnel.addTarget(target);
							else
								clonedTunnel.addTarget(sonMembrane);
						}

						child.addTunnel(clonedTunnel);
					}
				}
			}
		}
		// }

		if (sameNameMembrane != null) {
			List<Tunnel> pTunnels = membrane.getTunnels();
			for (int i = pTunnels.size() - 1; i >= 0; i--) {
				Tunnel tunnel = pTunnels.get(i);
				if (tunnel.getType().toString().contains("In_") && tunnel.getTargets().contains(sameNameMembrane)) {
					Tunnel clonedTunnel = null;
					try {
						clonedTunnel = tunnel.getClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					clonedTunnel.setType(tunnel.getType());
					clonedTunnel.setSource(tunnel.getSource());
					for (Membrane target : tunnel.getTargets()) {
						if (target != sameNameMembrane)
							clonedTunnel.addTarget(target);
						else
							clonedTunnel.addTarget(sonMembrane);
					}
					membrane.addTunnel(clonedTunnel);
				}
			}
		}
		membrane.addChild(sonMembrane, PTunnel.class);

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
