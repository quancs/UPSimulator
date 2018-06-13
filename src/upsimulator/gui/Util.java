package upsimulator.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import upsimulator.interfaces.UPSLogger;

/**
 * Some useful functions are collected in {@code Util}
 * 
 * @author quan
 *
 */
public class Util {
	public static void setDefaultSize(int size) {
		Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
		Object[] keys = keySet.toArray(new Object[keySet.size()]);

		for (Object key : keys) {
			if (key != null && key.toString().toLowerCase().contains("font")) {
				System.out.println(key);
				Font font = UIManager.getDefaults().getFont(key);
				if (font != null) {
					font = font.deriveFont((float) size);
					UIManager.put(key, font);
				}
			}
		}
	}

	public static String fileToString(File file) {
		Long fileLengthLong = file.length();

		byte[] fileContent = new byte[fileLengthLong.intValue()];
		try {
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(fileContent);
			inputStream.close();
		} catch (Exception e) {
			UPSLogger.error(new Object(), e.getMessage());
		}
		String string = new String(fileContent);

		return string;
	}

	public static String filesToString(List<File> fList) {
		StringBuilder stringBuilder = new StringBuilder();
		for (File file : fList)
			stringBuilder.append(Util.fileToString(file));
		return stringBuilder.toString();
	}

	public static void addMsgToJTextPane(JTextPane pane, String str, Color col, boolean bold, int fontSize) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, col);// font color

		if (bold == true) {// font type
			StyleConstants.setBold(attrSet, true);
		}

		StyleConstants.setFontSize(attrSet, fontSize);
		// Font size
		// StyleConstants.setFontFamily(attrSet, "黑体");
		// Set font

		Document doc = pane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, attrSet);
			pane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			System.out.println("BadLocationException: " + e);
		}
	}

}
