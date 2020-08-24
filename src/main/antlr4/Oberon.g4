grammar Oberon;

compilationUnit
  : 'MODULE' name = Id ';' ('CONST' constant*)? ('VAR' varDeclaration*)? 'END' Id '.'
  ;  

constant
  : varName = Id '=' exp = expression ';'
  ;

varDeclaration
  : (vars += Id (',' vars += Id)*) ':' varType = oberonType ';'
  ; 

expression
 : boolValue                                               #BooleanValue 
 | intValue                                                #IntegerValue
 | left = expression opr = ('*' | '/' | '&&') right = expression  #MultExpression  // '*' must come before '+', due to precedence
 | left = expression opr = ('+' | '-' | '||') right = expression  #AddExpression
 ;

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