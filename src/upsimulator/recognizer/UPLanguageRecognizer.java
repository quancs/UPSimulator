package upsimulator.recognizer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.apache.log4j.Logger;

import upsimulator.core.PMembrane;
import upsimulator.core.PObject;
import upsimulator.core.PRule;
import upsimulator.core.PTunnel;
import upsimulator.interfaces.Condition;
import upsimulator.interfaces.Membrane;
import upsimulator.interfaces.Obj;
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Rule;
import upsimulator.interfaces.UPSLogger;
import upsimulator.interfaces.Tunnel.TunnelType;
import upsimulator.recognizer.UPLanguageParser.AdditionalResultsContext;
import upsimulator.recognizer.UPLanguageParser.AllContext;
import upsimulator.recognizer.UPLanguageParser.AndOptContext;
import upsimulator.recognizer.UPLanguageParser.FormulaDimContext;
import upsimulator.recognizer.UPLanguageParser.GoContext;
import upsimulator.recognizer.UPLanguageParser.HereContext;
import upsimulator.recognizer.UPLanguageParser.InContext;
import upsimulator.recognizer.UPLanguageParser.IntDimContext;
import upsimulator.recognizer.UPLanguageParser.MemDissolveResultContext;
import upsimulator.recognizer.UPLanguageParser.MemDivisionResultContext;
import upsimulator.recognizer.UPLanguageParser.MembraneTypeContext;
import upsimulator.recognizer.UPLanguageParser.ObjAssignContext;
import upsimulator.recognizer.UPLanguageParser.ObjResultContext;
import upsimulator.recognizer.UPLanguageParser.ObjectsContext;
import upsimulator.recognizer.UPLanguageParser.OrOptContext;
import upsimulator.recognizer.UPLanguageParser.OutContext;
import upsimulator.recognizer.UPLanguageParser.ProbabilisticConditionContext;
import upsimulator.recognizer.UPLanguageParser.PropertiesContext;
import upsimulator.recognizer.UPLanguageParser.PropertyConditionContext;
import upsimulator.recognizer.UPLanguageParser.PropertyInitContext;
import upsimulator.recognizer.UPLanguageParser.PropertyResultContext;
import upsimulator.recognizer.UPLanguageParser.PruleContext;
import upsimulator.recognizer.UPLanguageParser.RandomContext;
import upsimulator.recognizer.UPLanguageParser.RegConditionContext;
import upsimulator.recognizer.UPLanguageParser.RuleSetDeclareContext;
import upsimulator.recognizer.UPLanguageParser.SubmembraneContext;
import upsimulator.recognizer.UPLanguageParser.TargetContext;
import upsimulator.recognizer.UPLanguageParser.TunnelTargetContext;
import upsimulator.recognizer.UPLanguageParser.TunnelsContext;
import upsimulator.recognizer.actions.MembraneExtendAction;
import upsimulator.recognizer.actions.RecognizerAction;
import upsimulator.recognizer.actions.RuleSetDeclareAction;
import upsimulator.recognizer.actions.RuleTunnelChecker;
import upsimulator.recognizer.actions.TunnelCreateAction;
import upsimulator.rules.conditions.BooleanCondition;
import upsimulator.rules.conditions.InhibitorCondition;
import upsimulator.rules.conditions.MembranePropertyCondition;
import upsimulator.rules.conditions.MembraneStatusCondition;
import upsimulator.rules.conditions.ObjectCondition;
import upsimulator.rules.conditions.PriorityCondition;
import upsimulator.rules.conditions.ProbabilisticCondition;
import upsimulator.rules.conditions.PromoterCondition;
import upsimulator.rules.conditions.RegularExpressionCondition;
import upsimulator.rules.results.MembraneCreateResult;
import upsimulator.rules.results.MembraneDissolveResult;
import upsimulator.rules.results.MembraneDivisionResult;
import upsimulator.rules.results.MembranePropertyResult;
import upsimulator.rules.results.MembraneStatusResult;
import upsimulator.rules.results.ObjectResult;
import upsimulator.rules.results.PositionResult;
import upsimulator.rules.results.PositionResult.Target;

/**
 * This class provides an empty implementation of {@link UPLanguageVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T>
 *            The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
@SuppressWarnings("unchecked")
public class UPLanguageRecognizer<T> extends AbstractParseTreeVisitor<T> implements UPLanguageVisitor<T> {

	private static Logger logger = Logger.getLogger("Recognizer");
	private List<RecognizerAction> actions = new LinkedList<>();// 待完成工作

	private Membrane skin;
	private Membrane currMembrane;

	@Override
	public T visitStart(UPLanguageParser.StartContext ctx) {
		logger.info("visitStart");
		visitChildren(ctx);
		// 做待完成的工作
		for (RecognizerAction action : actions)
			action.init();

		for (boolean reduce = true; reduce;) {
			reduce = false;
			for (int i = 0; i < actions.size(); i++) {
				RecognizerAction action = actions.get(i);
				if (action.ready()) {
					action.doAction();
					actions.remove(i);
					i--;
					reduce = true;
				}
			}
		}

		if (actions.size() > 0) {
			UPSLogger.error(this, "Please check your code. Cannot finish following recognization jobs:");
			for (RecognizerAction action : actions)
				UPSLogger.error(this, action);
		}
		logger.info("visitStart end");
		return (T) skin;
	}

	@Override
	public T visitEnvironmentDef(UPLanguageParser.EnvironmentDefContext ctx) {
		logger.info("visitEnvironmentDef " + ctx.getText());
		skin = new PMembrane();
		skin.setName("Environment");
		currMembrane = skin;
		for (ObjectsContext oc : ctx.objects()) {
			List<Object[]> objects = (List<Object[]>) visitObjects(oc);
			for (Object[] obj : objects)
				currMembrane.addObject((Obj) obj[0], (Integer) obj[1]);
		}
		for (PruleContext pc : ctx.prule()) {
			currMembrane.addRule((Rule) visitPrule(pc));
		}

		for (SubmembraneContext sc : ctx.submembrane()) {
			PTunnel.addChildParentTunnel(currMembrane, (Membrane) visitSubmembrane(sc));
		}

		for (RuleSetDeclareContext rsdc : ctx.ruleSetDeclare())
			visitRuleSetDeclare(rsdc);// may be this rule set has not been recognized, so need actions to do this job

		for (PropertiesContext pc : ctx.properties()) {
			List<Object[]> properties = (List<Object[]>) visitProperties(pc);
			for (Object[] property : properties)
				currMembrane.setProperty((String) property[0], property[1]);
		}

		currMembrane = null;
		return null;
	}

	@Override
	public T visitRuleSetDef(UPLanguageParser.RuleSetDefContext ctx) {
		logger.info("visitRuleSetDef");
		String ruleSetType = ctx.ruleSetType().getText();
		ArrayList<Rule> ruleSet = new ArrayList<>();
		for (UPLanguageParser.PruleContext rdc : ctx.prule()) {
			Rule rule = (Rule) visitPrule(rdc);
			ruleSet.add(rule);
		}
		Rule.registRuleSet(ruleSetType, ruleSet);
		return null;
	}

	@Override
	public T visitRuleSetType(UPLanguageParser.RuleSetTypeContext ctx) {
		return null;// 上级已经做了
	}

	@Override
	public T visitMembraneDef(UPLanguageParser.MembraneDefContext ctx) {
		List<MembraneTypeContext> memTypes = ctx.membraneType();
		String memType = memTypes.get(0).getText();

		Membrane membrane;
		int i = 1;
		if (memTypes.size() >= 2 && Membrane.isPredefinedMem(memTypes.get(1).toString())) {
			membrane = Membrane.getMemInstanceOf(memTypes.get(2).toString());
			i = 2;
		} else {
			membrane = new PMembrane();
		}

		currMembrane = membrane;
		for (; i < memTypes.size(); i++) {
			MembraneExtendAction mea = new MembraneExtendAction(membrane, memTypes.get(i).getText());
			actions.add(mea);
		}

		for (ObjectsContext oc : ctx.objects()) {
			List<Object[]> objects = (List<Object[]>) visitObjects(oc);
			for (Object[] obj : objects)
				currMembrane.addObject((Obj) obj[0], (Integer) obj[1]);
		}
		for (PruleContext pc : ctx.prule()) {
			currMembrane.addRule((Rule) visitPrule(pc));
		}

		for (SubmembraneContext sc : ctx.submembrane()) {
			PTunnel.addChildParentTunnel(currMembrane, (Membrane) visitSubmembrane(sc));
		}

		for (RuleSetDeclareContext rsdc : ctx.ruleSetDeclare())
			visitRuleSetDeclare(rsdc);// may be this rule set has not been recognized, so need actions to do this job

		for (PropertiesContext pc : ctx.properties()) {
			List<Object[]> properties = (List<Object[]>) visitProperties(pc);
			for (Object[] property : properties)
				currMembrane.setProperty((String) property[0], property[1]);
		}

		Membrane.registMemClass(memType, membrane, false);
		return null;
	}

	@Override
	public T visitMembraneType(UPLanguageParser.MembraneTypeContext ctx) {
		return visitChildren(ctx);// 上级以及处理了
	}

	@Override
	public T visitProperties(UPLanguageParser.PropertiesContext ctx) {
		LinkedList<Object[]> properties = new LinkedList<>();
		for (PropertyInitContext pic : ctx.propertyInit())
			properties.add((Object[]) visitPropertyInit(pic));
		return (T) properties;
	}

	@Override
	public T visitPropertyInit(UPLanguageParser.PropertyInitContext ctx) {
		Object[] property = new Object[2];
		property[0] = ctx.propertyName().getText();
		property[1] = ctx.propertyValue().getText();
		return (T) property;
	}

	@Override
	public T visitPropertyName(UPLanguageParser.PropertyNameContext ctx) {
		return null;// 上级处理了
	}

	@Override
	public T visitPropertyValue(UPLanguageParser.PropertyValueContext ctx) {
		return null;// 上级处理了
	}

	@Override
	public T visitRuleSetDeclare(UPLanguageParser.RuleSetDeclareContext ctx) {
		logger.info("visitRuleSetDeclare");
		RuleSetDeclareAction rsdAction = new RuleSetDeclareAction(ctx.ruleSetType().getText(), ctx.ruleSetNamePrefix().getText(), currMembrane);
		actions.add(rsdAction);
		return null;
	}

	@Override
	public T visitRuleSetNamePrefix(UPLanguageParser.RuleSetNamePrefixContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitSubmembrane(UPLanguageParser.SubmembraneContext ctx) {
		logger.info("visitSubmembrane " + ctx.getText());

		Membrane membrane = null;
		if (ctx.membraneType() != null) {
			if (Membrane.isPredefinedMem(ctx.membraneType().getText())) {
				membrane = Membrane.getMemInstanceOf(ctx.membraneType().getText());
			} else {
				membrane = new PMembrane();
				MembraneExtendAction meAction = new MembraneExtendAction(membrane, ctx.membraneType().getText());
				actions.add(meAction);
			}
		} else
			membrane = new PMembrane();

		Membrane father = currMembrane;
		currMembrane = membrane;

		membrane.setName(ctx.membraneName().getText());
		for (IntDimContext iContext : ctx.intDim())
			membrane.addDimension(Long.parseLong(iContext.getText()));

		for (ObjectsContext oc : ctx.objects()) {
			List<Object[]> objects = (List<Object[]>) visitObjects(oc);
			for (Object[] obj : objects)
				currMembrane.addObject((Obj) obj[0], (Integer) obj[1]);
		}
		for (PruleContext pc : ctx.prule()) {
			currMembrane.addRule((Rule) visitPrule(pc));
		}

		for (SubmembraneContext sc : ctx.submembrane()) {
			PTunnel.addChildParentTunnel(currMembrane, (Membrane) visitSubmembrane(sc));
		}

		for (RuleSetDeclareContext rsdc : ctx.ruleSetDeclare())
			visitRuleSetDeclare(rsdc);// may be this rule set has not been recognized, so need actions to do this job

		for (PropertiesContext pc : ctx.properties()) {
			List<Object[]> properties = (List<Object[]>) visitProperties(pc);
			for (Object[] property : properties)
				currMembrane.setProperty((String) property[0], property[1]);
		}

		for (TunnelsContext tc : ctx.tunnels()) {
			List<String> tunnels = (List<String>) visitTunnels(tc);
			for (String tunnel : tunnels) {
				TunnelCreateAction action = new TunnelCreateAction(currMembrane, tunnel);
				actions.add(action);
			}
		}

		currMembrane = father;
		return (T) membrane;
	}

	@Override
	public T visitMembraneName(UPLanguageParser.MembraneNameContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitObjects(UPLanguageParser.ObjectsContext ctx) {
		LinkedList<Object[]> objects = new LinkedList<>();
		for (ObjAssignContext oac : ctx.objAssign())
			objects.add((Object[]) visitObjAssign(oac));
		return (T) objects;// 不需要处理，下级会处理
	}

	@Override
	public T visitObjAssign(UPLanguageParser.ObjAssignContext ctx) {
		PObject pobject = new PObject();
		pobject.setName(ctx.objName().getText());
		for (UPLanguageParser.IntDimContext idc : ctx.intDim())
			pobject.addDimension(Long.parseLong(idc.getText()));

		Object object[] = new Object[2];
		object[0] = pobject;
		object[1] = (int) visitObjNum(ctx.objNum());
		return (T) object;
	}

	@Override
	public T visitObjName(UPLanguageParser.ObjNameContext ctx) {
		return null;
	}

	@Override
	public T visitObjNum(UPLanguageParser.ObjNumContext ctx) {
		Integer objNum = 1;
		if (ctx != null)
			objNum = Integer.parseInt(ctx.getText());
		return (T) objNum;
	}

	private Rule currRule;

	@Override
	public T visitPrule(UPLanguageParser.PruleContext ctx) {
		logger.info("visitRuleDef:" + ctx.getText());
		PRule rule = new PRule();
		currRule = rule;
		// 获取名称
		rule.setName(ctx.ruleName().getText());
		// 获取维度
		for (UPLanguageParser.AbcDimContext dim : ctx.abcDim())
			rule.addDimension((String) visitAbcDim(dim));
		// 获取objCondtion
		for (UPLanguageParser.ObjCondtionContext occ : ctx.objCondtion())
			rule.addCondition((Condition) visitObjCondtion(occ));
		// 获取result
		for (UPLanguageParser.ResultContext rc : ctx.result()) {
			Result result = (Result) visitResult(rc);
			rule.addResult(result);
		}
		// 获取condition
		for (UPLanguageParser.ConditionContext cc : ctx.condition()) {
			Condition condition = (Condition) visitCondition(cc);
			rule.addCondition(condition);
		}
		if (ctx.regCondition() != null) {
			rule.addCondition((Condition) visitRegCondition(ctx.regCondition()));
		}

		if (ctx.priorityCondition() != null) {
			PriorityCondition priorityCondition = (PriorityCondition) visitPriorityCondition(ctx.priorityCondition());
			rule.addCondition(priorityCondition);
		}

		for (PropertyConditionContext mpctx : ctx.propertyCondition()) {
			MembranePropertyCondition membranePropertyCondition = (MembranePropertyCondition) visitPropertyCondition(mpctx);
			rule.addCondition(membranePropertyCondition);
		}

		for (PropertyResultContext mprctx : ctx.propertyResult()) {
			MembranePropertyResult mPropertyResult = (MembranePropertyResult) visitPropertyResult(mprctx);
			rule.addResult(mPropertyResult);
		}

		currRule = null;
		return (T) rule;
	}

	@Override
	public T visitRuleName(UPLanguageParser.RuleNameContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitCondition(UPLanguageParser.ConditionContext ctx) {
		return visitChildren(ctx);// 不需要做任何事情
	}

	@Override
	public T visitResult(UPLanguageParser.ResultContext ctx) {
		return visitChildren(ctx);// 不需要做任何事情
	}

	@Override
	public T visitMemCreateResult(UPLanguageParser.MemCreateResultContext ctx) {
		MembraneCreateResult mcr = new MembraneCreateResult();

		List<Object> list1 = (List<Object>) visitAdditionalResults(ctx.additionalResults());
		LinkedList<ObjectResult> orList1 = (LinkedList<ObjectResult>) list1.get(0);
		LinkedList<MembranePropertyResult> prList1 = (LinkedList<MembranePropertyResult>) list1.get(1);
		for (ObjectResult or : orList1)
			mcr.addResult(or);
		for (MembranePropertyResult pr : prList1)
			mcr.addResult(pr);

		mcr.setTemplateMemName(ctx.membraneType().getText());
		if (ctx.membraneName() == null) {
			mcr.setName(ctx.membraneType().getText());
		} else {
			mcr.setName(ctx.membraneName().getText());
		}

		for (UPLanguageParser.FormulaDimContext fdc : ctx.formulaDim()) {
			mcr.addDimension((String) visitFormulaDim(fdc));
		}

		return (T) mcr;
	}

	@Override
	public T visitMemDissolveResult(MemDissolveResultContext ctx) {
		MembraneDissolveResult mdr = new MembraneDissolveResult();
		return (T) mdr;
	}

	@Override
	public T visitPropertyResult(UPLanguageParser.PropertyResultContext ctx) {
		String pn;
		String pv = ctx.propertyValue().getText();
		if (ctx.propertyName() == null)
			pn = "Status";
		else
			pn = ctx.propertyName().getText();

		MembranePropertyResult mpr;

		if (pn.equalsIgnoreCase("Status")) {
			mpr = new MembraneStatusResult(pv);
		} else {
			mpr = new MembranePropertyResult(pn, pv);
		}

		return (T) mpr;
	}

	@Override
	public T visitObjResult(UPLanguageParser.ObjResultContext ctx) {
		ObjectResult or = new ObjectResult();
		or.setName(ctx.objName().getText());
		for (UPLanguageParser.FormulaDimContext fdc : ctx.formulaDim())
			or.addDimension((String) visitFormulaDim(fdc));
		or.setNum((int) visitObjNum(ctx.objNum()));
		return (T) or;
	}

	private PositionResult currPositionResult;

	@Override
	public T visitPositionResult(UPLanguageParser.PositionResultContext ctx) {
		PositionResult pResult = new PositionResult();
		currPositionResult = pResult;
		for (ObjResultContext orc : ctx.objResult())
			pResult.addObjectResult((ObjectResult) visitObjResult(orc));

		for (TargetContext tc : ctx.target())
			pResult.addTarget((Target) visitTarget(tc));

		if (ctx.here() != null) {
			pResult.setMove(TunnelType.Here);
		} else if (ctx.out() != null) {
			pResult.setMove(TunnelType.Out);
		} else if (ctx.in() != null) {
			if (ctx.all() != null) {
				pResult.setMove(TunnelType.In_all);
			} else if (ctx.random() != null) {
				pResult.setMove(TunnelType.In_one_of_all);
			} else if (ctx.andOpt().size() > 0) {
				pResult.setMove(TunnelType.In_all_of_specified);
			} else if (ctx.orOpt().size() > 0) {
				pResult.setMove(TunnelType.In_one_of_specified);
			} else {
				pResult.setMove(TunnelType.In);
			}
		} else {
			if (ctx.all() != null) {
				pResult.setMove(TunnelType.Go_all);
			} else if (ctx.random() != null) {
				pResult.setMove(TunnelType.Go_one_of_all);
			} else if (ctx.andOpt().size() > 0) {
				pResult.setMove(TunnelType.Go_all_of_specified);
			} else if (ctx.orOpt().size() > 0) {
				pResult.setMove(TunnelType.Go_one_of_specified);
			} else {
				pResult.setMove(TunnelType.Go);
			}
		}

		currPositionResult = null;

		return (T) pResult;
	}

	@Override
	public T visitBoolCondition(UPLanguageParser.BoolConditionContext ctx) {
		BooleanCondition bCondition = new BooleanCondition();
		bCondition.addDimension(ctx.getText());

		return (T) bCondition;
	}

	@Override
	public T visitPromoterCondition(UPLanguageParser.PromoterConditionContext ctx) {
		PromoterCondition pCondition = new PromoterCondition();
		pCondition.setName(ctx.objName().getText());
		for (UPLanguageParser.FormulaDimContext fdc : ctx.formulaDim())
			pCondition.addDimension((String) visitFormulaDim(fdc));
		return (T) pCondition;
	}

	@Override
	public T visitInhibitorCondition(UPLanguageParser.InhibitorConditionContext ctx) {
		InhibitorCondition iCondition = new InhibitorCondition();
		iCondition.setName(ctx.objName().getText());
		for (UPLanguageParser.FormulaDimContext fdc : ctx.formulaDim())
			iCondition.addDimension((String) visitFormulaDim(fdc));
		return (T) iCondition;
	}

	@Override
	public T visitPropertyCondition(UPLanguageParser.PropertyConditionContext ctx) {
		String pn;
		String pv = ctx.propertyValue().getText();
		if (ctx.propertyName() == null)
			pn = "Status";
		else
			pn = ctx.propertyName().getText();

		MembranePropertyCondition mpCondition;
		if (pn.equalsIgnoreCase("Status")) {
			mpCondition = new MembraneStatusCondition(pv);
		} else {
			mpCondition = new MembranePropertyCondition(pn, pv);
		}

		return (T) mpCondition;
	}

	@Override
	public T visitObjCondtion(UPLanguageParser.ObjCondtionContext ctx) {
		ObjectCondition objectCondition = new ObjectCondition();
		objectCondition.setName(ctx.objName().getText());
		for (UPLanguageParser.FormulaDimContext fdc : ctx.formulaDim())
			objectCondition.addDimension((String) visitFormulaDim(fdc));
		objectCondition.setNum((int) visitObjNum(ctx.objNum()));
		return (T) objectCondition;
	}

	@Override
	public T visitPriorityCondition(UPLanguageParser.PriorityConditionContext ctx) {
		PriorityCondition pCondition = new PriorityCondition(Integer.parseInt(ctx.priority().getText()));

		return (T) pCondition;
	}

	@Override
	public T visitPriority(UPLanguageParser.PriorityContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitIntDim(UPLanguageParser.IntDimContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitAbcDim(UPLanguageParser.AbcDimContext ctx) {
		return (T) ctx.getText();
	}

	@Override
	public T visitFormulaDim(UPLanguageParser.FormulaDimContext ctx) {
		return (T) ctx.getText();
	}

	@Override
	public T visitTunnels(TunnelsContext ctx) {
		logger.info("visitTunnels " + ctx.getText());

		LinkedList<String> tunnels = new LinkedList<>();
		for (TunnelTargetContext ttc : ctx.tunnelTarget())
			tunnels.add((String) visitTunnelTarget(ttc));
		return (T) tunnels;
	}

	@Override
	public T visitTunnelTarget(TunnelTargetContext ctx) {
		String mName = ctx.membraneName().getText();
		for (IntDimContext intDimContext : ctx.intDim()) {
			mName += "[" + intDimContext.toString() + "]";
		}
		return (T) mName;
	}

	@Override
	public T visitTarget(TargetContext ctx) {
		String tString = ctx.getText();
		logger.info("visitTarget:" + tString);

		Target target = currPositionResult.new Target();
		target.setName(ctx.membraneName().getText());

		boolean needCheck = true;
		for (FormulaDimContext fdc : ctx.formulaDim()) {
			String dim = (String) visitFormulaDim(fdc);
			target.addDimension(dim);
			try {
				Integer.parseInt(dim);
			} catch (Exception e) {
				needCheck = false;
			}
		}

		if (needCheck)
			actions.add(new RuleTunnelChecker(target.getNameDim(), currRule, currMembrane));

		for (PropertyConditionContext pcc : ctx.propertyCondition())
			target.conditions.add((MembranePropertyCondition) visitPropertyCondition(pcc));

		return (T) target;
	}

	@Override
	public T visitIn(InContext ctx) {
		return null;
	}

	@Override
	public T visitOut(OutContext ctx) {
		return null;
	}

	@Override
	public T visitHere(HereContext ctx) {
		return null;
	}

	@Override
	public T visitGo(GoContext ctx) {
		return null;
	}

	@Override
	public T visitAndOpt(AndOptContext ctx) {
		return null;
	}

	@Override
	public T visitOrOpt(OrOptContext ctx) {
		return null;
	}

	@Override
	public T visitAll(AllContext ctx) {
		return null;
	}

	@Override
	public T visitRandom(RandomContext ctx) {
		return null;
	}

	@Override
	public T visitRegCondition(RegConditionContext ctx) {
		return (T) new RegularExpressionCondition(ctx.getText());
	}

	@Override
	public T visitMemDivisionResult(MemDivisionResultContext ctx) {
		MembraneDivisionResult mdr = new MembraneDivisionResult();
		List<Object> list1 = (List<Object>) visitAdditionalResults(ctx.additionalResults(0));
		LinkedList<ObjectResult> orList1 = (LinkedList<ObjectResult>) list1.get(0);
		LinkedList<MembranePropertyResult> prList1 = (LinkedList<MembranePropertyResult>) list1.get(1);
		for (ObjectResult or : orList1)
			mdr.addObjectResult1(or);
		for (MembranePropertyResult pr : prList1)
			mdr.addMembranePropertyResult1(pr);

		List<Object> list2 = (List<Object>) visitAdditionalResults(ctx.additionalResults(1));
		LinkedList<ObjectResult> orList2 = (LinkedList<ObjectResult>) list2.get(0);
		LinkedList<MembranePropertyResult> prList2 = (LinkedList<MembranePropertyResult>) list1.get(1);
		for (ObjectResult or : orList2)
			mdr.addObjectResult2(or);
		for (MembranePropertyResult pr : prList2)
			mdr.addMembranePropertyResult2(pr);

		return (T) mdr;
	}

	@Override
	public T visitAdditionalResults(AdditionalResultsContext ctx) {
		LinkedList<Object> list = new LinkedList<>();
		LinkedList<ObjectResult> oList = new LinkedList<>();
		for (ObjResultContext orc : ctx.objResult()) {
			oList.add((ObjectResult) visitObjResult(orc));
		}
		LinkedList<MembranePropertyResult> membranePropertyResults = new LinkedList<>();
		for (PropertyResultContext prc : ctx.propertyResult())
			membranePropertyResults.add((MembranePropertyResult) visitPropertyResult(prc));
		list.add(oList);
		list.add(membranePropertyResults);
		return (T) list;
	}

	@Override
	public T visitProbabilisticCondition(ProbabilisticConditionContext ctx) {
		ProbabilisticCondition pCondition = new ProbabilisticCondition(Double.parseDouble(ctx.Double().getText()));
		return (T) pCondition;
	}

}