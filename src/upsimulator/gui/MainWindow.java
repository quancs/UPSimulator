package upsimulator.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.antlr.v4.gui.TestRig;

import upsimulator.core.PController;
import upsimulator.core.PMembrane;
import upsimulator.gui.FileDescriber.State;
import upsimulator.gui.FileDescriber.Type;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.UPSLogger;

/**
 * User interface
 * 
 * @author quan
 *
 */
public class MainWindow extends UPSLogger implements TreeSelectionListener, ItemListener {

	private JFrame frmUpsimulator;
	private JSplitPane splitPane;
	private JList<String> modelFileJList;
	private JList<String> instanceFileJList;

	// the instance files
	private UPLFileListModel instances = new UPLFileListModel();
	// the model files
	private UPLFileListModel models = new UPLFileListModel();
	private Settings settings = new Settings();
	private final JSplitPane lrSplitPane = new JSplitPane();
	private JTextPane editor;
	private FileDescriber fileInEditor;
	private JTabbedPane tabbedPane;
	private JComboBox<String> singleInstance;
	private static JTextPane resultConsole;
	private HashMap<String, PController> controllerMap = new HashMap<>();
	private JComboBox<String> instanceSelected;
	private JSpinner cycleSelected;
	private JTree membraneTree;
	private JSplitPane splitPaneVisualization;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}

				try {
					MainWindow window = new MainWindow();
					window.frmUpsimulator.setVisible(true);
					UPSLogger.setLogger(window);
					UPSLogger.setEnable(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void importModel() {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setMultiSelectionEnabled(true);
		int status = fileChooser.showOpenDialog(null);

		if (status == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();
			for (File file : selectedFiles) {
				FileDescriber fileDescriber = new FileDescriber(file, Type.Instance);
				models.append(fileDescriber);
			}
		} else if (status == JFileChooser.CANCEL_OPTION) {
			System.out.println("canceled");
		}

		updatePane();
	}

	private void importInstance() {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setMultiSelectionEnabled(true);
		int status = fileChooser.showOpenDialog(null);

		if (status == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();
			for (File file : selectedFiles) {
				FileDescriber fileDescriber = new FileDescriber(file, Type.Instance);
				instances.append(fileDescriber);
			}
		} else if (status == JFileChooser.CANCEL_OPTION) {
			System.out.println("canceled");
		}
		updatePane();
	}

	private void updatePane() {
		singleInstance.removeAllItems();
		instanceSelected.removeAllItems();
		for (int i = 0; i < instances.getSize(); i++) {
			singleInstance.addItem(instances.getFileDescriber(i).getFile().getName());
			instanceSelected.addItem(instances.getFileDescriber(i).getFile().getName());
		}
	}

	private void save() {
		try {
			fileInEditor.save();
			setFileDescriptorState(fileInEditor, State.Saved);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveAll() {
		for (int i = 0; i < instances.getSize(); i++) {
			try {
				instances.getFileDescriber(i).save();
				instances.setState(i, State.Saved);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < models.getSize(); i++) {
			try {
				models.getFileDescriber(i).save();
				models.setState(i, State.Saved);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void exit() {
		writeSettings();
		System.exit(0);
	}

	private void initMembrane() {
		if (singleInstance.getSelectedIndex() < 0) {
			resultLog(this, "No instance selected!!!");
			return;
		}

		ArrayList<File> files = new ArrayList<>();
		files.add(instances.getFileDescriber(singleInstance.getSelectedIndex()).getFile());
		for (int i = 0; i < models.size(); i++) {
			files.add(models.getFileDescriber(i).getFile());
		}

		String fileName = instances.getFileDescriber(singleInstance.getSelectedIndex()).getFile().getName();
		PController controller = new PController(Util.filesToString(files));
		controller.start();
		controllerMap.put(fileName, controller);
	}

	@SuppressWarnings("unused")
	private void stopSimulate() {

	}

	private void readSettings() {
		File file = new File("settings");
		if (!file.exists()) {
			System.err.println("settings file is not exist.");
			return;
		}
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream("settings"));
			settings = (Settings) is.readObject();
			instances = (UPLFileListModel) is.readObject();
			models = (UPLFileListModel) is.readObject();
			// controllerMap=(HashMap<String, PController>) is.readObject();
			System.out.println("read settings...");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeSettings() {
		File file = new File("settings");
		file.delete();

		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("settings"));
			os.writeObject(settings);
			os.writeObject(instances);
			os.writeObject(models);
			// os.writeObject(controllerMap);
			os.close();
			System.out.println("write settings...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doubleClickInstance(MouseEvent event, int selectedIndex) {
		tabbedPane.setSelectedIndex(1);

		FileDescriber file;
		if (event.getSource() == instanceFileJList) {
			file = instances.getFileDescriber(selectedIndex);
		} else {
			file = models.getFileDescriber(selectedIndex);
		}

		if (file == fileInEditor)
			return;
		if (fileInEditor != null)
			fileInEditor.setUnsavedText(editor.getText());

		fileInEditor = file;
		if (file.getState() == State.Unsaved) {
			editor.setText(file.getUnsavedText());
			return;
		}

		try {
			Scanner sin = new Scanner(file.getFile());
			StringBuilder sb = new StringBuilder();
			for (; sin.hasNextLine();) {
				sb.append(sin.nextLine());
				if (sin.hasNextLine())
					sb.append("\n");
			}
			sin.close();
			editor.setText(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void setFileDescriptorState(FileDescriber describer, State state) {
		if (instances.contains(describer)) {
			instances.setState(describer, state);
		} else {
			models.setState(describer, state);
		}
	}

	private void editorTextChange() {
		fileInEditor.setUnsavedText(editor.getText());
		setFileDescriptorState(fileInEditor, State.Unsaved);
	}

	public MainWindow() {

		readSettings();
		initialize();
		modelFileJList.setModel(models);
		instanceFileJList.setModel(instances);

		updatePane();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUpsimulator = new JFrame();
		frmUpsimulator.setTitle("UPSimulator\r\n");
		frmUpsimulator.setBounds(100, 100, 894, 811);
		frmUpsimulator.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmUpsimulator.getContentPane().setLayout(new BorderLayout(0, 0));
		frmUpsimulator.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});

		JPanel mainPanel = new JPanel();
		frmUpsimulator.getContentPane().add(mainPanel);
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.columnWidths = new int[] { 220, 198, 0 };
		gbl_mainPanel.rowHeights = new int[] { 0, 152, 0 };
		gbl_mainPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_mainPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		mainPanel.setLayout(gbl_mainPanel);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_toolBar.gridwidth = 2;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		mainPanel.add(toolBar, gbc_toolBar);

		JButton btnSave = new JButton();
		btnSave.setText("Save");
		// btnSave.setPreferredSize(new Dimension(30, 60));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		toolBar.add(btnSave);

		JButton btnSaveAll = new JButton("Save All");
		toolBar.add(btnSaveAll);
		GridBagConstraints gbc_lrSplitPane = new GridBagConstraints();
		gbc_lrSplitPane.gridwidth = 2;
		gbc_lrSplitPane.fill = GridBagConstraints.BOTH;
		gbc_lrSplitPane.gridx = 0;
		gbc_lrSplitPane.gridy = 1;
		mainPanel.add(lrSplitPane, gbc_lrSplitPane);

		JPanel projectPanel = new JPanel();
		lrSplitPane.setLeftComponent(projectPanel);
		GridBagLayout gbl_projectPanel = new GridBagLayout();
		gbl_projectPanel.columnWidths = new int[] { 122, 0 };
		gbl_projectPanel.rowHeights = new int[] { 0, 0, 27, 0 };
		gbl_projectPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_projectPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		projectPanel.setLayout(gbl_projectPanel);

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.NORTH;
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 0;
		projectPanel.add(panel_4, gbc_panel_4);

		JLabel lblProject = new JLabel("Project");
		panel_4.add(lblProject);

		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 1;
		projectPanel.add(separator_1, gbc_separator_1);

		splitPane = new JSplitPane();
		splitPane.setToolTipText("");
		splitPane.setOneTouchExpandable(true);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 2;
		projectPanel.add(splitPane, gbc_splitPane);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setBackground(UIManager.getColor("Button.background"));
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		modelFileJList = new JList<>();
		modelFileJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doubleClickInstance(e, modelFileJList.getSelectedIndex());
					instanceFileJList.clearSelection();
				}
			}
		});
		panel.add(modelFileJList);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Environments", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBackground(UIManager.getColor("Button.background"));
		splitPane.setRightComponent(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		instanceFileJList = new JList<>();
		instanceFileJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {

				} else if (e.getClickCount() == 2) {
					doubleClickInstance(e, instanceFileJList.getSelectedIndex());
					modelFileJList.clearSelection();
				}

			}
		});
		panel_2.add(instanceFileJList);

		JMenuBar menuBar = new JMenuBar();
		frmUpsimulator.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmImportModelFile = new JMenuItem("Import Model File");
		mntmImportModelFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importModel();
			}
		});
		mnFile.add(mntmImportModelFile);

		JMenuItem mntmImportInstanceFile = new JMenuItem("Import Instance File");
		mntmImportInstanceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importInstance();
			}
		});
		mnFile.add(mntmImportInstanceFile);

		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAll = new JMenuItem("Save All");
		mntmSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAll();
			}
		});
		mnFile.add(mntmSaveAll);

		JSeparator separator_3 = new JSeparator();
		mnFile.add(separator_3);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnFile.add(mntmExit);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		enableRecords = new JCheckBoxMenuItem("Record Results(Affect Efficiency)");
		mnSettings.add(enableRecords);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedIndex() == 2)
					cycleChange((Integer) cycleSelected.getValue());
			}
		});
		lrSplitPane.setResizeWeight(0.3);
		lrSplitPane.setRightComponent(tabbedPane);

		JPanel simulatePanel = new JPanel();
		tabbedPane.addTab("Simulate", null, simulatePanel, null);
		GridBagLayout gbl_simulatePanel = new GridBagLayout();
		gbl_simulatePanel.columnWidths = new int[] { 0, 0 };
		gbl_simulatePanel.rowHeights = new int[] { 100, 0, 0 };
		gbl_simulatePanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_simulatePanel.rowWeights = new double[] { 0.0, 2.0, Double.MIN_VALUE };
		simulatePanel.setLayout(gbl_simulatePanel);

		JPanel stepByStepPanel = new JPanel();
		stepByStepPanel.setBackground(Color.WHITE);
		stepByStepPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Select Environment To Simulate", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_stepByStepPanel = new GridBagConstraints();
		gbc_stepByStepPanel.insets = new Insets(0, 0, 5, 0);
		gbc_stepByStepPanel.fill = GridBagConstraints.BOTH;
		gbc_stepByStepPanel.gridx = 0;
		gbc_stepByStepPanel.gridy = 0;
		simulatePanel.add(stepByStepPanel, gbc_stepByStepPanel);
		GridBagLayout gbl_stepByStepPanel = new GridBagLayout();
		gbl_stepByStepPanel.columnWidths = new int[] { 50, 50, 50, 50, 0 };
		gbl_stepByStepPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_stepByStepPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_stepByStepPanel.rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		stepByStepPanel.setLayout(gbl_stepByStepPanel);

		JLabel lblInstance = new JLabel("Environment");
		GridBagConstraints gbc_lblInstance = new GridBagConstraints();
		gbc_lblInstance.insets = new Insets(0, 0, 5, 5);
		gbc_lblInstance.gridx = 0;
		gbc_lblInstance.gridy = 0;
		stepByStepPanel.add(lblInstance, gbc_lblInstance);

		singleInstance = new JComboBox<String>();
		singleInstance.addItemListener(this);
		singleInstance.setBackground(Color.WHITE);
		GridBagConstraints gbc_singleInstance = new GridBagConstraints();
		gbc_singleInstance.fill = GridBagConstraints.HORIZONTAL;
		gbc_singleInstance.gridwidth = 3;
		gbc_singleInstance.insets = new Insets(0, 0, 5, 0);
		gbc_singleInstance.gridx = 1;
		gbc_singleInstance.gridy = 0;
		stepByStepPanel.add(singleInstance, gbc_singleInstance);
				
						JButton btnPause = new JButton("Run to End");
						btnPause.setToolTipText("Simulate till no rules can be used");
						btnPause.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								String instance = singleInstance.getSelectedItem().toString();
								PController controller = controllerMap.get(instance);
								if (controller != null) {
									controller.setEnableRecords(getEnableRecords().isSelected());
									controller.runToStop();
								} else
									resultLog(this, "Instance is not selected.");
							}
						});
						
								JButton btnStart = new JButton("Init");
								btnStart.setToolTipText("Initialize the simulation environment of selected file");
								btnStart.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										try {
											resultConsole.setText("");
											debugConsole.setText("");
											initMembrane();
										} catch (Exception e2) {
											e2.printStackTrace();
											// resultLog(this, e2.toString());
										}
									}
								});
								
										JButton btnGrammerCheck = new JButton("Check Grammar");
										btnGrammerCheck.setToolTipText("Check the grammar of selected environment");
										btnGrammerCheck.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												grammerCheck();
											}
										});
										GridBagConstraints gbc_btnGrammerCheck = new GridBagConstraints();
										gbc_btnGrammerCheck.insets = new Insets(0, 0, 5, 5);
										gbc_btnGrammerCheck.gridx = 0;
										gbc_btnGrammerCheck.gridy = 2;
										stepByStepPanel.add(btnGrammerCheck, gbc_btnGrammerCheck);
								GridBagConstraints gbc_btnStart = new GridBagConstraints();
								gbc_btnStart.insets = new Insets(0, 0, 5, 5);
								gbc_btnStart.gridx = 1;
								gbc_btnStart.gridy = 2;
								stepByStepPanel.add(btnStart, gbc_btnStart);
						GridBagConstraints gbc_btnPause = new GridBagConstraints();
						gbc_btnPause.insets = new Insets(0, 0, 5, 5);
						gbc_btnPause.gridx = 2;
						gbc_btnPause.gridy = 2;
						stepByStepPanel.add(btnPause, gbc_btnPause);
				
						JButton btnOneStep = new JButton("Run One Step");
						btnOneStep.setToolTipText("Simulate one step");
						btnOneStep.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								String instance = singleInstance.getSelectedItem().toString();
								PController controller = controllerMap.get(instance);
								if (controller != null) {
									controller.setEnableRecords(getEnableRecords().isSelected());
									controller.runSteps(1);
								}
							}
						});
						GridBagConstraints gbc_btnOneStep = new GridBagConstraints();
						gbc_btnOneStep.insets = new Insets(0, 0, 5, 0);
						gbc_btnOneStep.gridx = 3;
						gbc_btnOneStep.gridy = 2;
						stepByStepPanel.add(btnOneStep, gbc_btnOneStep);

		JPanel consolePanel = new JPanel();
		consolePanel.setBackground(Color.WHITE);
		consolePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Simulation Messages", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_consolePanel = new GridBagConstraints();
		gbc_consolePanel.fill = GridBagConstraints.BOTH;
		gbc_consolePanel.gridx = 0;
		gbc_consolePanel.gridy = 1;
		simulatePanel.add(consolePanel, gbc_consolePanel);
		consolePanel.setLayout(new BorderLayout(0, 0));

		consoleSplitPane = new JSplitPane();
		consoleSplitPane.setResizeWeight(0.8);
		consoleSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		consolePanel.add(consoleSplitPane);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Result", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		consoleSplitPane.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_3.add(scrollPane_1);

		resultConsole = new JTextPane();
		scrollPane_1.setViewportView(resultConsole);

		JPopupMenu popupMenu_2 = new JPopupMenu();
		addPopup(resultConsole, popupMenu_2);

		resultConsoleEnable = new JCheckBoxMenuItem("Enable Log");
		resultConsoleEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		resultConsoleEnable.setSelected(true);
		popupMenu_2.add(resultConsoleEnable);

		JMenuItem mntmClear_1 = new JMenuItem("Clear All");
		mntmClear_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultConsole.setText("");
			}
		});
		popupMenu_2.add(mntmClear_1);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Process", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		consoleSplitPane.setRightComponent(panel_5);
		panel_5.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_3 = new JScrollPane();
		panel_5.add(scrollPane_3);

		debugConsole = new JTextPane();
		scrollPane_3.setViewportView(debugConsole);

		JPopupMenu popupMenu_1 = new JPopupMenu();
		addPopup(debugConsole, popupMenu_1);

		JMenuItem mntmClear = new JMenuItem("Clear All");
		mntmClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugConsole.setText("");
			}
		});

		debugConsoleEnable = new JCheckBoxMenuItem("Enable Debug Log");
		debugConsoleEnable.setSelected(true);
		popupMenu_1.add(debugConsoleEnable);

		enableInfoLog = new JCheckBoxMenuItem("Enable Info Log");
		popupMenu_1.add(enableInfoLog);
		popupMenu_1.add(mntmClear);

		JPanel editorPanel = new JPanel();
		tabbedPane.addTab("Editor", null, editorPanel, null);
		editorPanel.setLayout(new GridLayout(0, 1, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		editorPanel.add(scrollPane);

		editor = new JTextPane();
		editor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				editorTextChange();
			}
		});
		scrollPane.setViewportView(editor);

		JPanel visualizationPanel = new JPanel();
		tabbedPane.addTab("Visualization", null, visualizationPanel, null);
		GridBagLayout gbl_visualizationPanel = new GridBagLayout();
		gbl_visualizationPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_visualizationPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_visualizationPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_visualizationPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		visualizationPanel.setLayout(gbl_visualizationPanel);

		JLabel lblInstance_1 = new JLabel("Environment");
		GridBagConstraints gbc_lblInstance_1 = new GridBagConstraints();
		gbc_lblInstance_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblInstance_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblInstance_1.gridx = 0;
		gbc_lblInstance_1.gridy = 0;
		visualizationPanel.add(lblInstance_1, gbc_lblInstance_1);

		instanceSelected = new JComboBox<String>();
		instanceSelected.addItemListener(this);
		GridBagConstraints gbc_instanceSelected = new GridBagConstraints();
		gbc_instanceSelected.insets = new Insets(0, 0, 5, 5);
		gbc_instanceSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_instanceSelected.gridx = 1;
		gbc_instanceSelected.gridy = 0;
		visualizationPanel.add(instanceSelected, gbc_instanceSelected);

		JLabel lblCycle = new JLabel("Cycle");
		GridBagConstraints gbc_lblCycle = new GridBagConstraints();
		gbc_lblCycle.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblCycle.insets = new Insets(0, 0, 5, 5);
		gbc_lblCycle.gridx = 3;
		gbc_lblCycle.gridy = 0;
		visualizationPanel.add(lblCycle, gbc_lblCycle);

		cycleSelected = new JSpinner();
		cycleSelected.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				cycleChange((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		GridBagConstraints gbc_cycleSelected = new GridBagConstraints();
		gbc_cycleSelected.insets = new Insets(0, 0, 5, 0);
		gbc_cycleSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_cycleSelected.gridx = 4;
		gbc_cycleSelected.gridy = 0;
		visualizationPanel.add(cycleSelected, gbc_cycleSelected);

		splitPaneVisualization = new JSplitPane();
		GridBagConstraints gbc_splitPaneVisualization = new GridBagConstraints();
		gbc_splitPaneVisualization.gridwidth = 5;
		gbc_splitPaneVisualization.insets = new Insets(0, 0, 0, 5);
		gbc_splitPaneVisualization.fill = GridBagConstraints.BOTH;
		gbc_splitPaneVisualization.gridx = 0;
		gbc_splitPaneVisualization.gridy = 1;
		visualizationPanel.add(splitPaneVisualization, gbc_splitPaneVisualization);

		membraneTree = new JTree();
		membraneTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Environment") {
			private static final long serialVersionUID = 1L;
		}));
		membraneTree.addTreeSelectionListener(this);
		splitPaneVisualization.setLeftComponent(membraneTree);

		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		splitPaneVisualization.setRightComponent(tabbedPane_1);

		JPanel panel_1 = new JPanel();
		tabbedPane_1.addTab("Text", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_2 = new JScrollPane();
		panel_1.add(scrollPane_2);

		textResultPane = new JTextPane();
		scrollPane_2.setViewportView(textResultPane);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(instanceFileJList, popupMenu);

		JMenuItem mntmImport = new JMenuItem("import");
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importInstance();
			}
		});
		popupMenu.add(mntmImport);

		JMenuItem mntmRemove = new JMenuItem("remove");
		mntmRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				instances.remove(instanceFileJList.getSelectedIndex());
				updatePane();
			}
		});
		popupMenu.add(mntmRemove);

		JPopupMenu popupMenu2 = new JPopupMenu();
		addPopup(modelFileJList, popupMenu2);

		JMenuItem mntmImport_1 = new JMenuItem("import");
		mntmImport_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importModel();
			}
		});
		popupMenu2.add(mntmImport_1);

		JMenuItem mntmRemove_1 = new JMenuItem("remove");
		mntmRemove_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				models.remove(modelFileJList.getSelectedIndex());
				updatePane();
			}
		});
		popupMenu2.add(mntmRemove_1);
	}

	protected void grammerCheck() {
		if (singleInstance.getSelectedIndex() < 0) {
			resultLog(this, "No instance selected!!!");
			return;
		}

		ArrayList<File> files = new ArrayList<>();
		files.add(instances.getFileDescriber(singleInstance.getSelectedIndex()).getFile());
		for (int i = 0; i < models.size(); i++) {
			files.add(models.getFileDescriber(i).getFile());
		}

		ArrayList<String> argsList = new ArrayList<>();
		argsList.add("upsimulator.recognizer.UPLanguage");
		argsList.add("start");
		argsList.add("-gui");
		for (File file : files)
			argsList.add(file.getAbsolutePath());
		String[] args = new String[argsList.size()];
		argsList.toArray(args);
		TestRig rig;
		try {
			rig = new TestRig(args);
			rig.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void cycleChange(Integer value) {
		if (value < 0) {
			cycleSelected.setValue(0);
			return;
		}

		String instance = instanceSelected.getSelectedItem().toString();
		PController controller = controllerMap.get(instance);
		if (controller != null) {
			if (value >= controller.getRecords().size()) {
				value = controller.getRecords().size() - 1;
				cycleSelected.setValue(value);
			}

			DefaultMutableTreeNode rooTreeNode = (DefaultMutableTreeNode) membraneTree.getModel().getRoot();
			rooTreeNode.removeAllChildren();
			treeNodeToMembraneMap.clear();
			genTreeNode(controller.getRecords().get(value), rooTreeNode);
			((DefaultTreeModel) membraneTree.getModel()).reload();
			textResultPane.setText(controller.getRecords().get(value).toString());
		} else {
			DefaultMutableTreeNode rooTreeNode = (DefaultMutableTreeNode) membraneTree.getModel().getRoot();
			rooTreeNode.removeAllChildren();
			((DefaultTreeModel) membraneTree.getModel()).reload();
		}
	}

	private HashMap<DefaultMutableTreeNode, Membrane> treeNodeToMembraneMap = new HashMap<>();
	private JTextPane textResultPane;
	private JSplitPane consoleSplitPane;
	private JCheckBoxMenuItem resultConsoleEnable;
	private JCheckBoxMenuItem debugConsoleEnable;
	private JTextPane debugConsole;
	private JCheckBoxMenuItem enableInfoLog;
	private JCheckBoxMenuItem enableRecords;

	private DefaultMutableTreeNode genTreeNode(Membrane membrane, DefaultMutableTreeNode node) {
		if (node == null)
			node = new DefaultMutableTreeNode(membrane.getNameDim());
		treeNodeToMembraneMap.put(node, membrane);

		for (Membrane son : ((PMembrane) membrane).getChildren()) {
			node.add(genTreeNode(son, null));
		}
		for (Entry<Obj, Number> obj : membrane.getObjects().entrySet()) {
			node.add(new DefaultMutableTreeNode(obj.getKey().getNameDim() + "^" + obj.getValue()));
		}

		return node;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) membraneTree.getLastSelectedPathComponent();
		Membrane membrane = treeNodeToMembraneMap.get(selectedNode);

		if (membrane != null)
			textResultPane.setText(membrane.toString());
		else if (selectedNode != null)
			System.err.println("Not a membrane node：" + selectedNode.toString());
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == singleInstance) {
			System.out.println("e.getSource() == singleInstance");
		} else if (e.getSource() == instanceSelected) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				cycleChange((Integer) cycleSelected.getValue());
			}
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public JSplitPane getConsoleSplitPane() {
		return consoleSplitPane;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void debugLog(Object who, Object msg) {
		if (getDebugConsoleEnable().isSelected()) {
			if (msg instanceof PMembrane) {
				Util.addMsgToJTextPane(debugConsole, ((PMembrane) msg).toString("  ", true, true, true, true, true) + "\n\n\n", Color.black, false, resultConsole.getFont().getSize());
			} else if (msg instanceof HashMap<?, ?>) {
				Iterator<Entry<Membrane, Map<?, ?>>> it = ((HashMap<Membrane, Map<?, ?>>) msg).entrySet().iterator();
				for (int j = 0; j < 100 & it.hasNext(); j++) {
					Map.Entry<Membrane, Map<?, ?>> mentry = (Entry<Membrane, Map<?, ?>>) it.next();
					Membrane membrane = mentry.getKey();
					Map<Rule, Integer> ruleUsedTimes = (Map<Rule, Integer>) mentry.getValue();

					StringBuilder sBuilder = new StringBuilder(ruleUsedTimes.size() * 100);
					sBuilder.append("rules used in " + membrane.getNameDim() + ":\n");
					Iterator<?> iter = ruleUsedTimes.entrySet().iterator();
					for (int i = 0; i < 100 && iter.hasNext(); i++) {
						Map.Entry entry = (Map.Entry) iter.next();
						Rule key = (Rule) entry.getKey();
						Integer val = (Integer) entry.getValue();
						sBuilder.append(key.toString());
						sBuilder.append(" × ");
						sBuilder.append(val);
						sBuilder.append("\n");
					}
					if (iter.hasNext())
						sBuilder.append("more rules ...\n");
					sBuilder.append("\n");
					Util.addMsgToJTextPane(debugConsole, sBuilder.toString(), Color.black, false, resultConsole.getFont().getSize());
				}
				if (it.hasNext()) {
					Util.addMsgToJTextPane(debugConsole, "more membranes ...\n", Color.black, false, resultConsole.getFont().getSize());
				}
			} else {
				Util.addMsgToJTextPane(debugConsole, msg.toString() + "\n", Color.black, false, resultConsole.getFont().getSize());
			}
		}
	}

	@Override
	public void errorLog(Object who, Object msg) {
		if (getResultConsoleEnable().isSelected())
			Util.addMsgToJTextPane(resultConsole, msg.toString() + "\n", Color.red, true, resultConsole.getFont().getSize());
	}

	@Override
	public void resultLog(Object who, Object msg) {
		if (getResultConsoleEnable().isSelected()) {
			Util.addMsgToJTextPane(resultConsole, msg.toString() + "\n", Color.black, false, resultConsole.getFont().getSize());
			if (msg instanceof Membrane)
				Util.addMsgToJTextPane(resultConsole, "\n\n", Color.black, false, resultConsole.getFont().getSize());
		}
	}

	public JCheckBoxMenuItem getResultConsoleEnable() {
		return resultConsoleEnable;
	}

	public JCheckBoxMenuItem getDebugConsoleEnable() {
		return debugConsoleEnable;
	}

	public JTextPane getDebugConsole() {
		return debugConsole;
	}

	@Override
	public void infoLog(Object who, Object msg) {
		if (getEnableInfoLog().isSelected())
			Util.addMsgToJTextPane(debugConsole, msg.toString() + "\n", Color.black, false, resultConsole.getFont().getSize());
	}

	public JCheckBoxMenuItem getEnableInfoLog() {
		return enableInfoLog;
	}

	public JCheckBoxMenuItem getEnableRecords() {
		return enableRecords;
	}
}