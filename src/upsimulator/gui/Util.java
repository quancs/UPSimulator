package upsimulator.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

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
			e.printStackTrace();
		}
		String string = new String(fileContent);

		return string;
	}

	public static void addMsgToJTextPane(JTextPane pane, String str, Color col, boolean bold, int fontSize) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, col);// 颜色

		if (bold == true) {// 字体类型
			StyleConstants.setBold(attrSet, true);
		}

		StyleConstants.setFontSize(attrSet, fontSize);
		// 字体大小
		// StyleConstants.setFontFamily(attrSet, "黑体");
		// 设置字体

		Document doc = pane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException: " + e);
		}
	}

}
