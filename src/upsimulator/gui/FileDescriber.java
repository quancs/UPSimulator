package upsimulator.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class FileDescriber implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		Instance, Model
	}

	public enum State {
		Saved, Unsaved
	}

	private File file;
	private Type type;
	private String unsavedText;

	private State state;

	public FileDescriber(File file, Type type) {
		super();
		this.file = file;
		this.type = type;
		this.setState(State.Saved);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		String str = file.getName() + (state == State.Unsaved ? "*" : "");
		return str;
	}

	public String getUnsavedText() {
		return unsavedText;
	}

	public void setUnsavedText(String unsavedText) {
		this.unsavedText = unsavedText;
	}

	public void save() throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(unsavedText);
		writer.close();
	}
}