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
OPERATOR: '++' | '+' | '-' | '*' | '/'; // Include common operators
DOT: '.';
COLON: ':';
COMMA: ',';
LCURLY: '{';
RCURLY: '}';
LSQUARE: '[';
RSQUARE: ']';
SEPARATOR: '---';
NEWLINE: [\r\n]+ -> skip; // Skip newline characters
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;

REGEX: '/' .*? '/' ;

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

// Expression rules for the body
expression
    : literal                              # literalExpression
    | array                                # arrayExpression
    | object                               # objectExpression
    | functionCall                         # functionCallExpression
    | IDENTIFIER                           # identifierExpression
    ;

literal
    : STRING
    | NUMBER
    | BOOLEAN
    | DATE
    | REGEX
    ;

// Array support
array: LSQUARE (expression (COMMA expression)*)? RSQUARE;

// Object support
object: LCURLY (keyValue (COMMA keyValue)*)? RCURLY;

keyValue: IDENTIFIER COLON expression;

// Function calls
functionCall: IDENTIFIER '(' (expression (COMMA expression)*)? ')';
