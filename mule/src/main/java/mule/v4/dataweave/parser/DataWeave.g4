grammar DataWeave;

// Lexer rules for DataWeave 2.0
VAR: 'var';
FUNCTION: 'fun';
IMPORT: 'import';
NAMESPACE: 'ns';
OUTPUT: 'output';
INPUT: 'input';
DW: '%dw';
TYPE: 'type';
ASSIGN: '=';
ARROW: '->';
BOOLEAN: 'true' | 'false';

// Keywords
AND: 'and';
OR: 'or';
NOT: 'not' | '!';
IF: 'if';
ELSE: 'else';
UNLESS: 'unless';
USING: 'using';
AS: 'as';
IS: 'is';
NULL: 'null';
DEFAULT: 'default';
CASE: 'case';
THROW: 'throw';
DO: 'do';
FOR: 'for';
YIELD: 'yield';
ENUM: 'enum';
PRIVATE: 'private';
ASYNC: 'async';
MAP: 'map';
FILTER: 'filter';
GROUP_BY: 'groupBy';
SIZE_OF: 'sizeOf';
UPPER: 'upper';
LOWER: 'lower';
REPLACE: 'replace';
WITH: 'with';
FROM: 'from';

// Built-in identifiers
NOW: 'now';

// Operators with names
OPERATOR_EQUALITY: '==' | '!=' | '~=';
OPERATOR_RELATIONAL:'>' | '<' | '>=' | '<=';
OPERATOR_MULTIPLICATIVE: '*' | '/';
OPERATOR_ADDITIVE: '+' | '>>';
MINUS: '-';
OPERATOR_RANGE: '..';
CONCAT: '++';

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
INDEX_IDENTIFIER: '$$';
VALUE_IDENTIFIER: '$';
URL: [a-zA-Z]+ '://' [a-zA-Z0-9./_-]+;
MEDIA_TYPE: [a-z]+ '/' [a-z0-9.+-]+;
NUMBER: [0-9]+('.'[0-9]+)?;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
STRING: '"' ('\\' . | ~["\\])* '"' | '\'' ('\\' . | ~['\\])* '\'';
DATE: '|' .*? '|';
REGEX: '/' .*? '/';
DOT: '.';
DOUBLE_COLON: '::';
COLON: ':';
COMMA: ',';
LCURLY: '{';
RCURLY: '}';
LSQUARE: '[';
RSQUARE: ']';
LPAREN: '(';
RPAREN: ')';
SEPARATOR: '---';
WS: [ \t]+ -> skip;
NEWLINE: [\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;

// Selectors
STAR: '*';
AT: '@';
QUESTION: '?';

// Parser rules
script
    : header (SEPARATOR body?)? NEWLINE* EOF
    | SEPARATOR? body NEWLINE* EOF
    ;

header: (directive (NEWLINE | WS)*)+;

directive
    : dwVersion
    | outputDirective
    | inputDirective
    | importDirective
    | namespaceDirective
    | variableDeclaration
    | functionDeclaration
    | typeDeclaration;

dwVersion: DW NUMBER;

outputDirective: OUTPUT MEDIA_TYPE outputOption*;

outputOption: IDENTIFIER ASSIGN outputOptionValue;

outputOptionValue: STRING | BOOLEAN | NUMBER | IDENTIFIER;

inputDirective: INPUT IDENTIFIER MEDIA_TYPE;

importDirective: IMPORT importSpec (COMMA importSpec)* (FROM (qualifiedIdentifier | STRING))?;

importSpec: STAR | qualifiedIdentifier (AS IDENTIFIER)?;

namespaceDirective: NAMESPACE IDENTIFIER URL;

variableDeclaration: VAR IDENTIFIER ASSIGN expression;

functionDeclaration: FUNCTION IDENTIFIER LPAREN functionParameters? RPAREN (COLON typeExpression)? ASSIGN expression;

typeDeclaration: TYPE IDENTIFIER ASSIGN typeExpression;

// Body of the parser
body: expression NEWLINE*;

// Expression Rules
expression
    : operationExpression
    ;

// Level 9: Operations (Map, Filter, GroupBy, Replace, Concat)
operationExpression
    : operationExpression FILTER implicitLambdaExpression   # filterExpression
    | operationExpression MAP implicitLambdaExpression      # mapExpression
    | operationExpression GROUP_BY implicitLambdaExpression # groupByExpression
    | operationExpression REPLACE REGEX WITH expression     # replaceExpression
    | operationExpression CONCAT defaultExpression          # concatExpression
    | operationExpression IDENTIFIER defaultExpression      # infixFunctionCall
    | defaultExpression                                     # operationExpressionWrapper
    ;

// Level 8.5: Default Expression
defaultExpression
    : logicalOrExpression (DEFAULT logicalOrExpression)?
    ;

// Implicit Lambda Expressions
implicitLambdaExpression
    : inlineLambda
    | expression
    | '(' implicitLambdaExpression ')'
    ;

// Lambda functions
inlineLambda: '(' functionParameters ')' ARROW expression;

functionParameters: functionParameter (COMMA functionParameter)*;

functionParameter: IDENTIFIER (COLON typeExpression)? (ASSIGN expression)?;


// Level 8: Logical OR
logicalOrExpression
    : logicalAndExpression (OR logicalAndExpression)*
    ;

// Level 7: Logical AND
logicalAndExpression
    : equalityExpression (AND equalityExpression)*
    ;

// Level 6: Equality Operators (==, !=, ~=)
equalityExpression
    : relationalExpression (OPERATOR_EQUALITY relationalExpression)*
    ;

// Level 5: Relational and Type Comparison (>, <, >=, <=, is)
relationalExpression
    : additiveExpression (OPERATOR_RELATIONAL additiveExpression)*     # relationalComparison
    | additiveExpression IS typeExpression                             # isExpression
    ;

// Level 4: Additive Operators (+, -, >>)
additiveExpression
    : multiplicativeExpression (additiveOperator multiplicativeExpression)*
    ;

additiveOperator: OPERATOR_ADDITIVE | MINUS;

// Level 3: Multiplicative Operators (*, /)
multiplicativeExpression
    : typeCoercionExpression (OPERATOR_MULTIPLICATIVE typeCoercionExpression)*
    ;

// Level 2: Type Coercion (`as`)
typeCoercionExpression
    : typeCoercionExpression AS typeExpression formatOption?
    | unaryExpression
    ;

// Formatting options within `{}`
formatOption
    : '{' IDENTIFIER ':' STRING '}'
    ;

// Level 1: Unary Operators (-, not)
unaryExpression
    : SIZE_OF '(' expression ')'           # sizeOfExpressionWithParentheses
    | SIZE_OF expression                   # sizeOfExpression
    | UPPER '(' expression ')'             # upperExpressionWithParentheses
    | UPPER expression                     # upperExpression
    | LOWER '(' expression ')'             # lowerExpressionWithParentheses
    | LOWER expression                     # lowerExpression
    | NOT expression                       # notExpression
    | MINUS expression                     # negativeExpression
    | primaryExpression                    # primaryExpressionWrapper
    ;

// **Primary Expressions (Highest Precedence)**
primaryExpression
    : IF LPAREN expression RPAREN expression (ELSE IF LPAREN expression RPAREN expression)* (ELSE expression)?    # ifElseCondition
    | doBlock                                                   # doBlockExpression
    | inlineLambda                                              # lambdaExpression
    | grouped                                                   # groupedExpression
    | literal                                                   # literalExpression
    | functionCall                                              # functionCallExpression
    | array                                                     # arrayExpression
    | object                                                    # objectExpression
    | builtInFunction                                           # builtInFunctionExpression
    | IDENTIFIER                                                # identifierExpression
    | VALUE_IDENTIFIER                                          # valueIdentifierExpression
    | INDEX_IDENTIFIER                                          # indexIdentifierExpression
    | primaryExpression selectorExpression                      # selectorExpressionWrapper
    | primaryExpression selectorExpression DEFAULT expression   # selectorExpressionWrapperWithDefault
    ;

// Built-in functions
builtInFunction
    : NOW '(' ')'                            # nowFunction
    ;

// Grouped expressions
grouped: '(' expression ')';

// Scoped expressions: do { <declarations> --- <expression> }
doBlock: DO LCURLY header? SEPARATOR expression RCURLY;

selectorExpression
    : DOT IDENTIFIER                         # singleValueSelector
    | DOT STRING                             # keySelector
    | DOT STAR IDENTIFIER                    # multiValueSelector
    | OPERATOR_RANGE IDENTIFIER              # descendantsSelector
    | LSQUARE expression RSQUARE             # indexedSelector
    | DOT AT IDENTIFIER                      # attributeSelector
    | QUESTION                               # existenceQuerySelector
    ;

// Literals
literal
    : STRING
    | NUMBER
    | BOOLEAN
    | DATE
    | REGEX
    | NULL;

// Arrays
array: LSQUARE (expression (COMMA expression)*)? RSQUARE;

// Objects
object
    : LCURLY objectField (COMMA? objectField)* RCURLY  # multiFieldObject
    | LCURLY objectField RCURLY                        # singleFieldObject
    | LCURLY RCURLY                                    # emptyObject
    ;

objectField
    : IDENTIFIER COLON expression              # unquotedKeyField
    | STRING COLON expression                  # quotedKeyField
    | '(' expression ')' COLON expression      # dynamicKeyField
    | '(' objectField ')' IF expression        # conditionalField
    ;

// Qualified identifiers for module references (e.g., Mule::p)
qualifiedIdentifier: IDENTIFIER (DOUBLE_COLON IDENTIFIER)*;

// Function calls
functionCall: qualifiedIdentifier '(' (expression (COMMA expression)*)? ')';

// Type expressions for DataWeave 2.0
typeExpression
    : IDENTIFIER                             # namedType
    | 'String'                               # stringType
    | 'Boolean'                              # booleanType
    | 'Number'                               # numberType
    | 'Regex'                                # regexType
    | 'Null'                                 # regexType
    | 'Date'                                 # dateType
    | 'DateTime'                             # dateTimeType
    | 'LocalDateTime'                        # localDateTimeType
    | 'LocalTime'                            # localTimeType
    | 'Time'                                 # timeType
    | 'Period'                               # periodType
//    | 'Array' '<' typeExpression '>'         # arrayType TODO: conflicts with [1, 4] filter ($ > 2). Revisit later
    | 'Object'                               # objectType
    | 'Any'                                  # anyType
    ;
