/** This is the grammer of UPLanguage
 * @author quancs from CQU, Computer Science
 * @date 2017/05/20
 */
grammar UPLanguage;

/*结构相关部分 */
start  			: 	( membraneDef | ruleSetDef )* environmentDef ( membraneDef | ruleSetDef )*;
environmentDef  : 	'Environment' '{'  (objects | prule | submembrane | ruleSetDeclare | properties)*  '}';
ruleSetDef		:	'RuleSet' ruleSetType '{' prule* '}';
ruleSetType		: 	Name | Letters;
membraneDef 	: 	'Membrane' membraneType ('extends' membraneType (',' membraneType)* )? '{'  (objects | prule | submembrane | ruleSetDeclare | properties)*  '}';
membraneType 	: 	Name | Letters;

/*膜属性 */
properties		:	'Property' propertyInit (',' propertyInit)* ';';
propertyInit	:	propertyName '=' propertyValue;
propertyName	:	Name | Letters;
propertyValue	:	Letters | Integer | '+' | '-';

/*膜通道*/
tunnels			:	'Tunnel' tunnelTarget ( ',' tunnelTarget )* ';';
tunnelTarget	:	membraneName('[' intDim ']')* ;

/*规则集声明*/
ruleSetDeclare	: 	'RuleSet' ruleSetType ruleSetNamePrefix? ';';
ruleSetNamePrefix:  Name | Letters;

/*膜定义*/
submembrane		: 	'Membrane' membraneType? membraneName ('[' intDim ']')*  ( '{' (objects | prule | submembrane | ruleSetDeclare | properties | tunnels)* '}' ';'? | ';');
membraneName	: 	Name | Letters | Integer;

/*对象定义与赋值*/
objects 		:	'Object' objAssign (','  objAssign )* ';';
objAssign		:	objName ('[' intDim ']')* ('^' objNum)?;
objName			:	Name | Letters;
objNum			:	'-'? Integer;

/*规则定义*/
prule			:	'Rule' ruleName ('[' abcDim ']')* '=' (regCondition '/')? propertyCondition* (objCondition | objConditionWithTarget)+ '->' propertyResult* result*
					('|' condition ('&' condition)* )? priorityCondition? ';';

ruleName		:	Name | Letters;
condition		:	promoterCondition | inhibitorCondition  | boolCondition | probabilisticCondition;
result			:	objResult | positionResult | memDissolveResult | memCreateResult | memDivisionResult;
memDivisionResult: 'divide' '(' '{' additionalResults '}' ',' '{' additionalResults '}' ')';
additionalResults: 	propertyResult* objResult*;

memCreateResult	: 	membraneType ':' membraneName ('[' formulaDim ']')* ('{' additionalResults '}')? ;
memDissolveResult:	'dissolve';
propertyResult	:	'<' (propertyName '=')? propertyValue '>';//Default propertyName=Status
objResult		:	objName ('[' formulaDim ']')* ('^' objNum)?;
positionResult	:	'(' objResult+ ',' (out | here | 
					( in | go ) 
					( 	target ( ( andOpt target)+ | (orOpt target)+ )?
						| all 
						| random
					))')';
in				:	'in';
out				:	'out';
here			:	'here';
go				:	'go';
andOpt			:	'&';
orOpt			:	'|';
all				:	'all';
random			:	'random';

target			:	membraneName ('[' formulaDim ']')* propertyCondition* ;
boolCondition	:	( Letters | Name | Integer | '-' | '/' | '%' | '^' | '+' | '*' | '&&' 
					| '||' | '>=' | '>' | '==' | '!=' | '<=' | '<' | '≠' | '(' | ')' )+;//逻辑算术混合表达式
promoterCondition: 	'@' objName ('[' formulaDim ']')* ;
inhibitorCondition:	'!' objName ('[' formulaDim ']')* ;
propertyCondition:	'<' (propertyName '=')? propertyValue '>';//Default propertyName=Status
objCondition		:	objName ('[' formulaDim ']')* ('^' objNum)?;
objConditionWithTarget: ( 'out' | 'in.' membraneName ('[' formulaDim ']')* | 'go.' membraneName ('[' formulaDim ']')*) '{' objCondition+ '}';
priorityCondition:	',' priority;
priority		:	Integer;
probabilisticCondition: 'probability' '=' Double;
regCondition	:	(Letters | '+' | '(' | ')' | '[' | ']' | '{' | '}' | ',' | '^' | '*' | '+' | '?' | '|' )+;

/*所有的维度类型*/
intDim			:	Integer;//整数维度
abcDim			:	Name | Letters;//字母维度
formulaDim		:	(Letters | Name | Integer | '-' | '/' | '%' | '^' | '+' | '*' | '(' | ')' )+;//算术表达式


/* 词法分析部分
 * 所有词法分析规则：大写开头
 * 在使用大概念的时候，要注意是否要包含其子概念。
 * 在书写词法分析规则时，子概念要写在前面，如果不写在前面则有可能在使用子概念的时候，词法分析结果不会出现子概念，因为写在前面的概念优先被识别*/
Integer	:	[0-9]+;
Double	: 	[0-9]+ '.' [0-9]+;
Letters	:	[a-zA-Z]+;
Name	: 	([a-zA-Z] | [0-9] | '_')+;
COMMENT	:   ( '//' ~[\r\n]* '\r'? '\n'
        	| '/*' .*? '*/'
        	) -> skip;
WS  	:   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
