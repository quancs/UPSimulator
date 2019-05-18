package test;

import upsimulator.core.PMembrane;
import upsimulator.core.PObject;
import upsimulator.core.PRule;
import upsimulator.core.PTunnel;
import upsimulator.exceptions.TunnelNotExistException;
import upsimulator.exceptions.UnpredictableDimensionException;
import upsimulator.rules.conditions.ObjectCondition;
import upsimulator.rules.results.MembraneDivisionResult;
import upsimulator.rules.results.ObjectResult;

public class Example {

	public static void main(String[] args) {
		ObjectCondition oCondition = new ObjectCondition("a", 1);
		PObject object = new PObject("a");
		System.err.println(object.equals(oCondition));
		System.err.println(oCondition.equals(object));
		System.err.println(object.hashCode());
		System.err.println(oCondition.hashCode());

		PMembrane environment = new PMembrane("Environment");
		PMembrane a = new PMembrane("a");

		environment.addChild(a, PTunnel.class);
		a.addObject(new PObject("d"), 3);
		PRule rule = new PRule();
		rule.setName("r1");
		rule.addCondition(new ObjectCondition("d", 1));
		MembraneDivisionResult membraneDivisionResult = new MembraneDivisionResult();
		membraneDivisionResult.addObjectResult1(new ObjectResult("d"));
		membraneDivisionResult.addObjectResult2(new ObjectResult("d"));

		rule.addResult(membraneDivisionResult);
		a.addRule(rule);

		System.out.println(environment.toString("  ", true, true, true, true, true));
		System.out.println();
		try {
			System.out.println(environment.getChildren().get(0).getUsableRules());
			System.out.println(environment.getChildren().get(0).fetch());
			System.out.println(environment.getChildren().get(0).setProducts());
			System.out.println(environment.toString("  ", true, true, true, true, true));
			System.out.println();

			environment.newStepInit();

			System.out.println(environment.getChildren().get(0).getUsableRules());
			System.out.println(environment.getChildren().get(0).fetch());
			System.out.println(environment.getChildren().get(0).setProducts());
			System.out.println(environment.toString("  ", true, true, true, true, true));
			System.out.println();
		} catch (UnpredictableDimensionException | CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TunnelNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
