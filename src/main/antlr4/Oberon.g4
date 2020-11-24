grammar Oberon;

compilationUnit
  : 'MODULE' name = Id ';' declarations block? 'END' Id '.'
  ;  

declarations
  : ('CONST' constant+)? ('VAR' varDeclaration+)? procedure*
  ;
  
constant
  : constName = Id '=' exp = expression ';'
  ;

varDeclaration
  : (vars += Id (',' vars += Id)*) ':' varType = oberonType ';'    
  ; 

procedure :
  'PROCEDURE' name = Id '(' formals?  ')' (':' procedureType = oberonType)? ';'
    declarations    // NOTE: This might support nested procedures
    block
   Id
  ; 

formals
 : formalArg (',' formalArg)*
 ;

arguments
 : expression (',' expression)*
 ;
 
formalArg 
 : (args += Id (',' args += Id)*) ':' argType = oberonType 
 ; // TODO: we should also support VarBased formal arguments.
 
block
 : 'BEGIN' statement 'END'
 ; 
    
expression
 : '(' expression ')'                                                                     #Brackets
 | intValue                                                                               #IntegerValue
 | boolValue                                                                              #BooleanValue 
 | name = Id                                                                              #Variable
 | name = Id '(' arguments? ')'                                                           #FunctionCall       
 | left = expression opr = ('=' | '#' | '<' | '<=' | '>' | '>=')  right = expression      #RelExpression 
 | left = expression opr = ('*' | '/' | '&&') right = expression                          #MultExpression  
 | left = expression opr = ('+' | '-' | '||') right = expression                          #AddExpression
 | exp = expression '.' name = Id                                                         #ExpressionName
 ;

statement
 : var = Id ':=' exp = expression                                                                                             #AssignmentStmt
 | stmt += statement (';' stmt += statement)+                                                                                 #SequenceStmt
 | 'readInt'  '(' var = Id ')'                                                                                                #ReadIntStmt
 | 'write' '(' expression ')'                                                                                                 #WriteStmt
 | name = Id '(' arguments? ')'                                                                                               #ProcedureCall
 | 'IF' cond = expression 'THEN' thenStmt = statement ('ELSE' elseStmt = statement)? 'END'                                    #IfElseStmt
 | 'IF' cond = expression 'THEN' thenStmt = statement ('ELSIF' elsifs += elseIfStmt)+ ('ELSE' elseStmt = statement)? 'END'                                 #IfElseIfStmt
 | 'WHILE' cond = expression 'DO' stmt = statement 'END'                                                                      #WhileStmt
 | 'REPEAT' stmt = statement 'UNTIL' cond = expression                                                                        #RepeatUntilStmt
 | 'FOR' init = statement 'TO' condition = expression 'DO' stmt = statement 'END'                                             #ForStmt
 | 'FOR' var = Id 'IN' min = expression '..' max = expression 'DO' stmt = statement 'END'                                     #ForRangeStmt
 | 'RETURN' exp = expression                                                                                                  #ReturnStmt
 | 'CASE' exp = expression 'OF' cases += caseAlternative ('|' cases += caseAlternative)* ('ELSE' elseStmt= statement)? 'END'  #CaseStmt
 ;

caseAlternative
 : cond = expression ':' stmt = statement                       #SimpleCase
 | min = expression '..' max = expression ':' stmt = statement  #RangeCase
 ; 

elseIfStmt : cond = expression 'THEN' stmt = statement ;

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

fragment CharDef
  : ('a'..'z') | ('A' .. 'Z')
  ;


fragment Digit : [0-9] ;

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;