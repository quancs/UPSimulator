package upsimulator.core;

import java.util.ArrayList;
import java.util.Collections;
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
import upsimulator.interfaces.BasicName;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.MembraneListener;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.Tunnel;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.interfaces.UPSLogger;
import upsimulator.rules.conditions.PriorityCondition;
import upsimulator.rules.results.ObjectResult;

/**
 * The default membrane implementation, a container of objects & rules & tunnels
 * & properties.
 * 
 * @author quan
 *
 */
public class PMembrane extends BasicName implements Membrane, MembraneListener {
	private static final long serialVersionUID = -1932793470654198760L;

	private ConcurrentHashMap<Obj, Number> objects = new ConcurrentHashMap<Obj, Number>();
	private ArrayList<Rule> rules = new ArrayList<>();
	private ArrayList<MembraneListener> listeners = new ArrayList<>();

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

	@SuppressWarnings("deprecation")
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
				if (!(t.getType() == TunnelType.In || t.getType() == TunnelType.Go || (t.getType() == TunnelType.Out && t.getSource() != this)))
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
			Iterator<Entry<Obj, Number>> iter = m.getObjects().entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Obj, Number> entry = iter.next();
				Obj key = entry.getKey();
				Number val = entry.getValue();
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
	public Number getNumOf(Obj object) {
		if (objects.containsKey(object)) {
			if (object.isAntiObject()) {
				if (object.getClass() == PNumericObject.class)
					return (Double) objects.get(object) * -1;
				else
					return (Integer) objects.get(object) * -1;
			} else
				return objects.get(object);
		}
		return 0;
	}

	@Override
	public void addObject(Obj object, Number num) {
		if (!deleted) {
			if (object.isAntiObject()) {
				if (object.getClass() == PNumericObject.class) {
					num = (Double) num * -1;
				} else {
					num = (Integer) num * -1;
				}
			}

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
					if (object instanceof ObjectResult || object.isAntiObject()) {
						object = new PObject((PObject) object);
						((PObject) object).setAnti(false);
					}
					objects.put(object, num);
				}
			}
		}
	}

	@Override
	public boolean reduceObject(Obj object, Number num) {
		if (object.isAntiObject()) {
			if (object.getClass() == PNumericObject.class) {
				num = (Double) num * -1;
			} else {
				num = (Integer) num * -1;
			}
		}

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
	public Map<Obj, Number> getObjects() {
		return objects;
	}

	private HashMap<Rule, Integer> urules;

	@Override
	public HashMap<Rule, Integer> getUsableRules() throws UnpredictableDimensionException, CloneNotSupportedException {
		urules.clear();
		for (int i = listeners.size() - 1; i >= 0; i--)
			listeners.get(i).startChecking(this);

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

		for (int i = listeners.size() - 1; i >= 0; i--)
			listeners.get(i).endChecking(this);

		return urules;
	}

	private HashMap<Rule, Integer> fetchedRules;

	@Override
	public Map<Rule, Integer> fetch() throws TunnelNotExistException {
		fetchedRules.clear();
		for (int i = listeners.size() - 1; i >= 0; i--)
			listeners.get(i).startFetching(this);

		if (PriorityCondition.exist()) {
			List<Map.Entry<Rule, Integer>> entryList = new ArrayList<>(urules.entrySet());
			entryList.sort(new Comparator<Map.Entry<Rule, Integer>>() {
				@Override
				public int compare(Entry<Rule, Integer> o1, Entry<Rule, Integer> o2) {
					Rule r1 = o1.getKey();
					Rule r2 = o2.getKey();
					PriorityCondition p1 = getPriorityCondition(r1);
					PriorityCondition p2 = getPriorityCondition(r2);
					if (p1.getPriority() != p2.getPriority())
						return p1.getPriority() - p2.getPriority();
					else
						return Math.random() > 0.5 ? -1 : 1;
				}

				private PriorityCondition getPriorityCondition(Rule rule) {
					for (Condition condition : rule.getConditions())
						if (condition instanceof PriorityCondition)
							return (PriorityCondition) condition;
					return new PriorityCondition(0);
				}

			});

			for (Map.Entry<Rule, Integer> entry : entryList) {
				Rule rule = entry.getKey();
				Integer times = entry.getValue();
				Integer trueTimes = rule.fetch(this, times);
				if (trueTimes > 0) {
					fetchedRules.put(rule, trueTimes);
					for (Result result : rule.getResults()) {
						result.selectTunnel(this).holdResult(result, trueTimes);
					}
				}
			}
		} else {
			LinkedList<Rule> rules = new LinkedList<>(urules.keySet());
			Collections.shuffle(rules);
			for (int i = 0; rules.size() > 0; i++) {
				if (urules.size() > 100 && i % 1000 == 0)
					UPSLogger.info(this, "Membrane " + getNameDim() + " has fetched " + rules.size() + "/" + urules.size() + " rules");

				Rule first = rules.removeFirst();
				Integer times = urules.get(first);
				int readyToFetch = (int) (times * Math.random()) + 1;
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

		for (int i = listeners.size() - 1; i >= 0; i--)
			listeners.get(i).endFetching(this);
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
		for (int i = listeners.size() - 1; i >= 0; i--)
			listeners.get(i).startSetting(this);

		for (int i = 0, size = tunnels.size(); i < size; i++)
			tunnels.get(i).pushResult();

		endSetting(this);
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
			Iterator<Entry<Obj, Number>> iter = objects.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Obj, Number> entry = iter.next();
				Obj key = entry.getKey();
				Number val = entry.getValue();

				String name = key.toString();
				if (val instanceof Integer) {
					if ((Integer) val < 0) {
						val = (Integer) val * -1;
						name = "-" + name;
					}
				} else {
					if ((Double) val < 0) {
						val = (Double) val * -1;
						name = "-" + name;
					}
				}

				if (iter.hasNext()) {
					if (val instanceof Integer) {
						if (val.intValue() == 1) {
							oBuilder.append(name + ", ");
						} else {
							oBuilder.append(name + "^" + val + ", ");
						}
					} else {
						if ((Double) val > 0) {
							oBuilder.append(name + "^" + val + ", ");
						} else {
							oBuilder.append(name + ", ");
						}
					}
				} else {
					if (val instanceof Integer) {
						if (val.intValue() == 1) {
							oBuilder.append(name + "; \n");
						} else {
							oBuilder.append(name + "^" + val + "; \n");
						}
					} else {
						if ((Double) val > 0) {
							oBuilder.append(name + "^" + val + "; \n");
						} else {
							oBuilder.append(name + "; \n");
						}
					}
				}
			}
			if (objects.size() > 0)
				mBuilder.append(oBuilder);
		}

		if (withProp && properties.size() > 0) {
			boolean has = false;
			StringBuilder pBuilder = new StringBuilder(space + "Property ");
			Iterator<Entry<String, Object>> piter = this.properties.entrySet().iterator();
			while (piter.hasNext()) {
				Entry<String, Object> entry = piter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (!key.toString().startsWith("$")) {
					pBuilder.append(key + "=" + val);
					has = true;
					if (piter.hasNext())
						pBuilder.append(", ");
					else
						pBuilder.append(";\n");
				}
			}
			if (has)
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

	@SuppressWarnings("deprecation")
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
			addRule(rule.deepClone());
		}

		Map<Obj, Number> objmap = template.getObjects();
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
		// initial the membrane count
		unfinishedMembraneCount = 1 + getChildren().size();
	}

	private ArrayList<Tunnel> tunnels = new ArrayList<>();

	@Override
	public void addTunnel(Tunnel t) {
		if (!tunnels.contains(t))
			tunnels.add(t);
	}

	@Override
	public Tunnel getTunnel(TunnelType type, String target) {
		LinkedList<Tunnel> tunnelsTemp = new LinkedList<>();

		for (Tunnel t : tunnels) {
			if (t.getType() == type) {
				if (target == null) {
					return t;
				} else if (t.getTargetsName().equals(target)) {
					tunnelsTemp.add(t);
				}
			}
		}
		if (tunnelsTemp.isEmpty())
			return null;
		else
			return tunnelsTemp.get((int) (Math.random() * tunnelsTemp.size()));
	}

	@Override
	public List<Tunnel> getTunnels() {
		return tunnels;
	}

	@Override
	public void addChild(Class<?> tunnelClass, Membrane child) {
		Membrane.super.addChild(tunnelClass, child);
		child.addListener(this);
	}

	@Override
	public void addListener(MembraneListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public void removeListener(MembraneListener listener) {
		listeners.remove(listeners.lastIndexOf(listener));
	}

	@Override
	public void startChecking(Membrane membrane) {

	}

	@Override
	public void endChecking(Membrane membrane) {

	}

	@Override
	public void startFetching(Membrane membrane) {
		unfinishedMembraneCount++;
	}

	@Override
	public void endFetching(Membrane membrane) {

	}

	@Override
	public void startSetting(Membrane membrane) {

	}

	private int unfinishedMembraneCount = 1;// this membrane

	@Override
	public void endSetting(Membrane membrane) {
		unfinishedMembraneCount--;
		if (unfinishedMembraneCount == 0)
			for (int i = listeners.size() - 1; i >= 0; i--)
				listeners.get(i).endSetting(this);
	}
}
