package upsimulator.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.interfaces.Tunnel.TunnelType;

public interface Membrane extends Name, Cloneable, Dimension {

	public static HashMap<String, Membrane> membraneClass = new HashMap<>();

	public static void addMemClass(String name, Membrane membrane) {
		membraneClass.put(name, membrane);
	}

	public static Membrane getMemInstanceOf(String membraneName) {
		if (membraneClass.containsKey(membraneName)) {
			try {
				return membraneClass.get(membraneName).deepClone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public static Membrane getMemClass(String name) {
		return membraneClass.get(name);
	}

	public void newStepInit();

	/**
	 * 获取object的数量
	 * 
	 * @param object
	 * @return
	 */
	public int getNumOf(Obj object);

	/**
	 * 增加obj的数量
	 * 
	 * @param object
	 * @param num
	 */
	public void addObject(Obj object, int num);

	/**
	 * 减少obj的数量
	 * 
	 * @param object
	 * @param num
	 */
	public boolean reduceObject(Obj object, int num);

	/**
	 * 获取全部的对象
	 * 
	 * @return
	 */
	public HashMap<Obj, Integer> getObjects();

	/**
	 * 增加rule
	 * 
	 * @param rule
	 */
	public void addRule(Rule rule);

	/**
	 * 获取所有的规则
	 * 
	 * @return 全部的规则
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

	// /**
	// * 新增子膜
	// *
	// * @param sonMembrane
	// */
	// public void addSon(Membrane sonMembrane);
	//
	// /**
	// * 从膜中删除子膜
	// *
	// * @param sonMembrane
	// */
	// public void deleteSon(Membrane sonMembrane);
	//
	// /**
	// * 获取所有子膜
	// *
	// * @return
	// */
	// public List<Membrane> getSons();

	/**
	 * 设置膜处于被删除状态
	 */
	public void delete();

	/**
	 * 获取膜的状态
	 * 
	 * @return 膜是否处于被删除了的状态
	 */
	public boolean isDeleted();

	// /**
	// * 设置父膜，需要注意的是，PositionResult里面用到的父膜也是需要更改的
	// *
	// * @param fatherMembrane
	// */
	// public void setFather(Membrane fatherMembrane);
	//
	// /**
	// * 获取父膜
	// *
	// * @return 父膜
	// */
	// public Membrane getFather();

	/**
	 * 设置此方法的目的：<br>
	 * 1.为了实现极大并行性、极小并行性 <br>
	 * 2.为了使得催化剂、抑制剂等不参与反应的物质条件，能同时参与反应和发挥促进抑制效果（因为检查物质条件是此步【先发生】，发生反应是fetch这一步【后发生】）
	 * 获取所有可用的规则
	 * 
	 * @return 所有可用的规则
	 * @throws CloneNotSupportedException
	 * @throws UnpredictableDimensionException
	 */
	public List<Rule> getUseableRules() throws UnpredictableDimensionException, CloneNotSupportedException;

	/**
	 * 设置此方法的目的：检查是否存在规则不兼容，比如优先级规则，1优先级的规则和2优先级的规则之间是不兼容的，需要从可用列表删除2优先级的规则
	 * 获取所有可用的规则
	 * 
	 * @return 所有已知的可用的规则
	 */
	// public List<Rule> getKnownUseableRules();

	/**
	 * 从 所有可用的规则中尝试取出所有需要的对象
	 * 
	 * @return 规则数
	 * @throws TunnelNotExistException
	 */
	public int fetch() throws TunnelNotExistException;

	/**
	 * 设置结果
	 * 
	 * @return 规则数
	 */
	public List<Rule> setProducts();

	public String toStringWithRule();

	/**
	 * 用于实现对象的深拷贝，因为膜的类型声明，所以声明一个膜的时候需要深拷贝
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Membrane deepClone() throws CloneNotSupportedException, InstantiationException, IllegalAccessException;

	public void setProperty(String propertyName, Object propertyValue);

	public Object getProperty(String propertyName);

	public Map<String, Object> getProperties();

	public void extend(Membrane template);
	//
	// public default void addChild(Membrane child) {
	// Tunnel cloneIn = t.getClass().newInstance();
	// cloneIn.setSource(this);
	// cloneIn.addTarget(sonClone);
	// cloneIn.setType(TunnelType.In);
	// addTunnel(cloneIn);
	//
	// Tunnel cloneOut = t.getClass().newInstance();
	// cloneOut.setSource(sonClone);
	// cloneOut.addTarget(this);
	// cloneOut.setType(TunnelType.Out);
	// sonClone.addTunnel(cloneOut);
	// }

	public default List<Membrane> getChildren() {
		ArrayList<Membrane> children = new ArrayList<Membrane>();
		for (Tunnel tunnel : getTunnels()) {
			if (tunnel.getType() == TunnelType.In && !children.contains(tunnel.getTargets().get(0))) {
				children.add(tunnel.getTargets().get(0));
			}
		}
		return children;
	}

	public default Membrane getParent() {
		Tunnel tunnelOut = getTunnel(TunnelType.Out, null);
		if (tunnelOut != null) {
			return tunnelOut.getTargets().get(0);
		} else {
			return null;
		}
	}
}