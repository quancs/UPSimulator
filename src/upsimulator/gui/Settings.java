package upsimulator.gui;

import java.io.Serializable;

/**
 * Software settings will be stored into file <tt>setting</tt>.
 * 
 * @author quanc
 *
 */
public class Settings implements Serializable {
	private static final long serialVersionUID = 8263423889446050644L;

	private boolean separateModelInstance = false;

	public boolean getSeparateModelInstance() {
		return separateModelInstance;
	}

	public void setSeparateModelInstance(boolean separateModelInstance) {
		this.separateModelInstance = separateModelInstance;
	}

}
