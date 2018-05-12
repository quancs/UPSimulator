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

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.interfaces.BaseDimensional;
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
public class PMembrane extends BaseDimensional implements Membrane {
	private static final long serialVersionUID = -1932793470654198760L;

	private ConcurrentHashMap<Obj, Object> objects = new ConcurrentHashMap<Obj, Object>();
	private ArrayList<Rule> rules = new ArrayList<>();

	/**
	 * Create an empty membrane with a name
	 * 
	 * @param name
	 *            membrane name
	 */
	public PMembrane(String name) {
		super();
		setName(name);

		PTunnel tunnel = new PTunnel(TunnelType.Here);
		tunnel.setSource(this);
		tunnel.addTarget(this);
		tunnels.add(tunnel);
		urules = new HashMap<Rule, Integer>(1000);
		fetchedRules = new HashMap<Rule, Integer>(1000);
	}

	/**
	 * Create an empty membrane with no name
	 */
	public PMembrane() {
		super();
		setName("NoType");
		PTunnel tunnel = new PTunnel(TunnelType.Here);
		tunnel.setSource(this);
		tunnel.addTarget(this);
		tunnels.add(tunnel);
		urules = new HashMap<Rule, Integer>(1000);
		fetchedRules = new HashMap<Rule, Integer>(1000);
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
			Iterator<Entry<Obj, Object>> iter = m.getObjects().entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Obj, Object> entry = iter.next();
				Obj key = entry.getKey();
				Object val = entry.getValue();
				cloned.addObject(key.deepClone(), val);
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
	public Object getNumOf(Obj object) {
		if (objects.containsKey(object)) {
			return objects.get(object);
		}
		return 0;
	}

	@Override
	public void addObject(Obj object, Object num) {
		if (object instanceof Condition)
			System.err.println("add a condition as object");

		if (!deleted) {
			if (object.getClass() == PNumericObject.class) {
				Double stored = (Double) objects.get(object);
				if (stored != null) {
					objects.put(object, stored + (Double) num);
				} else {
					objects.put(object, num);
				}
			} else {
				Integer stored = (Integer) objects.get(object);
				if (stored != null) {
					objects.put(object, stored + (Integer) num);
				} else {
					objects.put(object, num);
				}
			}

		}
	}

	@Override
	public boolean reduceObject(Obj object, Object num) {
		if (object.getClass() == PNumericObject.class) {
			Double storedNum = (Double) objects.get(object);
			if (storedNum != null && storedNum >= (Double) num) {
				storedNum = storedNum - (Double) num;
				if (storedNum == 0) {
					objects.remove(object);
				} else {
					objects.put(object, storedNum);
				}
				return true;
			}
			return false;
		} else {
			Integer storedNum = (Integer) objects.get(object);
			if (storedNum != null && storedNum >= (Integer) num) {
				storedNum = storedNum - (Integer) num;
				if (storedNum == 0) {
					objects.remove(object);
				} else {
					objects.put(object, storedNum);
				}
				return true;
			}
			return false;
		}
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
	public Map<Obj, Object> getObjects() {
		return objects;
	}

	private HashMap<Rule, Integer> urules;

	@Override
	public HashMap<Rule, Integer> getUsableRules() throws UnpredictableDimensionException, CloneNotSupportedException {
		urules.clear();

		for (Rule rule : rules) {
			if (rule.getDimensionSize() > 0) {
				urules.putAll(rule.satisfiedRules(this));
			} else {
				int times = rule.satisfy(this);
				if (times > 0) {
					urules.put(rule, times);
				}
			}
		}
		return urules;
	}

	private HashMap<Rule, Integer> fetchedRules;

	@Override
	public Map<Rule, Integer> fetch() throws TunnelNotExistException {
		fetchedRules.clear();

		if (PriorityCondition.exist()) {
			for (Map.Entry<Rule, Integer> entry : urules.entrySet()) {
				Rule rule = entry.getKey();
				Integer times = entry.getValue();
				Integer trueTimes = rule.fetch(this, times);
				if (trueTimes > 0) {
					fetchedRules.put(rule, trueTimes);
					for (Result result : rule.getResults()) {
						try {
							result.selectTunnel(this).holdResult(result, trueTimes);
						} catch (TunnelNotExistException e) {
							e.printStackTrace();
							throw e;
						}
					}
				}
			}
		} else {
			LinkedList<Rule> rules = new LinkedList<>(urules.keySet());
			for (int i = 0; rules.size() > 0; i++) {
				if (urules.size() > 100 && i % 1000 == 0)
					UPSLogger.info(this, "Membrane " + getNameDim() + " has fetched " + rules.size() + "/" + urules.size() + " rules");

				Rule first = rules.removeFirst();
				Integer times = urules.get(first);
				int readyToFetch = (int) (times * Math.random());
				if (readyToFetch < 1)
					readyToFetch = 1;
				int fetched = first.fetch(this, readyToFetch);
				if (fetched > 0) {
					addFetched(first, fetched);
					for (Result result : first.getResults())
						result.selectTunnel(this).holdResult(result, fetched);

					if (fetched == readyToFetch && readyToFetch < times) {// can fetch more
						rules.add(first);
					}
				}
			}
		}

		return fetchedRules;
	}

	private void addFetched(Rule rule, int times) {
		if (fetchedRules.containsKey(rule)) {
			fetchedRules.put(rule, fetchedRules.get(rule) + times);
		} else {
			fetchedRules.put(rule, times);
		}
	}

	@Override
	public Map<Rule, Integer> setProducts() {
		for (Tunnel t : tunnels)
			t.pushResult();
		return fetchedRules;
	}

	@Override
	public String toString() {
		if (isDeleted())
			return "";
		return toString(" ", true, true, false, true, false);
	}

	@Override
	public String toString(String space, boolean withObject, boolean withProp, boolean withRule, boolean withSubmembrane, boolean withTunnel) {
		StringBuilder mBuilder = new StringBuilder();
		if (getNameDim().equals("Environment")) {
			mBuilder.append(getNameDim() + " {\n");
		} else {
			mBuilder.append("Membrane " + getNameDim() + " {\n");
		}

		if (withObject && objects.size() > 0) {
			StringBuilder oBuilder = new StringBuilder(space + "Object ");
			Iterator<Entry<Obj, Object>> iter = objects.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Obj, Object> entry = iter.next();
				Obj key = entry.getKey();
				Object val = entry.getValue();
				if (iter.hasNext()) {
					if (val instanceof Integer) {
						if ((Integer) val > 1) {
							oBuilder.append(key + "^" + val + ", ");
						} else {
							oBuilder.append(key + ", ");
						}
					} else {
						if ((Double) val > 0) {
							oBuilder.append(key + "^" + val + ", ");
						} else {
							oBuilder.append(key + ", ");
						}
					}
				} else {
					if (val instanceof Integer) {
						if ((Integer) val > 1) {
							oBuilder.append(key + "^" + val + "; \n");
						} else {
							oBuilder.append(key + "; \n");
						}
					} else {
						if ((Double) val > 0) {
							oBuilder.append(key + "^" + val + "; \n");
						} else {
							oBuilder.append(key + "; \n");
						}
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

		if (withTunnel && tunnels.size() > 0) {// Here tunnel
			StringBuilder tBuilder = new StringBuilder(space + "Tunnel ");
			boolean hasOpenedTunnel = false;
			for (int i = 0; i < tunnels.size(); i++) {
				if (!tunnels.get(i).isOpen() || tunnels.get(i).getType() != TunnelType.Go)
					continue;
				hasOpenedTunnel = true;
				tBuilder.append(tunnels.get(i));
				if (i == tunnels.size() - 1) {
					tBuilder.append(";\n");
				} else {
					tBuilder.append(", ");
				}
			}
			if (hasOpenedTunnel)
				mBuilder.append(tBuilder);
		}

		if (withRule) {
			for (Rule rule : rules) {
				mBuilder.append(space + rule + "\n");
			}
		}

		mBuilder.append("}");
		return mBuilder.toString();
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
	public void addDimension(String formula) {
		addDimension(Long.parseLong(formula));
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

		Map<Obj, Object> objmap = template.getObjects();
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
