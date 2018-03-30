package upsimulator.gui;

import java.io.Serializable;

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
