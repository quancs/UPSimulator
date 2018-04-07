package upsimulator.recognizer.actions;

import java.util.concurrent.ConcurrentHashMap;

import upsimulator.core.PMembrane;
import upsimulator.interfaces.Membrane;

/**
 * 需要推迟的编译器动作：当当前需要做的事情需要推迟的时候，就将动作推迟到文档识别结束的时候
 * 
 * @author quan
 *
 */
public abstract class RecognizerAction {

	/**
	 * 未准备好的膜
	 */
	protected static ConcurrentHashMap<String, Integer> unreadyMembranes = new ConcurrentHashMap<>();

	/**
	 * 初始化动作
	 */
	public abstract void init();

	/**
	 * 动作需要的条件是否准备好，如果需要使用的膜存在其他未被完成的动作，则未准备好
	 * 
	 * @return 是否准备好的标志
	 */
	public abstract boolean ready();

	/**
	 * 做出推迟的动作
	 */
	public abstract void doAction();

	protected void minusUnreadyCount(Membrane membrane) {
		for (; membrane != null;) {
			String memName = membrane.getName();
			minusUnreadyCount(memName);
			membrane = ((PMembrane) membrane).getParent();
		}
	}

	private void minusUnreadyCount(String memName) {
		Integer count = unreadyMembranes.get(memName);
		if (count != null) {
			if (count != 1)
				unreadyMembranes.put(memName, --count);
			else {
				unreadyMembranes.remove(memName);
				count--;
			}
		} else {
			throw new RuntimeException("发生计数错误 : " + memName);
		}
	}

	protected void addUnreadyCount(Membrane membrane) {
		for (; membrane != null;) {
			String memName = membrane.getName();
			addUnreadyCount(memName);
			membrane = ((PMembrane) membrane).getParent();
		}
	}

	private void addUnreadyCount(String memName) {
		Integer count = 0;
		if (unreadyMembranes.containsKey(memName))
			count = unreadyMembranes.get(memName);
		count++;
		unreadyMembranes.put(memName, count);
	}

	protected boolean ready(String memName) {
		// if (Membrane.getMemClass(memName) == null)
		// throw new RuntimeException("发现未被定义的膜类：" + memName);

		if (unreadyMembranes.containsKey(memName)) {
			return false;
		} else {
			return true;
		}
	}
}
