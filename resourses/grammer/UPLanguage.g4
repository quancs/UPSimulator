/** This is the grammer of UPLanguage
 * @author quancs from CQU, Computer Science
 * @date 2017/05/20
 */
grammar UPLanguage;

/*结构相关部分 */
start  			: 	( membraneDef | ruleSetDef )* environmentDef ( membraneDef | ruleSetDef )*;
environmentDef  : 	'Environment' '{'  membraneContent*  '}';
ruleSetDef		:	'RuleSet' ruleSetType '{' ruleDef* '}';
ruleSetType		: 	Name | Letters;
membraneDef 	: 	'Membrane' membraneType ('extends' membraneType (',' membraneType)* )? '{'  membraneContent*  '}';
membraneType 	: 	Name | Letters;
membraneContent	: 	objDef | ruleDef | memDeclare | ruleSetDeclare | memProperty;

/*膜属性 */
memProperty		:	'Property' propertyInit (',' propertyInit)* ';';
propertyInit	:	propertyName '=' propertyValue;
propertyName	:	Name | Letters;
propertyValue	:	Letters | Integer | '+' | '-';

/*规则集声明*/
ruleSetDeclare	: 	'RuleSet' ruleSetType ruleSetNamePrefix? ';';
ruleSetNamePrefix:  Name | Letters;

/*膜定义*/
memDeclare		: 	'Membrane' membraneType membraneName ('[' intDim ']')*  ( '{' membraneContent* '}' | ';');
membraneName	: 	Name | Letters | Integer;

/*对象定义与赋值*/
objDef 		:	'Object' objAssign (','  objAssign )* ';';
objAssign	:	objName ('[' intDim ']')* ('^' objNum)?;
objName		:	Name | Letters;
objNum		:	Integer;

/*规则定义*/
ruleDef			:	'Rule' ruleName ('[' abcDim ']')* '=' objCondtion+ '->' result*
					('|' condition ('&' condition)* )? ';';
ruleName		:	Name | Letters;
condition		:	promoterCondition | inhibitorCondition | priorityCondition  | boolCondition | memPropertyCondition ;
result			:	objResult | positionResult | memPropertyResult | memDissolveResult | memCreateResult;
memCreateResult	: 	'{' (objResult | memPropertyResult)* '}'membraneType (':' membraneName ('[' formulaDim ']')*)? ;
memDissolveResult:	'm.dissolve';
memPropertyResult:	'm.' propertyName '=' propertyValue;
objResult		:	objName ('[' formulaDim ']')* ('^' objNum)?;
positionResult	:	'(' objResult+ ',' ('in' membraneName ('[' formulaDim ']')*  ('.' memPropertyCondition)*   |'out'|'here') ')';
boolCondition	:	( Letters | Name | Integer | '-' | '/' | '%' | '^' | '+' | '*' | '&&' 
					| '||' | '>=' | '>' | '==' | '!=' | '<=' | '<' | '≠' | '(' | ')' )+;//逻辑算术混合表达式
promoterCondition	: 	'@' objName ('[' formulaDim ']')* ;
inhibitorCondition:  '!' objName ('[' formulaDim ']')* ;
memPropertyCondition: 'm.' propertyName '==' propertyValue;
objCondtion		: 	objName ('[' formulaDim ']')* ('^' objNum)?;
priorityCondition: 	'pri' '=' priority;
priority		: 	Integer;

/*所有的维度类型*/
intDim			:	Integer;//整数维度
abcDim			:	Name | Letters;//字母维度
formulaDim		:	(Letters | Name | Integer | '-' | '/' | '%' | '^' | '+' | '*' | '(' | ')' )+;//算术表达式


/* 词法分析部分
 * 所有词法分析规则：大写开头
 * 在使用大概念的时候，要注意是否要包含其子概念。
 * 在书写词法分析规则时，子概念要写在前面，如果不写在前面则有可能在使用子概念的时候，词法分析结果不会出现子概念*/
Integer	:	[0-9]+;
Letters	:	[a-zA-Z]+;
Name	: 	([a-zA-Z] | [0-9] | '_')+;
COMMENT	:   ( '//' ~[\r\n]* '\r'? '\n'
        	| '/*' .*? '*/'
        	) -> skip;
WS  	:   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
