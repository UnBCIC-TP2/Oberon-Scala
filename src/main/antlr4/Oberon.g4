grammar Oberon;

compilationUnit
  : 'MODULE' name = Id ';' imports? declarations block? 'END' Id '.'
  ;

imports
  : 'IMPORT' importList ';'
  ;

importList
  : (modules += importModule (',' modules += importModule)*)
  ;

importModule
  : module = Id (':=' alias = Id)?
  ;


declarations
  : ('TYPE' userTypeDeclaration+) ? ('CONST' constant+)? ('VAR' varDeclaration+)? procedure*
  ;

userTypeDeclaration
  : nameType = Id '=' baseType = userType
  ;

userType
: ('ARRAY' length = INT 'OF' baseType = oberonType)     #ArrayTypeDeclaration
| ('RECORD' (vars += varDeclaration)+ 'END')            #RecordTypeDeclaration
| ('POINTER' 'TO' baseType = oberonType)                #PointerTypeDeclaration
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
 | expValue                                                                               #Value
 | name = qualifiedName                                                                   #Variable
 | name = qualifiedName '(' arguments? ')'                                                #FunctionCall
 | exp = expression '.' name = Id                                                         #FieldAccess
 | arrayBase = expression '[' index = expression ']'                                      #ArraySubscript
 | name = Id '^'                                                                          #PointerAccess
 | left = expression opr = ('=' | '#' | '<' | '<=' | '>' | '>=')  right = expression      #RelExpression
 | left = expression opr = ('*' | '/' | '&&') right = expression                          #MultExpression
 | left = expression opr = ('+' | '-' | '||') right = expression                          #AddExpression
 ;

qualifiedName
  : (module = Id '::')? name = Id
  ;


statement
 : var = Id ':=' exp = expression                                                                                             #AssignmentStmt
 | des = designator ':=' exp = expression                                                                                     #EAssignmentStmt
 | stmt += statement (';' stmt += statement)+                                                                                 #SequenceStmt
 | 'readReal'       '(' var = Id ')'                                                                                          #ReadRealStmt
 | 'readInt'        '(' var = Id ')'                                                                                          #ReadIntStmt
 | 'readChar'   '(' var = Id ')'                                                                                              #ReadCharStmt
 | 'write' '(' expression ')'                                                                                                 #WriteStmt
 | name = Id '(' arguments? ')'                                                                                               #ProcedureCall
 | 'IF' cond = expression 'THEN' thenStmt = statement ('ELSE' elseStmt = statement)? 'END'                                    #IfElseStmt
 | 'IF' cond = expression 'THEN' thenStmt = statement ('ELSIF' elsifs += elseIfStmt)+ ('ELSE' elseStmt = statement)? 'END'    #IfElseIfStmt
 | 'WHILE' cond = expression 'DO' stmt = statement 'END'                                                                      #WhileStmt
 | 'REPEAT' stmt = statement 'UNTIL' cond = expression                                                                        #RepeatUntilStmt
 | 'FOR' init = statement 'TO' condition = expression 'DO' stmt = statement 'END'                                             #ForStmt
 | 'FOR' var = Id 'IN' min = expression '..' max = expression 'DO' stmt = statement 'END'                                     #ForRangeStmt
 | 'LOOP' stmt = statement 'END'                                                                                              #LoopStmt
 | 'RETURN' exp = expression                                                                                                  #ReturnStmt
 | 'CASE' exp = expression 'OF' cases += caseAlternative ('|' cases += caseAlternative)* ('ELSE' elseStmt= statement)? 'END'  #CaseStmt
 | 'EXIT'                                                                                                                     #ExitStmt
 ;



designator
  : var = Id                                                          #VarAssignment
  | array = expression '[' elem = expression ']'                      #ArrayAssignment
  | record = expression '.' name = Id                                 #RecordAssignment
  | pointer = Id '^'                                                  #PointerAssignment
  ;

caseAlternative
 : cond = expression ':' stmt = statement                       #SimpleCase
 | min = expression '..' max = expression ':' stmt = statement  #RangeCase
 ;

elseIfStmt : cond = expression 'THEN' stmt = statement ;

// TODO: NOT, MOD, Relational operators,
// <assoc=right> expr '::' expr

expValue
  : intValue
  | realValue
  | charValue
  | stringValue
  | boolValue
  ;

intValue: INT ;
realValue: REAL ;
charValue: CHAR ;
stringValue: STRING ;
boolValue: TRUE | FALSE ;

oberonType
 : 'INTEGER'         #IntegerType
 | 'REAL'            #RealType
 | 'CHAR'            #CharacterType
 | 'BOOLEAN'         #BooleanType
 | 'STRING'          #StringType
 | name = Id         #ReferenceType        // Reference for user defined types
 | userType          #ComplexType
 ;

INT : '-'? Digit+;
REAL : '-'? Digit+ '.' Digit+;
CHAR : '\'' CharDef '\'';

TRUE  : 'True' ;
FALSE : 'False'  ;

STRING
   : ('"' .*? '"')
   ;

Id : CharDef (CharDef | Digit | '_')* ;


repl: varDeclaration #REPLVarDeclaration
      | constant #REPLConstant
      | expression #REPLExpression
      | statement #REPLStatement
      | userTypeDeclaration #REPLUserTypeDeclaration
      ;

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
