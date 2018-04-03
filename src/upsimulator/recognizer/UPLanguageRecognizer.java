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
import upsimulator.interfaces.Result;
import upsimulator.interfaces.Rule;
import upsimulator.recognizer.UPLanguageParser.MemDissolveResultContext;
import upsimulator.recognizer.UPLanguageParser.MembraneTypeContext;
import upsimulator.recognizer.actions.MembraneExtendAction;
import upsimulator.recognizer.actions.RecognizerAction;
import upsimulator.recognizer.actions.RuleSetDeclareAction;
import upsimulator.rules.conditions.BooleanCondition;
import upsimulator.rules.conditions.InhibitorCondition;
import upsimulator.rules.conditions.MembranePropertyCondition;
import upsimulator.rules.conditions.ObjectCondition;
import upsimulator.rules.conditions.PriorityCondition;
import upsimulator.rules.conditions.PromoterCondition;
import upsimulator.rules.results.MembraneCreateResult;
import upsimulator.rules.results.MembraneDissolveResult;
import upsimulator.rules.results.MembranePropertyResult;
import upsimulator.rules.results.ObjectResult;
import upsimulator.rules.results.PositionResult;

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

		for (; actions.size() > 0;) {
			RecognizerAction action = actions.get(0);
			if (action.ready()) {
				action.doAction();
				actions.remove(0);
			} else {
				actions.add(actions.remove(0));
			}
		}
		return (T) skin;
	}

	@Override
	public T visitEnvironmentDef(UPLanguageParser.EnvironmentDefContext ctx) {
		logger.info("visitEnvironmentDef");
		skin = new PMembrane();
		skin.setName("Environment");
		currMembrane = skin;
		visitChildren(ctx);
		currMembrane = null;
		return null;
	}

	@Override
	public T visitRuleSetDef(UPLanguageParser.RuleSetDefContext ctx) {
		logger.info("visitRuleSetDef");
		String ruleSetType = ctx.ruleSetType().getText();
		ArrayList<Rule> ruleSet = new ArrayList<>();
		for (UPLanguageParser.RuleDefContext rdc : ctx.ruleDef()) {
			Rule rule = (Rule) visitRuleDef(rdc);
			ruleSet.add(rule);
		}
		Rule.addRuleSet(ruleSetType, ruleSet);
		logger.debug("ruleset : " + ruleSetType + " " + ruleSet);
		return null;
	}

	@Override
	public T visitRuleSetType(UPLanguageParser.RuleSetTypeContext ctx) {
		return null;// 上级已经做了
	}

	@Override
	public T visitMembraneDef(UPLanguageParser.MembraneDefContext ctx) {
		PMembrane membrane = new PMembrane();
		currMembrane = membrane;

		List<MembraneTypeContext> memTypes = ctx.membraneType();

		String memType = memTypes.get(0).getText();
		for (int i = 1; i < memTypes.size(); i++) {
			MembraneExtendAction mea = new MembraneExtendAction(membrane, memTypes.get(i).getText());
			actions.add(mea);
		}
		for (UPLanguageParser.MembraneContentContext mcc : ctx.membraneContent()) {
			visitMembraneContent(mcc);
		}
		Membrane.registMemClass(memType, membrane);
		return null;
	}

	@Override
	public T visitMembraneType(UPLanguageParser.MembraneTypeContext ctx) {
		return visitChildren(ctx);// 上级以及处理了
	}

	@Override
	public T visitMembraneContent(UPLanguageParser.MembraneContentContext ctx) {
		Object object = visitChildren(ctx);
		if (object != null) {// objDef,memProperty,ruleSetDeclare return null
			if (object instanceof Rule) {
				currMembrane.addRule((Rule) object);
			} else if (object instanceof Membrane) {
				PTunnel.addChildParentTunnel(currMembrane, (Membrane) object);
			} else {
				throw new RuntimeException("未知的返回类型：" + object);
			}
		}

		return null;// 直接返回给调用的函数
	}

	@Override
	public T visitMemProperty(UPLanguageParser.MemPropertyContext ctx) {
		return visitChildren(ctx);// 不需要处理,PropertyInit处理
	}

	@Override
	public T visitPropertyInit(UPLanguageParser.PropertyInitContext ctx) {
		currMembrane.setProperty(ctx.propertyName().getText(), ctx.propertyValue().getText());
		return null;
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
		RuleSetDeclareAction rsdAction = new RuleSetDeclareAction(ctx.ruleSetType().getText(),
				ctx.ruleSetNamePrefix().getText(), currMembrane);
		actions.add(rsdAction);
		return null;
	}

	@Override
	public T visitRuleSetNamePrefix(UPLanguageParser.RuleSetNamePrefixContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitMemDeclare(UPLanguageParser.MemDeclareContext ctx) {
		logger.info("visitMemDeclare");
		PMembrane membrane = new PMembrane();
		Membrane father = currMembrane;
		currMembrane = membrane;

		membrane.setName(ctx.membraneName().getText());
		for (UPLanguageParser.MembraneContentContext mcc : ctx.membraneContent())
			visitMembraneContent(mcc);

		MembraneExtendAction meAction = new MembraneExtendAction(membrane, ctx.membraneType().getText());
		actions.add(meAction);

		currMembrane = father;
		return (T) membrane;
	}

	@Override
	public T visitMembraneName(UPLanguageParser.MembraneNameContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public T visitObjDef(UPLanguageParser.ObjDefContext ctx) {
		return visitChildren(ctx);// 不需要处理，下级会处理
	}

	@Override
	public T visitObjAssign(UPLanguageParser.ObjAssignContext ctx) {
		PObject object = new PObject();
		object.setName(ctx.objName().getText());
		for (UPLanguageParser.IntDimContext idc : ctx.intDim())
			object.addDimension(Integer.parseInt(idc.getText()));
		currMembrane.addObject(object, (int) visitObjNum(ctx.objNum()));
		return null;
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
	public T visitRuleDef(UPLanguageParser.RuleDefContext ctx) {
		logger.info("visitRuleDef");
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
			if (result instanceof Condition)
				rule.addCondition((Condition) result);
		}
		// 获取condition
		PriorityCondition priorityCondition = null;
		for (UPLanguageParser.ConditionContext cc : ctx.condition()) {
			Condition condition = (Condition) visitCondition(cc);
			if (condition instanceof PriorityCondition)
				priorityCondition = (PriorityCondition) condition;
			else
				rule.addCondition(condition);
		}
		if (priorityCondition != null) {
			rule.addCondition(priorityCondition);
			rule.setPriority(priorityCondition.getPriority());
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
		for (UPLanguageParser.ObjResultContext orc : ctx.objResult())
			mcr.addResult((Result) visitObjResult(orc));
		for (UPLanguageParser.MemPropertyResultContext mprc : ctx.memPropertyResult())
			mcr.addResult((Result) visitMemPropertyResult(mprc));

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
	public T visitMemPropertyResult(UPLanguageParser.MemPropertyResultContext ctx) {
		MembranePropertyResult mpr = new MembranePropertyResult();
		mpr.setProperty(ctx.propertyName().getText());
		mpr.setValue(ctx.propertyValue().getText());
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

	@Override
	public T visitPositionResult(UPLanguageParser.PositionResultContext ctx) {
		PositionResult pResult = new PositionResult();

		if (ctx.membraneName() != null) {
			pResult.setMove(PositionResult.in);
			pResult.setMembraneName(ctx.membraneName().getText());
			for (UPLanguageParser.FormulaDimContext fdc : ctx.formulaDim())
				pResult.addDimension((String) visitFormulaDim(fdc));
		} else
			switch (ctx.getChild(ctx.getChildCount() - 2).getText()) {
			case "out":
				pResult.setMove(PositionResult.out);
				break;
			case "here":
				pResult.setMove(PositionResult.here);
				break;
			default:
				throw new RuntimeException("unknown PositionResult move direction : "
						+ ctx.getChild(ctx.getChildCount() - 2).getText() + "   \n" + ctx.toStringTree());
			}

		for (UPLanguageParser.ObjResultContext orc : ctx.objResult())
			pResult.addObjectResult((ObjectResult) visitObjResult(orc));

		for (UPLanguageParser.MemPropertyConditionContext mpcc : ctx.memPropertyCondition())
			pResult.addMemPropCondition((MembranePropertyCondition) visitMemPropertyCondition(mpcc));

		return (T) pResult;
	}

	@Override
	public T visitBoolCondition(UPLanguageParser.BoolConditionContext ctx) {
		BooleanCondition bCondition = new BooleanCondition();
		bCondition.addDimension((String) getRuleFormulaString(ctx.getText(), currRule.getDimensions()));

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
	public T visitMemPropertyCondition(UPLanguageParser.MemPropertyConditionContext ctx) {
		MembranePropertyCondition mpCondition = new MembranePropertyCondition(ctx.propertyName().getText(),
				ctx.propertyValue().getText());

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
		return (T) getRuleFormulaString(ctx.getText(), currRule.getDimensions());
	}

	/**
	 * 在规则的表达式维度（算术的或者逻辑的）中加入格式#{ }
	 * 
	 * @param text
	 * @param dims
	 * @return
	 */
	private String getRuleFormulaString(String text, List<String> dims) {
		text = " " + text + " ";
		// 对象的维度中 规则维度 的前后都不是 字母和数字 则说明 该维度就是规则的维度，将之改成 #{ * };
		for (String dim : dims) {
			for (int i = 1; i < text.length() - 1;) {
				String subText = text.substring(i);
				if (subText.startsWith(dim)) {
					String front = text.substring(0, i);
					String back = text.substring(i + dim.length());

					Character flast = front.charAt(front.length() - 1);
					Character bfirst = back.charAt(0);

					boolean frontNotLetterNum = !Character.isLetterOrDigit(flast);
					boolean backNotLetterNum = !Character.isLetterOrDigit(bfirst);

					if (backNotLetterNum && frontNotLetterNum) {
						text = front + "#{" + dim + "}" + back;
						i = front.length() + 3 + dim.length();
					} else
						i++;
				} else
					i++;
			}
		}

		return text.trim();
	}

}