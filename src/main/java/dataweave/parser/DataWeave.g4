grammar DataWeave;

// Lexer rules
VAR: '%var';
FUNCTION: '%function';
INPUT: '%input';
NAMESPACE: '%namespace';
OUTPUT: '%output';
DW: '%dw';
ASSIGN: '=';
ARROW: '->';
BOOLEAN: 'true' | 'false';

// Operators with names
OPERATOR_EQUALITY: '==' | '!=' | '~=';
OPERATOR_RELATIONAL:'>' | '<' | '>=' | '<=' | 'is';
OPERATOR_MULTIPLICATIVE: '*' | '/';
OPERATOR_ADDITIVE: '+' | '>>' | '-' ;
OPERATOR_TYPE_COERCION: 'as';
OPERATOR_RANGE: '..';

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
INDEX_IDENTIFIER: '$$';
VALUE_IDENTIFIER: '$';
URL: [a-zA-Z]+ '://' [a-zA-Z0-9./_-]+;
MEDIA_TYPE: [a-z]+ '/' [a-z0-9.+-]+;
NUMBER: [0-9]+('.'[0-9]+)?; // Matches integers and decimals
STRING: '"' .*? '"' | '\'' .*? '\'';
DATE: '|' .*? '|'; // ISO-8601 enclosed in "|"
REGEX: '/' .*? '/';
DOT: '.';
COLON: ':';
COMMA: ',';
LCURLY: '{';
RCURLY: '}';
LSQUARE: '[';
RSQUARE: ']';
SEPARATOR: '---';
WS: [ \t]+ -> skip; // Skip whitespace
NEWLINE: [\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;

// Selectors
STAR: '*';
AT: '@';
QUESTION: '?';

// Parser rules
script: header? SEPARATOR body? NEWLINE* EOF;

header: (directive (NEWLINE | WS)*)+;

directive
    : dwVersion
    | outputDirective
    | inputDirective
    | namespaceDirective
    | variableDeclaration
    | functionDeclaration;

dwVersion: DW NUMBER;

outputDirective: OUTPUT MEDIA_TYPE;

inputDirective: INPUT IDENTIFIER MEDIA_TYPE;

namespaceDirective: NAMESPACE IDENTIFIER URL;

variableDeclaration: VAR IDENTIFIER ASSIGN expression;

functionDeclaration: FUNCTION IDENTIFIER '(' functionParameters? ')' expression;

body: expression NEWLINE*;

// Expression Rules (Rewritten for Precedence)
expression
    : defaultExpression                             # expressionWrapper
    | conditionalExpression                         # conditionalExpressionWrapper
    ;

// Level 10: Conditional Expressions (WHEN OTHERWISE, UNLESS OTHERWISE)
conditionalExpression
    : defaultExpression ('when' defaultExpression 'otherwise' defaultExpression)+     #whenCondition
    | defaultExpression ('unless' defaultExpression 'otherwise' defaultExpression)+   #unlessCondition
    ;

// Implicit Lambda Expressions (Ensuring `$` or `$$` is inside)
implicitLambdaExpression
    : expression
    | inlineLambda
    | '(' implicitLambdaExpression ')'
    ;

// Lambda functions
inlineLambda: '(' functionParameters ')' ARROW expression;

functionParameters: IDENTIFIER (COMMA IDENTIFIER)*;

// Level 9: Default value, Pattern Matching, Map, Filter
defaultExpression
    : logicalOrExpression defaultExpressionRest # defaultExpressionWrapper
    ;

defaultExpressionRest
    : 'filter' implicitLambdaExpression defaultExpressionRest  # filterExpression
    | 'map' implicitLambdaExpression defaultExpressionRest     # mapExpression
    | 'groupBy' implicitLambdaExpression defaultExpressionRest # groupByExpression
    | 'replace' REGEX 'with' expression                        # replaceExpression
    | '++' expression                                          # concatExpression
    | /* epsilon (empty) */                                    # defaultExpressionEnd
    ;

// Level 8: Logical OR
logicalOrExpression
    : logicalAndExpression ('or' logicalAndExpression)*
    ;

// Level 7: Logical AND
logicalAndExpression
    : equalityExpression ('and' equalityExpression)*
    ;

// Level 6: Equality Operators (==, !=, ~=)
equalityExpression
    : relationalExpression (OPERATOR_EQUALITY relationalExpression)*
    ;

// Level 5: Relational and Type Comparison (>, <, >=, ⇐, is)
relationalExpression
    : additiveExpression (OPERATOR_RELATIONAL additiveExpression)*
    ;

// Level 4: Additive Operators (+, -, >>)
additiveExpression
    : multiplicativeExpression (OPERATOR_ADDITIVE multiplicativeExpression)*
    ;

// Level 3: Multiplicative Operators (*, /)
multiplicativeExpression
    : typeCoercionExpression (OPERATOR_MULTIPLICATIVE typeCoercionExpression)*
    ;

// Level 2: Type Coercion (`as`)
typeCoercionExpression
    : unaryExpression (OPERATOR_TYPE_COERCION typeExpression formatOption?)?
    ;

// Formatting options within `{}`
formatOption
    : '{' IDENTIFIER ':' STRING '}'
    ;

// Level 1: Unary Operators (-, not)
unaryExpression
    : 'sizeOf' expression                   # sizeOfExpression
    | 'sizeOf' '(' expression ')'           # sizeOfExpressionWithParentheses
    | 'upper' expression                    # upperExpression
    | 'upper' '(' expression ')'            # upperExpressionWithParentheses
    | 'lower' expression                    # lowerExpression
    | 'lower' '(' expression ')'            # lowerExpressionWithParentheses
    | primaryExpression                     # primaryExpressionWrapper
    ;

// **Primary Expressions (Highest Precedence)**
primaryExpression
    : inlineLambda                           # lambdaExpression
    | grouped                                # groupedExpression
    | literal                                # literalExpression
    | functionCall                           # functionCallExpression
    | array                                  # arrayExpression
    | object                                 # objectExpression
    | IDENTIFIER                             # identifierExpression
    | VALUE_IDENTIFIER                       # valueIdentifierExpression
    | INDEX_IDENTIFIER                       # indexIdentifierExpression
    | primaryExpression selectorExpression   # selectorExpressionWrapper
    ;

// Grouped expressions
grouped: '(' expression ')';

selectorExpression
    : DOT IDENTIFIER                         # singleValueSelector
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
    | REGEX;

// Arrays
array: LSQUARE (expression (COMMA expression)*)? RSQUARE;

// Objects
object
    : LCURLY keyValue (COMMA keyValue)* RCURLY  # multiKeyValueObject
    | keyValue                                  # singleKeyValueObject
    ;

keyValue: IDENTIFIER COLON expression;

// Function calls
functionCall: IDENTIFIER '(' (expression (COMMA expression)*)? ')';

typeExpression
    : ':' IDENTIFIER;
