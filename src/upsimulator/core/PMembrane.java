package upsimulator.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.jeval.Evaluator;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.interfaces.UPSLogger;
import upsimulator.rules.conditions.PriorityCondition;

/**
 * The default membrane implementation, a container of objects & rules & tunnels
 * & properties.
 * 
 * @author quan
 *
 */
public class PMembrane implements Membrane {
	private static final long serialVersionUID = -1932793470654198760L;

	private String name;
	private ConcurrentHashMap<Obj, Integer> objects = new ConcurrentHashMap<Obj, Integer>();
	private ArrayList<Rule> rules = new ArrayList<>();

	/**
	 * Create an empty membrane with a name
	 * 
	 * @param name
	 *            membrane name
	 */
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

	/**
	 * Create an empty membrane with no name
	 */
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
	public PMembrane deepClone() {
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
				if (!(t.getType() == TunnelType.In || (t.getType() == TunnelType.Out && t.getSource() != this)))
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

			Membrane cloned = null;
			try {
				cloned = m.getClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
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
			Iterator<Entry<String, Object>> iter2 = m.getProperties().entrySet().iterator();
			while (iter2.hasNext()) {
				Entry<String, Object> entry = iter2.next();
				String key = entry.getKey();
				Object val = entry.getValue();
				cloned.setProperty(key, val);
			}
		}

		// clone tunnels
		for (Tunnel t : tunnels) {
			Tunnel cloned = null;
			try {
				cloned = t.getClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			cloned.setType(t.getType());
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
		rules.add(rule);
		if (rule.getConditions().get(0) instanceof PriorityCondition) {
			rules.sort(new Comparator<Rule>() {
				@Override
				public int compare(Rule o1, Rule o2) {
					if (o1.getConditions().get(0) instanceof PriorityCondition && o2.getConditions().get(0) instanceof PriorityCondition) {
						PriorityCondition p1 = (PriorityCondition) o1.getConditions().get(0);
						PriorityCondition p2 = (PriorityCondition) o2.getConditions().get(0);
						return p1.getPriority() - p2.getPriority();
					} else {
						return 0;
					}
				}
			});
		}
	}

	@Override
	public Map<Obj, Integer> getObjects() {
		return objects;
	}

	private ArrayList<Rule> urules;

	@Override
	public List<Rule> getUsableRules() throws UnpredictableDimensionException, CloneNotSupportedException {
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

	private ArrayList<Rule> fetchedRules;

	@Override
	public List<Rule> fetch() throws TunnelNotExistException {
		fetchedRules.clear();

		if (PriorityCondition.exist()) {
			for (int i = 0; i < urules.size(); i++) {
				if (urules.size() > 100 && ((urules.size() - i) % 1000 == 0)) {
					UPSLogger.info(this, "Membrane " + getNameDim() + " fetched " + i + "/" + urules.size() + " rules");
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
		} else {
			LinkedList<Rule> rules = new LinkedList<>(urules);
			for (int i = 0; rules.size() > 0; i++) {
				if (urules.size() > 100 && i % 1000 == 0)
					UPSLogger.info(this, "Membrane " + getNameDim() + " has fetched " + rules.size() + "/" + urules.size() + " rules");

				Rule first = rules.removeFirst();
				if (first.fetch(this)) {
					fetchedRules.add(first);
					rules.add(first);
					for (Result result : first.getResults()) {
						try {
							result.selectTunnel(this).holdResult(result);
						} catch (TunnelNotExistException e) {
							e.printStackTrace();
							throw e;
						}
					}
				}
			}
		}

		return fetchedRules;
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
				nameDim += "[" + dim + "]";
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
		if (isDeleted())
			return "";
		return toString(" ", true, true, false, true, false);
	}

	@Override
	public String toString(String space, boolean withObject, boolean withProp, boolean withRule, boolean withSubmembrane, boolean withTunnel) {
		StringBuilder mBuilder = new StringBuilder("Membrane " + getNameDim() + " {\n");

		if (withObject && objects.size() > 0) {
			StringBuilder oBuilder = new StringBuilder(space + "Object ");
			Iterator<Entry<Obj, Integer>> iter = objects.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Obj, Integer> entry = iter.next();
				Obj key = entry.getKey();
				Object val = entry.getValue();
				if (iter.hasNext()) {
					if ((Integer) val > 1) {
						oBuilder.append(key + "^" + val + ", ");
					} else {
						oBuilder.append(key + ", ");
					}
				} else {
					if ((Integer) val > 1) {
						oBuilder.append(key + "^" + val + "; \n");
					} else {
						oBuilder.append(key + "; \n");
					}
				}
			}
			if (objects.size() > 0)
				mBuilder.append(oBuilder);
		}

		if (withProp && properties.size() > 0) {
			StringBuilder pBuilder = new StringBuilder(space + "Property ");
			Iterator<Entry<String, Object>> piter = this.properties.entrySet().iterator();
			while (piter.hasNext()) {
				Entry<String, Object> entry = piter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (!key.toString().startsWith("$")) {
					pBuilder.append(key + "=" + val);
					if (piter.hasNext())
						pBuilder.append(", ");
					else
						pBuilder.append(";\n");
				}
			}
			mBuilder.append(pBuilder);
		}

		if (withSubmembrane && getChildren().size() > 0) {
			StringBuilder smBuilder = new StringBuilder();
			for (Membrane son : getChildren()) {
				String smString = son.toString(space, withObject, withProp, withRule, withSubmembrane, withTunnel);
				Scanner scanner = new Scanner(smString);
				while (scanner.hasNextLine()) {
					smBuilder.append(space);
					smBuilder.append(scanner.nextLine());
					smBuilder.append("\n");
				}
				scanner.close();
			}
			mBuilder.append(smBuilder);
		}

		if (withTunnel && tunnels.size() > 0) {
			mBuilder.append(space + "Tunnel ");
			boolean hasOpenedTunnel = false;
			for (int i = 0; i < tunnels.size(); i++) {
				if (!tunnels.get(i).isOpen() || tunnels.get(i).getType() != TunnelType.Go)
					continue;
				hasOpenedTunnel = true;
				mBuilder.append(tunnels.get(i));
				if (i == tunnels.size() - 1) {
					mBuilder.append(";\n");
				} else {
					mBuilder.append(", ");
				}
			}
			if (!hasOpenedTunnel)
				mBuilder.replace(0, mBuilder.length(), "");
		}

		if (withRule) {
			for (Rule rule : rules) {
				mBuilder.append(space + rule + "\n");
			}
		}

		mBuilder.append("}");
		return mBuilder.toString();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private ConcurrentHashMap<String, Object> properties = new ConcurrentHashMap<>();

	@Override
	public void setProperty(String propertyName, Object propertyValue) {
		if (propertyValue != null)
			properties.put(propertyName, propertyValue);
		else
			properties.remove(propertyName);
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
			}
		}

		for (Rule rule : template.getRules()) {
			addRule((Rule) rule.deepClone());
		}

		Map<Obj, Integer> objmap = template.getObjects();
		Iterator<?> iter = objmap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
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
	public Map<String, Object> getProperties() {
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
		deleted = true;
		for (Tunnel tunnel : tunnels) {
			tunnel.close();
			if (tunnel.getSource() == this && (tunnel.getType() == TunnelType.Go || tunnel.getType() == TunnelType.Out)) {
				for (Membrane target : tunnel.getTargets()) {
					List<Tunnel> ts = target.getTunnels();
					for (int i = 0; i < ts.size(); i++) {
						Tunnel t = ts.get(i);
						if (t.getSource() == this || t.getTargets().contains(this))
							t.close();
					}
				}
			}
		}
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

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
