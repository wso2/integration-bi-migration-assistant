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
OPERATOR_LOGICAL: 'and' | 'or';
OPERATOR_COMPARISON: '==' | '!=' | '>' | '<' | '>=' | '<=';
OPERATOR_MATH: '+' | '-' | STAR | '/' | 'mod';
OPERATOR_RANGE: '..';
OPERATOR_CHAIN: '++';

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
AT: '@';
QUESTION: '?';

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
    : primaryExpression selectorExpression? expressionRest?  # primaryExpressionWrapper
    ;

expressionRest
    : OPERATOR_CHAIN expression         # chainExpression
    | OPERATOR_RANGE expression          # rangeExpression
    | OPERATOR_MATH expression            # mathExpression
    | OPERATOR_COMPARISON expression      # comparisonExpression
    | OPERATOR_LOGICAL expression         # logicalExpression
    | 'filter' implicitLambdaExpression   # filterExpression
    | 'map' implicitLambdaExpression      # mapExpression
    ;

// Primary Expressions (Non-Recursive Base Expressions)
primaryExpression
    : grouped                                               # groupedExpression
    | primitive                                             # primitiveExpression
    | functionCall                                          # functionCallExpression
    | 'sizeOf' ('(' expression ')' | expression)           # sizeOfExpression
    | 'upper' ('(' expression ')' | expression)            # upperExpression
    | 'lower' ('(' expression ')' | expression)            # lowerExpression
    | inlineLambda                                          # lambdaExpression
    ;

primitive
    : literal                                               # literalExpression
    | array                                                 # arrayExpression
    | object                                                # objectExpression
    | IDENTIFIER                                            # identifierExpression
    ;

// Grouped expressions
grouped: '(' expression ')';

selectorExpression
    : (DOT IDENTIFIER)                  # singleValueSelector
    | (DOT STAR IDENTIFIER)              # multiValueSelector
    | (OPERATOR_RANGE IDENTIFIER)        # descendantsSelector
    | (LSQUARE NUMBER RSQUARE)           # indexedSelector
    | (DOT AT IDENTIFIER)                # attributeSelector
    | (QUESTION)                        # existenceQuerySelector
    ;

// Implicit Lambda Expressions (Ensuring `$` or `$$` is inside)
implicitLambdaExpression
    : expression                                     # singleParameterImplicitLambda
    | '(' IDENTIFIER (',' IDENTIFIER)* ')' ARROW expression  # multiParameterImplicitLambda
    ;

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
object
    : LCURLY keyValue (COMMA keyValue)* RCURLY  # multiKeyValueObject
    | keyValue                                  # singleKeyValueObject
    ;

keyValue: IDENTIFIER COLON expression;

// Function calls
functionCall: IDENTIFIER '(' (expression (COMMA expression)*)? ')';
