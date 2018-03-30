package upsimulator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.jeval.Evaluator;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.gui.MainWindow;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;

public class PMembrane implements Membrane {
	private static final long serialVersionUID = -1932793470654198760L;

	private String name;
	private HashMap<Obj, Integer> objects = new HashMap<Obj, Integer>();
	private ArrayList<Rule> rules = new ArrayList<>();

	public PMembrane(String name) {
		super();
		this.name = name;
		PTunnel tunnel = new PTunnel(TunnelType.Here);
		tunnel.setSource(this);
		tunnel.addTarget(this);
		tunnels.add(tunnel);
		urules = new ArrayList<>(1000);
		fetchedRules = new ArrayList<Rule>(1000);
	}

	public PMembrane() {
		super();
		this.name = "NoType";
		PTunnel tunnel = new PTunnel(TunnelType.Here);
		tunnel.setSource(this);
		tunnel.addTarget(this);
		tunnels.add(tunnel);
		urules = new ArrayList<>(1000);
		fetchedRules = new ArrayList<Rule>(1000);
	}

	@Override
	public PMembrane deepClone() throws CloneNotSupportedException, InstantiationException, IllegalAccessException {
		// no need to clone name , they won't change.
		// rules, objects and properties need to be cloned

		ArrayList<Membrane> membranes = new ArrayList<>();//
		ArrayList<Tunnel> tunnels = new ArrayList<>();
		HashMap<Membrane, Membrane> m2mMap = new HashMap<>();// membrane to its clone
		// 1.Scan all the membranes and tunnels
		membranes.add(this);
		for (int i = 0; i < membranes.size(); i++) {
			Membrane m = membranes.get(i);
			List<Tunnel> tList = m.getTunnels();
			for (Tunnel t : tList) {
				if (t.getType() != TunnelType.In || t.getSource() != m)
					continue;
				if (tunnels.contains(t))
					continue;

				tunnels.add(t);
				List<Membrane> targets = t.getTargets();
				for (Membrane target : targets) {
					if (!membranes.contains(target))
						membranes.add(target);
				}
			}

			Membrane cloned = m.getClass().newInstance();
			m2mMap.put(m, cloned);
			// clone name & dimension
			cloned.setName(m.getNameDim());
			// clone rules
			for (Rule rule : m.getRules())
				cloned.addRule(rule.deepClone());
			// clone objects
			Iterator<Entry<Obj, Integer>> iter = m.getObjects().entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Obj, Integer> entry = iter.next();
				Obj key = entry.getKey();
				Integer val = entry.getValue();
				cloned.addObject(key.deepClone(), val.intValue());
			}
			// clone properties
			Iterator<Entry<String, Object>> iter2 = properties.entrySet().iterator();
			while (iter2.hasNext()) {
				Entry<String, Object> entry = iter2.next();
				String key = entry.getKey();
				Object val = entry.getValue();
				cloned.setProperty(key, val);
			}
		}

		// clone tunnels
		for (Tunnel t : tunnels) {
			Tunnel cloned = t.getClass().newInstance();
			cloned.setSource(m2mMap.get(t.getSource()));
			for (Membrane target : t.getTargets())
				cloned.addTarget(m2mMap.get(target));
			m2mMap.get(t.getSource()).addTunnel(cloned);
		}

		return (PMembrane) m2mMap.get(this);
	}

	@Override
	public int getNumOf(Obj object) {
		if (objects.containsKey(object)) {
			return objects.get(object);
		}
		return 0;
	}

	@Override
	public void addObject(Obj object, int num) {
		if (object instanceof Condition)
			System.err.println("add a condition as object");

		if (!deleted) {
			if (objects.containsKey(object)) {
				objects.put(object, objects.get(object) + num);
			} else {
				objects.put(object, num);
			}
		}
		// else {
		// fatherMembrane.addObject(object, num);
		// }
	}

	@Override
	public boolean reduceObject(Obj object, int num) {
		if (objects.containsKey(object) && objects.get(object) >= num) {
			objects.put(object, objects.get(object) - num);
			if (objects.get(object) == 0) {
				objects.remove(object);
			}
			return true;
		}
		return false;
	}

	@Override
	public void addRule(Rule rule) {
		boolean added = false;
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i).getPriority() >= rule.getPriority()) {
				rules.add(i, rule);
				added = true;
				break;
			}
		}
		if (!added)
			rules.add(rule);
	}

	@Override
	public HashMap<Obj, Integer> getObjects() {
		return objects;
	}

	// @Override
	// public void addSon(Membrane sonMembrane) {
	// if (!deleted) {
	// sonMembranes.add(sonMembrane);
	// sonMembrane.setFather(this);
	// } else {
	// fatherMembrane.addSon(sonMembrane);
	// }
	// }

	// @Override
	// public void setFather(Membrane fatherMembrane) {
	// this.fatherMembrane = fatherMembrane;
	// }
	//
	// @Override
	// public List<Membrane> getSons() {
	// return sonMembranes;
	// }
	//
	// @Override
	// public Membrane getFather() {
	// return fatherMembrane;
	// }

	private ArrayList<Rule> urules;

	@Override
	public List<Rule> getUseableRules() throws UnpredictableDimensionException, CloneNotSupportedException {
		urules.clear();

		for (Rule rule : rules) {
			if (rule.dimensionCount() > 0) {
				urules.addAll(rule.satisfiedRules(this));
			} else if (rule.satisfy(this)) {
				urules.add(rule);
			}
		}
		return urules;
	}

	/*
	 * @Override public List<Rule> getKnownUseableRules() { return urules; }
	 */

	private ArrayList<Rule> fetchedRules;

	@Override
	public int fetch() throws TunnelNotExistException {
		fetchedRules.clear();

		for (int i = 0; i < urules.size(); i++) {
			if (urules.size() > 100 && ((urules.size() - i) % 1000 == 0)) {
				MainWindow.appendLogMsg("Membrane " + getNameDim() + " fetched " + i + "/" + urules.size() + " rules");
			}

			Rule rule = urules.get(i);
			while (rule.fetch(this)) {// 一条规则发生多次的时候
				fetchedRules.add(rule);
				for (Result result : rule.getResults()) {
					try {
						result.selectTunnel(this).holdResult(result);
					} catch (TunnelNotExistException e) {
						e.printStackTrace();
						throw e;
					}
				}
			}
		}
		return fetchedRules.size();
	}

	@Override
	public List<Rule> setProducts() {
		for (Tunnel t : tunnels)
			t.pushResult();
		return fetchedRules;
	}

	private String nameDim = null;

	@Override
	public String getNameDim() {
		if (nameDim == null) {
			nameDim = new String(name);
			boolean first = true;
			for (Integer dim : dimensions) {
				if (first) {
					nameDim += dim;
					first = false;
				} else {
					nameDim += "," + dim;
				}
			}
		}
		return nameDim;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String objs = "";
		Iterator<Entry<Obj, Integer>> iter = objects.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Obj, Integer> entry = iter.next();
			Obj key = entry.getKey();
			Object val = entry.getValue();
			objs += key.getNameDim() + "^" + val + " ";
		}

		String membranes = "";
		for (Membrane son : getChildren()) {
			membranes += son.toString() + " ";
		}

		String properties = "";
		Iterator<Entry<String, Object>> piter = this.properties.entrySet().iterator();
		while (piter.hasNext()) {
			Entry<String, Object> entry = piter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (!key.toString().startsWith("$"))
				properties += "." + key + "=" + val;
		}

		return "[ " + objs + membranes + "]" + getNameDim() + properties;
	}

	@Override
	public String toStringWithRule() {
		String objs = "";
		Iterator<Entry<Obj, Integer>> iter = objects.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Obj, Integer> entry = iter.next();
			Obj key = entry.getKey();
			Object val = entry.getValue();
			objs += key.getNameDim() + "^" + val + " ";
		}

		// 子膜还是按照包含关系进行输出，邻接关系则输出为通道
		String membranes = "";
		for (Membrane son : getChildren()) {
			membranes += son.toStringWithRule() + "\n";
		}

		String properties = "";
		Iterator<Entry<String, Object>> piter = this.properties.entrySet().iterator();
		while (piter.hasNext()) {
			Entry<String, Object> entry = piter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (!key.toString().startsWith("$"))
				properties += "." + key + "=" + val;
		}

		return "[ \nObject " + objs + ";  \n" + rules.toString() + "  ; \nMembrane " + membranes + ";\n]" + getNameDim() + properties;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private HashMap<String, Object> properties = new HashMap<>();

	@Override
	public void setProperty(String propertyName, Object propertyValue) {
		if (properties == null)
			properties = new HashMap<>();
		properties.put(propertyName, propertyValue);
	}

	@Override
	public Object getProperty(String propertyName) {
		if (properties == null)
			return null;
		else
			return properties.get(propertyName);
	}

	@Override
	public List<Rule> getRules() {
		return rules;
	}

	@Override
	public void setEval(Evaluator evaluator) {
		return;// Only integer dimension used.
	}

	private LinkedList<Integer> dimensions = new LinkedList<>();

	@Override
	public void addDimension(Integer... dimensions) {
		for (Integer dimension : dimensions) {
			this.dimensions.add(dimension);
		}
	}

	@Override
	public void addDimension(String... formulas) {
		for (String dimension : formulas) {
			this.dimensions.add(Integer.parseInt(dimension));
		}
	}

	@Override
	public int dimensionCount() {
		return dimensions.size();
	}

	@Override
	public List<String> getDimensions() {
		return null;
	}

	@Override
	public void extend(Membrane template) {
		for (Tunnel t : template.getTunnels()) {
			try {
				if (t.getSource() == template && t.getType() == TunnelType.In) {
					Membrane son = t.getTargets().get(0);
					Membrane sonClone = null;
					sonClone = son.deepClone();

					Tunnel cloneIn = t.getClass().newInstance();
					cloneIn.setSource(this);
					cloneIn.addTarget(sonClone);
					cloneIn.setType(TunnelType.In);
					addTunnel(cloneIn);

					Tunnel cloneOut = t.getClass().newInstance();
					cloneOut.setSource(sonClone);
					cloneOut.addTarget(this);
					cloneOut.setType(TunnelType.Out);
					sonClone.addTunnel(cloneOut);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		for (Rule rule : template.getRules()) {
			try {
				addRule((Rule) rule.deepClone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		HashMap<Obj, Integer> objmap = template.getObjects();
		Iterator<?> iter = objmap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			addObject((Obj) entry.getKey(), (Integer) entry.getValue());
		}

		Iterator<Entry<String, Object>> iter2 = template.getProperties().entrySet().iterator();
		while (iter2.hasNext()) {
			Entry<String, Object> entry = iter2.next();
			String key = entry.getKey();
			Object val = entry.getValue();
			if (!properties.containsKey(key))// 如果已经包含，则不继承
				properties.put(key, val);
		}
	}

	@Override
	public HashMap<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void fixDimension() {
	}

	@Override
	public Evaluator getEval() {
		return null;
	}

	private boolean deleted = false;

	@Override
	public void delete() {
		for (Tunnel tunnel : tunnels) {
			if (tunnel.getSource() == this && tunnel.getType() != TunnelType.Here) {
				for (Membrane target : tunnel.getTargets()) {
					List<Tunnel> ts = target.getTunnels();
					for (int i = 0; i < ts.size();) {
						Tunnel t = ts.get(i);
						if (t.getSource() == this || t.getTargets().contains(this)) {
							ts.remove(i);
						} else
							i++;
					}
				}
			}
		}
		deleted = true;
	}

	private void removeTunnelInvolved(Membrane target) {
		List<Tunnel> tunnels = getTunnels();
		for (int i = 0; i < tunnels.size();) {
			Tunnel tunnel = tunnels.get(i);
			if (tunnel.getSource() == target || tunnel.getTargets().contains(target)) {
				tunnels.remove(i);
			} else
				i++;
		}
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	// @Override
	// public void deleteSon(Membrane sonMembrane) {
	// sonMembranes.remove(sonMembrane);
	// sonMembrane.setDeleted();
	// }

	@Override
	public void newStepInit() {
		// remove runtime properties
		for (boolean exist = true; exist;) {
			exist = false;
			Iterator<Entry<String, Object>> piter = this.properties.entrySet().iterator();
			while (piter.hasNext()) {
				Entry<String, Object> entry = piter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (key.toString().startsWith("$")) {
					properties.remove(key);
					exist = true;
					break;
				}
			}
		}
		// remove closed tunnels
		for (int i = 0; i < tunnels.size();) {
			if (!tunnels.get(i).isOpen()) {
				tunnels.remove(i);
				continue;
			}
			i++;
		}
	}

	private ArrayList<Tunnel> tunnels = new ArrayList<>();

	@Override
	public void addTunnel(Tunnel t) {
		if (!tunnels.contains(t))
			tunnels.add(t);
	}

	@Override
	public Tunnel getTunnel(TunnelType type, String target) {
		for (Tunnel t : tunnels) {
			if (t.getType() == type) {
				if (target == null) {
					return t;
				} else if (t.getTargetsName() == target) {
					return t;
				}
				return null;
			}
		}
		return null;
	}

	@Override
	public List<Tunnel> getTunnels() {
		return tunnels;
	}

}
