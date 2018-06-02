package upsimulator.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.UPSLogger;
import upsimulator.recognizer.UPLanguageLexer;
import upsimulator.recognizer.UPLanguageParser;
import upsimulator.recognizer.UPLanguageRecognizer;

/**
 * PController is a controller of simulation.<br>
 * Maximum and minimum parallelism can be controlled in the class.
 * 
 * @author quan
 *
 */
@SuppressWarnings("deprecation")
public class PController extends Thread {
	public enum PMethod {
		maximum, minmum
	}

	private ArrayList<Membrane> records = new ArrayList<>();

	private PMethod method;

	private Membrane environment;

	private int leftStep = 0;
	private int step;

	private ArrayList<Membrane> smList = new ArrayList<>();// run satisfy step.
	private ArrayList<Membrane> fmList = new ArrayList<>();// run fetch step.
	private ArrayList<Membrane> rmList = new ArrayList<>();// run setResult step.

	/**
	 * Create a controller using the string description of environment
	 * 
	 * @param string
	 *            the string description of environment in UPL
	 */
	public PController(String string) {
		this.method = PMethod.maximum;
		environment = getEnvironment(string);
		if (environment != null)
			records.add((Membrane) environment.deepClone());
		step = 1;
	}

	/**
	 * Recognize simulation environment
	 * 
	 * @param modelInstanceStr
	 *            the string description of environment
	 * @return environment
	 */
	public Membrane getEnvironment(String modelInstanceStr) {
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
		UPLanguageRecognizer<Object> visitor = null;
		PMembrane membrane = null;
		try {
			UPLanguageLexer lexer = new UPLanguageLexer(new ANTLRInputStream(modelInstanceStr));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();

			UPLanguageParser parser = new UPLanguageParser(tokens);

			parser.setContext(new ParserRuleContext());

			UPLanguageParser.StartContext tree = parser.start();

			visitor = new UPLanguageRecognizer<>();
			membrane = (PMembrane) visitor.visit(tree);
		} catch (Exception e) {
			System.err.println(e);
		}

		// read error messages
		System.err.flush();
		System.err.close();
		System.setErr(syserr);
		try {
			Scanner scanner = new Scanner(errFile);
			if (scanner.hasNextLine())
				UPSLogger.error(this, scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (membrane != null) {
			UPSLogger.debug(this, "Recogize environment done.\n");
			UPSLogger.debug(this, membrane);
			UPSLogger.result(this, membrane);
		} else {
			UPSLogger.error(this, "Recogize environment failed.\n");
		}

		return membrane;
	}

	@Override
	public void run() {
		super.run();

		while (true) {
			while (leftStep > 0) {
				runOneStep();
				leftStep--;
			}

			for (; leftStep == -1 && (runOneStep() > 0 || satisfiedRulesCount > 0);)
				;

			leftStep = 0;
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Simulate till no rules can be used.
	 */
	public void runToStop() {
		leftStep = -1;
	}

	/**
	 * Simulate the environment with steps specified
	 * 
	 * @param steps
	 *            Steps need to simulate
	 */
	public void runSteps(int steps) {
		leftStep = steps;
	}

	private int satisfiedRulesCount = 0;

	/**
	 * Simulate one step
	 * 
	 * @return rules used
	 */
	private int runOneStep() {
		long startTime = System.nanoTime();

		smList.clear();
		fmList.clear();
		rmList.clear();
		satisfiedRulesCount = 0;

		smList.add(environment);
		for (int i = 0; i < smList.size(); i++) {
			Membrane membrane = smList.get(i);
			try {
				UPSLogger.info(this, "Membrane " + membrane.getNameDim() + " is checking usable rules");
				Map<Rule, Integer> uRules = membrane.getUsableRules();
				satisfiedRulesCount += uRules.size();

				// 此处控制极大和极小并行，以及随机执行也是在此控制
				smList.addAll(((PMembrane) membrane).getChildren());
				fmList.add(membrane);
			} catch (UnpredictableDimensionException e) {
				e.printStackTrace();
				UPSLogger.error(this, e);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				UPSLogger.error(this, e.getMessage());
			}
		}

		for (int i = 0; i < fmList.size(); i++) {
			Membrane membrane = fmList.get(i);
			try {
				UPSLogger.info(this, "Membrane " + membrane.getNameDim() + " is fetching objects");
				Map<Rule, Integer> fetchedRules = membrane.fetch();
				rmList.add(membrane);
			} catch (TunnelNotExistException e) {
				e.printStackTrace();
				leftStep = 0;
				return 0;
			}
		}

		HashMap<Membrane, Map<Rule, Integer>> uMap = new HashMap<>();
		int totalUsed = 0;
		for (int i = 0; i < rmList.size(); i++) {
			Membrane membrane = rmList.get(i);
			UPSLogger.info(this, "Membrane " + membrane.getNameDim() + " is setting products");
			Map<Rule, Integer> rList = membrane.setProducts();
			uMap.put(membrane, new HashMap<>(rList));
			totalUsed += rList.size();
		}

		for (Membrane m : smList) {
			m.newStepInit();
		}

		long endTime = System.nanoTime();
		long timeUsed = endTime - startTime;
		UPSLogger.result(this, "step:" + step + "\t\trules used : " + totalUsed + "\t\ttime used:" + ((double) timeUsed / 1000000) + "ms");
		UPSLogger.debug(this, "\n" + "step:" + step);
		UPSLogger.debug(this, uMap);
		UPSLogger.result(this, environment.toString() + "\n");

		if (enableRecords) {
			Membrane eClone = environment.deepClone();
			records.add(eClone);
		}
		step++;

		return totalUsed;
	}

	private boolean enableRecords = true;

	/**
	 * Get all the records of simulation result
	 * 
	 * @return Records
	 */
	public ArrayList<Membrane> getRecords() {
		return records;
	}

	public boolean isEnableRecords() {
		return enableRecords;
	}

	public void setEnableRecords(boolean enableRecords) {
		this.enableRecords = enableRecords;
	}
}