package upsimulator.gui;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import upsimulator.gui.FileDescriber.State;

/**
 * UPLanguage File List Model
 * 
 * @author quan
 * 
 */
public class UPLFileListModel extends DefaultListModel<String> {

	private static final long serialVersionUID = 1L;

	private ArrayList<FileDescriber> files = new ArrayList<>();

	public String remove(int index) {
		files.remove(index);
		return super.remove(index);
	}

	public void append(FileDescriber fileDescriber) {
		files.add(fileDescriber);
		super.addElement(fileDescriber.toString());
	}

	public void setState(int index, State state) {
		files.get(index).setState(state);
		super.setElementAt(files.get(index).toString(), index);
	}

	@Override
	public String get(int index) {
		System.err.println("get " + index + " " + files.get(index).toString());

		return files.get(index).toString();
	}

	public FileDescriber getFileDescriber(int index) {
		return files.get(index);
	}

	public boolean contains(FileDescriber fileDescriber) {
		return files.contains(fileDescriber);
	}

	public void setState(FileDescriber describer, State state) {
		int index = files.indexOf(describer);
		setState(index, state);
	}
}
