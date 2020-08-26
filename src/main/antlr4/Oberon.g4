grammar Oberon;

compilationUnit
  : 'MODULE' name = Id ';' ('CONST' constant*)? ('VAR' varDeclaration*)? block? 'END' Id '.'
  ;  

constant
  : varName = Id '=' exp = expression ';'
  ;

varDeclaration
  : (vars += Id (',' vars += Id)*) ':' varType = oberonType ';'    
  ; 

block
 : 'BEGIN' statement 'END'
 ; 
    
expression
 : '(' expression ')'                                                                     #Brackets
 | intValue                                                                               #IntegerValue
 | Id                                                                                     #Variable
 | left = expression opr = ('=' | '#' | '<' | '<=' | '>' | '>=')  right = expression      #RelExpression 
 | left = expression opr = ('*' | '/' | '&&') right = expression                          #MultExpression  // '*' must come before '+', due to precedence
 | left = expression opr = ('+' | '-' | '||') right = expression                          #AddExpression
 | boolValue                                                                              #BooleanValue 
 ;

statement
 : var = Id ':=' exp = expression                                                          #AssignmentStmt
 | stmt += statement (';' stmt += statement)+                                              #SequenceStmt
 | 'read'  '(' var = Id ')'                                                                #ReadStmt
 | 'write' '(' expression ')'                                                              #WriteStmt
 | 'IF' cond = expression 'THEN' thenStmt = statement ('ELSE' elseStmt = statement)? 'END' #IfElseStmt
 | 'WHILE' cond = expression 'DO' stmt = statement 'END'                                   #WhileStmt
 ; 
 
// TODO: NOT, MOD, Relational operators, 
// <assoc=right> expr '::' expr

intValue : INT ;

boolValue: TRUE | FALSE ;

oberonType
 : 'INTEGER'
 | 'BOOLEAN'
 ;

INT : Digit+;

TRUE  : 'True' ;
FALSE : 'False'  ;


Id : CharDef (CharDef | Digit | '_')* ;

CharDef
  : ('a'..'z') | ('A' .. 'Z')
  ;


fragment Digit : [0-9] ;
 
//
// Whitespace and comments
//

NL
   : '\n'
   | '\r' '\n'?
   ;
   

fragment WhiteSpace
   : '\u0020' | '\u0009' | '\u000D' | '\u000A'
   ;
   
NEWLINE
   : NL+ -> skip
   ;

WS
   :  WhiteSpace+ -> skip
   ;

COMMENT
   :   '/*' .*? '*/' -> skip
   ;


LINE_COMMENT
   :   '//' (~[\r\n])* -> skip
   ;