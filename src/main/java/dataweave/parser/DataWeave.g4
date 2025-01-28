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
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
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

// Operators (strictly complying with DataWeave 1.2 operators)
OPERATOR_MATH: '+' | '-' | '*' | '/' | 'mod';
OPERATOR_COMPARISON: '==' | '!=' | '>' | '<' | '>=' | '<=';
OPERATOR_LOGICAL: 'and' | 'or';
OPERATOR_BITWISE: '|' | '&' | '^';
OPERATOR_CONDITIONAL: '?' | ':';
OPERATOR_RANGE: '..';
OPERATOR_CHAIN: '++';

BUILTIN_FUNCTION: 'sizeOf' | 'map' | 'filter';

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

outputDirective: OUTPUT IDENTIFIER ('/' IDENTIFIER)?;

inputDirective: INPUT IDENTIFIER IDENTIFIER ('/' IDENTIFIER)?;

namespaceDirective: NAMESPACE IDENTIFIER STRING;

variableDeclaration: VAR IDENTIFIER ASSIGN expression;

functionDeclaration: FUNCTION IDENTIFIER ARROW expression;

body: expression (NEWLINE+ expression)*; // Only one expression allowed at the top level

// Expression rules with support for built-in functions
expression
    : builtInFunctionCall                                # builtInFunctionExpression
    | expression OPERATOR_CONDITIONAL expression COLON expression   # conditionalExpression
    | expression OPERATOR_LOGICAL expression                       # logicalExpression
    | expression OPERATOR_COMPARISON expression                    # comparisonExpression
    | expression OPERATOR_BITWISE expression                       # bitwiseExpression
    | expression OPERATOR_MATH expression                          # mathExpression
    | expression OPERATOR_RANGE expression                         # rangeExpression
    | expression OPERATOR_CHAIN expression                         # chainExpression
    | functionCall                                                 # functionCallExpression
    | literal                                                      # literalExpression
    | array                                                        # arrayExpression
    | object                                                       # objectExpression
    | IDENTIFIER                                                   # identifierExpression
    | '(' expression ')'                                           # groupedExpression
    ;

// Built-in function call
builtInFunctionCall
    : BUILTIN_FUNCTION '(' expression ')'                  // e.g., sizeOf(payload)
    | BUILTIN_FUNCTION '(' expression (COMMA expression)* ')' // e.g., map(payload, (item) -> item + 1)
    ;

// Inline lambda function for `map` and `filter`
inlineLambda
    : '(' (IDENTIFIER (COMMA IDENTIFIER)?)? ARROW expression ')'; // e.g., (item, index) -> item + 1


// Literals (values)
literal
    : STRING
    | NUMBER
    | BOOLEAN
    | DATE
    | REGEX
    ;

// Arrays
array: LSQUARE (expression (COMMA expression)*)? RSQUARE;

// Objects
object: LCURLY (keyValue (COMMA keyValue)*)? RCURLY;

keyValue: IDENTIFIER COLON expression;

// Function calls
functionCall: IDENTIFIER '(' (expression (COMMA expression)*)? ')';

