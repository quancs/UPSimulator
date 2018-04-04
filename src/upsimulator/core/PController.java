package upsimulator.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.gui.MainWindow;
import upsimulator.gui.Util;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;
import upsimulator.recognizer.UPLanguageLexer;
import upsimulator.recognizer.UPLanguageParser;
import upsimulator.recognizer.UPLanguageRecognizer;

/**
 * 控制极大并行和极小并行在此处控制 . <br>
 * Not completely finished yet. <br>
 * 优先级规则，还未确认满足
 * 
 * @author quan, pxx
 *
 */
@SuppressWarnings("deprecation")
public class PController extends Thread {

	public enum PMethod {
		maximum, minmum
	}

	/**
	 * model files & an instance file
	 */
	private ArrayList<File> files;
	private ArrayList<Membrane> records;

	/**
	 * 模拟方式：极大并行与极小并行
	 */
	private PMethod method;

	private Membrane environment;
	private PClock clock;

	public PController(ArrayList<File> files, PMethod method) {
		this.clock = PClock.getClock();
		this.files = files;
		this.method = method;

		StringBuilder stringBuilder = new StringBuilder();
		for (File file : files)
			stringBuilder.append(Util.fileToString(file));

		environment = getEnvironment(stringBuilder.toString());

		records = new ArrayList<>();
		records.add((Membrane) environment.deepClone());
	}

	public static Membrane getEnvironment(String modelInstanceStr) {
		// redirect the errors from stdio to file
		File errFile = new File("errors.log");
		if (errFile.exists())
			errFile.delete();
		else
			try {
				errFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		PrintStream syserr = System.err;
		try {
			System.setErr(new PrintStream(errFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// recoginze
		UPLanguageLexer lexer = new UPLanguageLexer(new ANTLRInputStream(modelInstanceStr));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		UPLanguageParser parser = new UPLanguageParser(tokens);

		parser.setContext(new ParserRuleContext());

		UPLanguageParser.StartContext tree = parser.start();

		UPLanguageRecognizer<Object> visitor = new UPLanguageRecognizer<>();
		PMembrane membrane = (PMembrane) visitor.visit(tree);

		// read error messages
		System.err.close();
		System.setErr(syserr);
		try {
			Scanner scanner = new Scanner(errFile);
			if (scanner.hasNextLine())
				MainWindow.appendErrMsg(scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		MainWindow.appendMsg("init the environment : ");
		MainWindow.appendMsg(membrane.toString() + "\n\n");

		return membrane;
	}

	private int leftStep = 0;

	@Override
	public void run() {
		super.run();

		while (true) {
			while (leftStep > 0) {
				runOneStep();
				leftStep--;
			}

			for (; leftStep == -1 && runOneStep() > 0;) {

			}

			leftStep = 0;
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void runToStop() {
		leftStep = -1;
	}

	public void runSteps(int steps) {
		leftStep = steps;
	}

	private ArrayList<Membrane> smList = new ArrayList<>();// run satisfy step.
	private ArrayList<Membrane> fmList = new ArrayList<>();// run fetch step.
	private ArrayList<Membrane> rmList = new ArrayList<>();// run setResult
															// step.

	public int runOneStep() {
		Calendar startTime, endTime;
		startTime = Calendar.getInstance();

		smList.clear();
		fmList.clear();
		rmList.clear();

		smList.add(environment);
		for (int i = 0; i < smList.size(); i++) {
			Membrane membrane = smList.get(i);
			try {
				MainWindow.appendLogMsg("Membrane " + membrane.getNameDim() + " are checking usable rules");
				List<Rule> uRules = membrane.getUsableRules();
				// 此处控制极大和极小并行，以及随机执行也是在此控制
				smList.addAll(((PMembrane) membrane).getChildren());
				fmList.add(membrane);
			} catch (UnpredictableDimensionException e) {
				e.printStackTrace();
				MainWindow.appendErrMsg(e.getMessage());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				MainWindow.appendErrMsg(e.getMessage());
			}
		}

		clock.nextSubstep();

		for (int i = 0; i < fmList.size(); i++) {
			Membrane membrane = fmList.get(i);
			try {
				MainWindow.appendLogMsg("Membrane " + membrane.getNameDim() + " are fetching objects");
				List<Rule> fetchedRules = membrane.fetch();
				rmList.add(membrane);
			} catch (TunnelNotExistException e) {
				e.printStackTrace();
			}
		}

		clock.nextSubstep();

		List<Rule> uList = new LinkedList<>();
		for (int i = 0; i < rmList.size(); i++) {
			Membrane membrane = rmList.get(i);
			MainWindow.appendLogMsg("Membrane " + membrane.getNameDim() + " are setting products");
			List<Rule> rList = membrane.setProducts();
			uList.addAll(rList);
		}

		clock.nextStep();
		for (Membrane m : smList) {
			m.newStepInit();
		}

		endTime = Calendar.getInstance();
		long timeUsed = endTime.getTimeInMillis() - startTime.getTimeInMillis();
		MainWindow.appendMsg("rule used : " + uList.size() + "\t\ttime used:" + timeUsed + "ms");
		MainWindow.appendMsg(uList.toString());
		MainWindow.appendMsg(environment.toString() + "\n");

		Membrane eClone = environment.deepClone();
		records.add(eClone);

		return uList.size();
	}

	public ArrayList<Membrane> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Membrane> records) {
		this.records = records;
	}
}