package upsimulator.recognizer;

// Generated from UPLanguage.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UPLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UPLanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(UPLanguageParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#environmentDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnvironmentDef(UPLanguageParser.EnvironmentDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#ruleSetDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRuleSetDef(UPLanguageParser.RuleSetDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#ruleSetType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRuleSetType(UPLanguageParser.RuleSetTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#membraneDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMembraneDef(UPLanguageParser.MembraneDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#membraneType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMembraneType(UPLanguageParser.MembraneTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#properties}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperties(UPLanguageParser.PropertiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#propertyInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyInit(UPLanguageParser.PropertyInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#propertyName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyName(UPLanguageParser.PropertyNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#propertyValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyValue(UPLanguageParser.PropertyValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#tunnels}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTunnels(UPLanguageParser.TunnelsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#tunnelTarget}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTunnelTarget(UPLanguageParser.TunnelTargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#ruleSetDeclare}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRuleSetDeclare(UPLanguageParser.RuleSetDeclareContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#ruleSetNamePrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRuleSetNamePrefix(UPLanguageParser.RuleSetNamePrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#submembrane}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubmembrane(UPLanguageParser.SubmembraneContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#membraneName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMembraneName(UPLanguageParser.MembraneNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#objects}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjects(UPLanguageParser.ObjectsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#objAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjAssign(UPLanguageParser.ObjAssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#objName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjName(UPLanguageParser.ObjNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#objNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjNum(UPLanguageParser.ObjNumContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#prule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrule(UPLanguageParser.PruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#ruleName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRuleName(UPLanguageParser.RuleNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(UPLanguageParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#result}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResult(UPLanguageParser.ResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#memDivisionResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemDivisionResult(UPLanguageParser.MemDivisionResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#additionalResults}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditionalResults(UPLanguageParser.AdditionalResultsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#memCreateResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemCreateResult(UPLanguageParser.MemCreateResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#memDissolveResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemDissolveResult(UPLanguageParser.MemDissolveResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#propertyResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyResult(UPLanguageParser.PropertyResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#objResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjResult(UPLanguageParser.ObjResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#positionResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositionResult(UPLanguageParser.PositionResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#in}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIn(UPLanguageParser.InContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#out}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOut(UPLanguageParser.OutContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#here}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHere(UPLanguageParser.HereContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#go}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGo(UPLanguageParser.GoContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#andOpt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndOpt(UPLanguageParser.AndOptContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#orOpt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrOpt(UPLanguageParser.OrOptContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#all}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAll(UPLanguageParser.AllContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#random}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRandom(UPLanguageParser.RandomContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget(UPLanguageParser.TargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#boolCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolCondition(UPLanguageParser.BoolConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#promoterCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPromoterCondition(UPLanguageParser.PromoterConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#inhibitorCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInhibitorCondition(UPLanguageParser.InhibitorConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#propertyCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyCondition(UPLanguageParser.PropertyConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#objCondtion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjCondtion(UPLanguageParser.ObjCondtionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#priorityCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriorityCondition(UPLanguageParser.PriorityConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#priority}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriority(UPLanguageParser.PriorityContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#regCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegCondition(UPLanguageParser.RegConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#intDim}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntDim(UPLanguageParser.IntDimContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#abcDim}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbcDim(UPLanguageParser.AbcDimContext ctx);
	/**
	 * Visit a parse tree produced by {@link UPLanguageParser#formulaDim}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormulaDim(UPLanguageParser.FormulaDimContext ctx);
}