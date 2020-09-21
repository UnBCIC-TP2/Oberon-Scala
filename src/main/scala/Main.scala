import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.codegen._

object Main extends App {
  val arg1 = FormalArg("x", IntegerType)
  val arg2 = FormalArg("y", BooleanType)
  val eqexp = EQExpression(IntValue(2), VarExpression("teste"))
  val exp = FunctionCallExpression("3eqExp", List(eqexp, eqexp, eqexp))
  val stmt = AssignmentStmt("teste", exp)
  val p1 = Procedure("pro1", List(arg1, arg2), Some(IntegerType), List[Constant](), List[VariableDeclaration](), SequenceStmt(List(stmt,stmt,stmt)))
  val p2 = Procedure("pro2", List(arg2, arg1), Some(BooleanType), List[Constant](), List[VariableDeclaration](), null)
  val p3 = Procedure("pro3", List(arg2, arg1), None, List[Constant](), List[VariableDeclaration](), null)
  val module = OberonModule("tESTAObeRon", List[Constant](), List[VariableDeclaration](), List(p1, p2, p3), None)
  val codeGen = PaigesBasedGenerator()
  codeGen.generateCode(module)
}