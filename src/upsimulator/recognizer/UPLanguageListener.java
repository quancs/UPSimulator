package upsimulator.recognizer;// Generated from UPLanguage.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UPLanguageParser}.
 */
public interface UPLanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(UPLanguageParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(UPLanguageParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#environmentDef}.
	 * @param ctx the parse tree
	 */
	void enterEnvironmentDef(UPLanguageParser.EnvironmentDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#environmentDef}.
	 * @param ctx the parse tree
	 */
	void exitEnvironmentDef(UPLanguageParser.EnvironmentDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#ruleSetDef}.
	 * @param ctx the parse tree
	 */
	void enterRuleSetDef(UPLanguageParser.RuleSetDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#ruleSetDef}.
	 * @param ctx the parse tree
	 */
	void exitRuleSetDef(UPLanguageParser.RuleSetDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#ruleSetType}.
	 * @param ctx the parse tree
	 */
	void enterRuleSetType(UPLanguageParser.RuleSetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#ruleSetType}.
	 * @param ctx the parse tree
	 */
	void exitRuleSetType(UPLanguageParser.RuleSetTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#membraneDef}.
	 * @param ctx the parse tree
	 */
	void enterMembraneDef(UPLanguageParser.MembraneDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#membraneDef}.
	 * @param ctx the parse tree
	 */
	void exitMembraneDef(UPLanguageParser.MembraneDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#membraneType}.
	 * @param ctx the parse tree
	 */
	void enterMembraneType(UPLanguageParser.MembraneTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#membraneType}.
	 * @param ctx the parse tree
	 */
	void exitMembraneType(UPLanguageParser.MembraneTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#membraneContent}.
	 * @param ctx the parse tree
	 */
	void enterMembraneContent(UPLanguageParser.MembraneContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#membraneContent}.
	 * @param ctx the parse tree
	 */
	void exitMembraneContent(UPLanguageParser.MembraneContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#memProperty}.
	 * @param ctx the parse tree
	 */
	void enterMemProperty(UPLanguageParser.MemPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#memProperty}.
	 * @param ctx the parse tree
	 */
	void exitMemProperty(UPLanguageParser.MemPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#propertyInit}.
	 * @param ctx the parse tree
	 */
	void enterPropertyInit(UPLanguageParser.PropertyInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#propertyInit}.
	 * @param ctx the parse tree
	 */
	void exitPropertyInit(UPLanguageParser.PropertyInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void enterPropertyName(UPLanguageParser.PropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void exitPropertyName(UPLanguageParser.PropertyNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#propertyValue}.
	 * @param ctx the parse tree
	 */
	void enterPropertyValue(UPLanguageParser.PropertyValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#propertyValue}.
	 * @param ctx the parse tree
	 */
	void exitPropertyValue(UPLanguageParser.PropertyValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#ruleSetDeclare}.
	 * @param ctx the parse tree
	 */
	void enterRuleSetDeclare(UPLanguageParser.RuleSetDeclareContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#ruleSetDeclare}.
	 * @param ctx the parse tree
	 */
	void exitRuleSetDeclare(UPLanguageParser.RuleSetDeclareContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#ruleSetNamePrefix}.
	 * @param ctx the parse tree
	 */
	void enterRuleSetNamePrefix(UPLanguageParser.RuleSetNamePrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#ruleSetNamePrefix}.
	 * @param ctx the parse tree
	 */
	void exitRuleSetNamePrefix(UPLanguageParser.RuleSetNamePrefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#memDeclare}.
	 * @param ctx the parse tree
	 */
	void enterMemDeclare(UPLanguageParser.MemDeclareContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#memDeclare}.
	 * @param ctx the parse tree
	 */
	void exitMemDeclare(UPLanguageParser.MemDeclareContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#membraneName}.
	 * @param ctx the parse tree
	 */
	void enterMembraneName(UPLanguageParser.MembraneNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#membraneName}.
	 * @param ctx the parse tree
	 */
	void exitMembraneName(UPLanguageParser.MembraneNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#objDef}.
	 * @param ctx the parse tree
	 */
	void enterObjDef(UPLanguageParser.ObjDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#objDef}.
	 * @param ctx the parse tree
	 */
	void exitObjDef(UPLanguageParser.ObjDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#objAssign}.
	 * @param ctx the parse tree
	 */
	void enterObjAssign(UPLanguageParser.ObjAssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#objAssign}.
	 * @param ctx the parse tree
	 */
	void exitObjAssign(UPLanguageParser.ObjAssignContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#objName}.
	 * @param ctx the parse tree
	 */
	void enterObjName(UPLanguageParser.ObjNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#objName}.
	 * @param ctx the parse tree
	 */
	void exitObjName(UPLanguageParser.ObjNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#objNum}.
	 * @param ctx the parse tree
	 */
	void enterObjNum(UPLanguageParser.ObjNumContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#objNum}.
	 * @param ctx the parse tree
	 */
	void exitObjNum(UPLanguageParser.ObjNumContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#ruleDef}.
	 * @param ctx the parse tree
	 */
	void enterRuleDef(UPLanguageParser.RuleDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#ruleDef}.
	 * @param ctx the parse tree
	 */
	void exitRuleDef(UPLanguageParser.RuleDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#ruleName}.
	 * @param ctx the parse tree
	 */
	void enterRuleName(UPLanguageParser.RuleNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#ruleName}.
	 * @param ctx the parse tree
	 */
	void exitRuleName(UPLanguageParser.RuleNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(UPLanguageParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(UPLanguageParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#result}.
	 * @param ctx the parse tree
	 */
	void enterResult(UPLanguageParser.ResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#result}.
	 * @param ctx the parse tree
	 */
	void exitResult(UPLanguageParser.ResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#memCreateResult}.
	 * @param ctx the parse tree
	 */
	void enterMemCreateResult(UPLanguageParser.MemCreateResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#memCreateResult}.
	 * @param ctx the parse tree
	 */
	void exitMemCreateResult(UPLanguageParser.MemCreateResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#memDissolveResult}.
	 * @param ctx the parse tree
	 */
	void enterMemDissolveResult(UPLanguageParser.MemDissolveResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#memDissolveResult}.
	 * @param ctx the parse tree
	 */
	void exitMemDissolveResult(UPLanguageParser.MemDissolveResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#memPropertyResult}.
	 * @param ctx the parse tree
	 */
	void enterMemPropertyResult(UPLanguageParser.MemPropertyResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#memPropertyResult}.
	 * @param ctx the parse tree
	 */
	void exitMemPropertyResult(UPLanguageParser.MemPropertyResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#objResult}.
	 * @param ctx the parse tree
	 */
	void enterObjResult(UPLanguageParser.ObjResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#objResult}.
	 * @param ctx the parse tree
	 */
	void exitObjResult(UPLanguageParser.ObjResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#positionResult}.
	 * @param ctx the parse tree
	 */
	void enterPositionResult(UPLanguageParser.PositionResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#positionResult}.
	 * @param ctx the parse tree
	 */
	void exitPositionResult(UPLanguageParser.PositionResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#boolCondition}.
	 * @param ctx the parse tree
	 */
	void enterBoolCondition(UPLanguageParser.BoolConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#boolCondition}.
	 * @param ctx the parse tree
	 */
	void exitBoolCondition(UPLanguageParser.BoolConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#promoterCondition}.
	 * @param ctx the parse tree
	 */
	void enterPromoterCondition(UPLanguageParser.PromoterConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#promoterCondition}.
	 * @param ctx the parse tree
	 */
	void exitPromoterCondition(UPLanguageParser.PromoterConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#inhibitorCondition}.
	 * @param ctx the parse tree
	 */
	void enterInhibitorCondition(UPLanguageParser.InhibitorConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#inhibitorCondition}.
	 * @param ctx the parse tree
	 */
	void exitInhibitorCondition(UPLanguageParser.InhibitorConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#memPropertyCondition}.
	 * @param ctx the parse tree
	 */
	void enterMemPropertyCondition(UPLanguageParser.MemPropertyConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#memPropertyCondition}.
	 * @param ctx the parse tree
	 */
	void exitMemPropertyCondition(UPLanguageParser.MemPropertyConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#objCondtion}.
	 * @param ctx the parse tree
	 */
	void enterObjCondtion(UPLanguageParser.ObjCondtionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#objCondtion}.
	 * @param ctx the parse tree
	 */
	void exitObjCondtion(UPLanguageParser.ObjCondtionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#priorityCondition}.
	 * @param ctx the parse tree
	 */
	void enterPriorityCondition(UPLanguageParser.PriorityConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#priorityCondition}.
	 * @param ctx the parse tree
	 */
	void exitPriorityCondition(UPLanguageParser.PriorityConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#priority}.
	 * @param ctx the parse tree
	 */
	void enterPriority(UPLanguageParser.PriorityContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#priority}.
	 * @param ctx the parse tree
	 */
	void exitPriority(UPLanguageParser.PriorityContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#intDim}.
	 * @param ctx the parse tree
	 */
	void enterIntDim(UPLanguageParser.IntDimContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#intDim}.
	 * @param ctx the parse tree
	 */
	void exitIntDim(UPLanguageParser.IntDimContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#abcDim}.
	 * @param ctx the parse tree
	 */
	void enterAbcDim(UPLanguageParser.AbcDimContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#abcDim}.
	 * @param ctx the parse tree
	 */
	void exitAbcDim(UPLanguageParser.AbcDimContext ctx);
	/**
	 * Enter a parse tree produced by {@link UPLanguageParser#formulaDim}.
	 * @param ctx the parse tree
	 */
	void enterFormulaDim(UPLanguageParser.FormulaDimContext ctx);
	/**
	 * Exit a parse tree produced by {@link UPLanguageParser#formulaDim}.
	 * @param ctx the parse tree
	 */
	void exitFormulaDim(UPLanguageParser.FormulaDimContext ctx);
}