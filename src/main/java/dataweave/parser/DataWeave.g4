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
IDENTIFIER: INDEX_IDENTIFIER | VALUE_IDENTIFIER | [a-zA-Z_][a-zA-Z0-9_]* ;
URL: [a-zA-Z]+ '://' [a-zA-Z0-9./_-]+;
MEDIA_TYPE: [a-z]+ '/' [a-z0-9.+-]+;
NUMBER: [0-9]+('.'[0-9]+)?; // Matches integers and decimals
STRING: '"' .*? '"' | '\'' .*? '\''; // Support for single and double-quoted strings
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
DOUBLE_DOT: '..';
AT: '@';
QUESTION: '?';

// Operators
OPERATOR_MATH: '+' | '-' | STAR | '/' | 'mod';
OPERATOR_COMPARISON: '==' | '!=' | '>' | '<' | '>=' | '<=';
OPERATOR_LOGICAL: 'and' | 'or';
OPERATOR_BITWISE: '|' | '&' | '^';
OPERATOR_CONDITIONAL: '?' | ':';
OPERATOR_RANGE: DOUBLE_DOT;
OPERATOR_CHAIN: '++';

BUILTIN_FUNCTION: 'sizeOf' | 'map' | 'filter';

INDEX_IDENTIFIER: '$$';
VALUE_IDENTIFIER: '$';

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
    : primaryExpression                                               # primaryExpressionWrapper
    | expression OPERATOR_CONDITIONAL expression COLON expression     # conditionalExpression
    | expression OPERATOR_LOGICAL expression                          # logicalExpression
    | expression OPERATOR_COMPARISON expression                       # comparisonExpression
    | expression OPERATOR_BITWISE expression                          # bitwiseExpression
    | expression OPERATOR_MATH expression                             # mathExpression
    | expression OPERATOR_RANGE expression                            # rangeExpression
    | expression OPERATOR_CHAIN expression                            # chainExpression
    | expression 'map' implicitLambdaExpression                       # mapExpression
    ;

// Primary Expressions (Non-Recursive Base Expressions)
primaryExpression
    : functionCall                                          # functionCallExpression
    | 'sizeOf' ('(' (expression) ')' | expression)          # sizeOfExpression
    | 'upper' ('(' (expression) ')' | expression)           # upperExpression
    | 'lower' ('(' (expression) ')' | expression)           # lowerExpression
    | inlineLambda                                          # lambdaExpression
    | literal                                               # literalExpression
    | array                                                 # arrayExpression
    | object                                                # objectExpression
    | IDENTIFIER                                            # identifierExpression
    | grouped                                               # groupedExpression
    | primaryExpression DOT IDENTIFIER                      # singleValueSelector
    | primaryExpression DOT STAR IDENTIFIER                 # multiValueSelector
    | primaryExpression OPERATOR_RANGE IDENTIFIER           # descendantsSelector
    | primaryExpression LSQUARE NUMBER RSQUARE              # indexedSelector
    | primaryExpression DOT AT IDENTIFIER                   # attributeSelector
    | primaryExpression QUESTION                            # existenceQuerySelector
    ;

// Implicit Lambda Expressions (Ensuring `$` or `$$` is inside)
implicitLambdaExpression
    : expression                                     # singleParameterImplicitLambda
    | '(' IDENTIFIER (',' IDENTIFIER)* ')' ARROW expression  # multiParameterImplicitLambda
    ;

// Built-in function call
builtInFunctionCall
    : BUILTIN_FUNCTION '(' expression (COMMA expression)* ')';

// Lambda functions
inlineLambda: '(' functionParameters ')' ARROW expression;

functionParameters: IDENTIFIER (COMMA IDENTIFIER)*;

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
object: LCURLY (keyValue (COMMA keyValue)*)? RCURLY;

keyValue: IDENTIFIER COLON expression;

// Function calls
functionCall: IDENTIFIER '(' (expression (COMMA expression)*)? ')';

// Grouped expressions
grouped: '(' expression ')';

