package upsimulator.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.interfaces.Tunnel.TunnelType;

/**
 * Represents a membrane which contains objects, rules, dimensions
 * 
 * @author quan
 *
 */
public interface Membrane extends Name, Cloneable, Dimension {

	public static HashMap<String, Membrane> membraneClass = new HashMap<>();
	public static HashMap<String, Boolean> membraneClassStatus = new HashMap<>();

	/**
	 * Register a Membrane Class
	 * 
	 * @param name
	 *            name of membrane class
	 * @param membrane
	 *            template of membrane class
	 * @param predefined
	 *            if it is a predefined membrane class. Predefined membrane classes
	 *            can be membranes which use Java code to implement
	 *            <tt>Membrane</tt> interface, other than use <tt>UPLanguage</tt> to
	 *            define a membrane class.
	 */
	public static void registMemClass(String name, Membrane membrane, boolean predefined) {
		membraneClass.put(name, membrane);
		membraneClassStatus.put(name, predefined);
	}

	/**
	 * Create an instance of a membrane class
	 * 
	 * @param membraneName
	 *            the name of membrane class
	 * @return an instance or {@code null} if membrane class required does not exist
	 */
	public static Membrane getMemInstanceOf(String membraneName) {
		if (membraneClass.containsKey(membraneName)) {
			return membraneClass.get(membraneName).deepClone();
		} else {
			return null;
		}
	}

	/**
	 * Check if a name is the name of one predefined membrane class
	 * 
	 * @param membraneName
	 *            the name used to check
	 * @return return {@code true} if the name is registered as a predefined
	 *         membrane class, else return {@code false}
	 */
	public static boolean isPredefinedMem(String membraneName) {
		Boolean fBoolean = membraneClassStatus.get(membraneName);
		if (fBoolean == null) {
			return false;
		} else
			return fBoolean;
	}

	/**
	 * Get the template of one membrane class
	 * 
	 * @param name
	 *            the name of membrane class
	 * @return template membrane
	 */
	public static Membrane getMemClass(String name) {
		return membraneClass.get(name);
	}

	/**
	 * Remove runtime properties whose name starts with {@code $} and initialize
	 * this membrane for a new simulation
	 */
	public void newStepInit();

	/**
	 * Get the quantity of object
	 * 
	 * @param object
	 *            P object
	 * @return the quantity of object
	 */
	public int getNumOf(Obj object);

	/**
	 * Add object to this membrane
	 * 
	 * @param object
	 *            the new added object
	 * @param num
	 *            the quantity of object
	 */
	public void addObject(Obj object, int num);

	/**
	 * Reduce object in this membrane
	 * 
	 * @param object
	 *            the object which will be reduced
	 * @param num
	 *            the reduced quantity
	 * @return if the object's quantity is greater than num, then reduce it and
	 *         return {@code true}
	 */
	public boolean reduceObject(Obj object, int num);

	/**
	 * Get all the objects contained in this membrane
	 * 
	 * @return objects and their quantity
	 */
	public Map<Obj, Integer> getObjects();

	/**
	 * Add a new rule
	 * 
	 * @param a
	 *            new rule
	 */
	public void addRule(Rule rule);

	/**
	 * Get all the rules contained in this membrane
	 * 
	 * @return All the rules
	 */
	public List<Rule> getRules();

	/**
	 * Add a tunnel between two membranes.
	 * 
	 * @param t
	 *            the tunnel between two membrane, through it the results of rules
	 *            can be set.
	 */
	public void addTunnel(Tunnel t);

	/**
	 * Get the tunnel from current to target
	 * 
	 * @param type
	 *            the type of the tunnel
	 * @param target
	 *            the name of the target membrane. It can be null if the type is
	 *            Out, Here, One2Multi or One2MultiRandom.
	 * @return the tunnel to target. Null will be returned if not found.
	 */
	public Tunnel getTunnel(TunnelType type, String target);

	/**
	 * Get all the tunnels of current membrane
	 * 
	 * @return All the tunnels of current membrane
	 */
	public List<Tunnel> getTunnels();

	/**
	 * Delete a membrane.
	 * <p>
	 * Close all the tunnels and set current membrane deleted
	 */
	public void delete();

	/**
	 * Return if this membrane is deleted.
	 * 
	 * @return if this membrane is deleted, return {@code true}
	 */
	public boolean isDeleted();

	/**
	 * Check all the rules inside if they are satisfied, and return all the usable
	 * rules.
	 * <p>
	 * <ul>
	 * <li>Maximum parallelism and minimum parallelism can be controlled by editing
	 * the returned list.</li>
	 * <li>Inhibitors and promoters and other nonconsumption conditions can be
	 * checked first.</li>
	 * </ul>
	 * 
	 * @return All the usable rules
	 * @throws CloneNotSupportedException
	 *             if some rules, objects, cannot be cloned
	 * @throws UnpredictableDimensionException
	 *             if there exist unpredictable dimension, such as the dimension of
	 *             one rule only appears at a inhibitor
	 */
	public List<Rule> getUsableRules() throws UnpredictableDimensionException, CloneNotSupportedException;

	/**
	 * All the satisfied rules try to fetch the objects they need
	 * 
	 * @return the quantity of rules which have fetched the objects
	 * @throws TunnelNotExistException
	 *             If one rule want to get a neighbor of this membrane and no tunnel
	 *             was found
	 */
	public List<Rule> fetch() throws TunnelNotExistException;

	/**
	 * Set products of rules
	 * 
	 * @return the rules have set their product successfully
	 */
	public List<Rule> setProducts();

	/**
	 * 
	 * @param space
	 *            put in the front of items
	 * @param withObject
	 *            whether show objects or not
	 * @param withProp
	 *            whether show properties or not
	 * @param withRule
	 *            whether show rules or not
	 * @param withSubmembrane
	 *            whether show sub-membranes or not
	 * @return a string which represents this membrane
	 */
	public String toString(String space, boolean withObject, boolean withProp, boolean withRule, boolean withSubmembrane, boolean withTunnel);

	/**
	 * Deep clone a membrane
	 * 
	 * @return the cloned membrane
	 */
	public Membrane deepClone();

	/**
	 * Set property of membrane
	 * 
	 * @param propertyName
	 *            the name of property
	 * @param propertyValue
	 *            the value of property
	 */
	public void setProperty(String propertyName, Object propertyValue);

	/**
	 * Get the property's value of this membrane
	 * 
	 * @param propertyName
	 *            the name of property
	 * @return property's value. If property doesn't exist in this membrane,
	 *         {@code null} will be returned.
	 */
	public Object getProperty(String propertyName);

	/**
	 * 
	 * @return all the properties of this membrane
	 */
	public Map<String, Object> getProperties();

	/**
	 * Extend a template
	 * 
	 * @param template
	 */
	public void extend(Membrane template);

	/**
	 * Get all the children to whom this membrane has a <tt>in</to> tunnel
	 * 
	 * @return all the children
	 */
	public default List<Membrane> getChildren() {
		ArrayList<Membrane> children = new ArrayList<Membrane>();
		for (Tunnel tunnel : getTunnels()) {
			if (tunnel.getType() == TunnelType.In && !children.contains(tunnel.getTargets().get(0))) {
				children.add(tunnel.getTargets().get(0));
			}
		}
		return children;
	}

	/**
	 * Get all the neighbors to whom this membrane has a <tt>go</to> tunnel
	 * 
	 * @return all the neighbors
	 */
	public default List<Membrane> getNeighbors() {
		ArrayList<Membrane> neighbors = new ArrayList<Membrane>();
		for (Tunnel tunnel : getTunnels()) {
			if (tunnel.getType() == TunnelType.Go && !neighbors.contains(tunnel.getTargets().get(0))) {
				neighbors.add(tunnel.getTargets().get(0));
			}
		}
		return neighbors;
	}

	/**
	 * Get the parent membrane of this membrane
	 * 
	 * @return parent membrane or {@code null} if parent membrane does not exist
	 */
	public default Membrane getParent() {
		Tunnel tunnelOut = getTunnel(TunnelType.Out, null);
		if (tunnelOut != null) {
			return tunnelOut.getTargets().get(0);
		} else {
			return null;
		}
	}
}