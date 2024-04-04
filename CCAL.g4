grammar CCAL;

fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];

SEMI: ';';
COMMA: ',';
COLON: ':';
ASSIGN: '=';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
PLUS: '+';
MINUS: '-';
NOT: '~';
OR: '||';
AND: '&&';
EQ: '==';
NEQ: '!=';
LT: '<';
LTEQ: '<=';
GT: '>';
GTEQ: '>=';

INTEGER: ('-'? '1' ..'9' '0' ..'9'* | '0');

VOID: V O I D;
INTEGER_TYPE: I N T E G E R;
BOOLEAN_TYPE: B O O L E A N;
VAR: V A R;
CONST: C O N S T;
RETURN: R E T U R N;
IF: I F;
ELSE: E L S E;
WHILE: W H I L E;
SKIPLN: S K I P;
MAIN: M A I N;
TRUE: T R U E;
FALSE: F A L S E;

IDENTIFIER: [_a-zA-Z] [_a-zA-Z0-9]*;
WS: [ \t\r\n]+ -> skip;
SLCOMMENT: '//' ~[\r\n]* -> skip;
MLCOMMENT: '/*' (MLCOMMENT | .)*? '*/' -> skip;

/* Parser Rules */
program: decl_list? function_list? main_block;

main_block: MAIN LBRACE decl_list? statement_block? RBRACE;

decl_list: (decl (decl_list)?);

decl: var_decl | const_decl;

var_decl: VAR IDENTIFIER COLON type ';';

const_decl: CONST IDENTIFIER COLON type ASSIGN expression ';';

function_list: (function (function_list)?);

function:
	type IDENTIFIER LPAREN parameter_list RPAREN LBRACE decl_list statement_block RETURN (
		expression
		|
	) SEMI RBRACE;

type: INTEGER_TYPE | BOOLEAN_TYPE | VOID;

parameter_list: (nonempty_parameter_list COMMA?)*;

nonempty_parameter_list:IDENTIFIER COLON type;

statement_block: statement (statement_block)?;

statement:
	assignment_statement
	| call_statement
	| block_statement
	| if_statement
	| while_statement
	| skip_statement;

assignment_statement: IDENTIFIER ASSIGN expression SEMI;

call_statement: IDENTIFIER LPAREN arg_list RPAREN;

block_statement: LBRACE statement_block RBRACE;

if_statement:
	IF condition LBRACE statement_block RBRACE ELSE LBRACE statement_block RBRACE;

while_statement: WHILE condition LBRACE statement_block RBRACE;

skip_statement: SKIPLN SEMI;

expression: fragM										#frag_expr
|fragM (binary_arith_op fragM)+  		#binary_arith_opexpr
	| LPAREN expression RPAREN					#parens_expr
	| call_statement 							#call_expr
;
fragM:
	IDENTIFIER					# idfrag
	| MINUS IDENTIFIER			# negidfrag
	| INTEGER					# intfrag
	| TRUE						# truefrag
	| FALSE						# falsefrag
	| LPAREN expression RPAREN	# parensfrag;
binary_arith_op: PLUS		# plusop
                |MINUS		# minusop
				;

condition:
	NOT condition						# negcondition
	| LPAREN condition RPAREN			# parenscondition
	| expression comp_op expression		# compcondition
	| condition OR condition	        # orcondition
    | condition AND condition			# andcondition;

comp_op: EQ | NEQ | LT | LTEQ | GT | GTEQ;

arg_list: (nonempty_arg_list COMMA?)*;

nonempty_arg_list: IDENTIFIER;
