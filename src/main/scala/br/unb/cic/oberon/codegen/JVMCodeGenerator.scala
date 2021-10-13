package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.ast._
import org.objectweb.asm._
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label._ 

import java.io.PrintStream
import java.util.Base64

object JVMCodeGenerator extends CodeGenerator {

  override def generateCode(module: OberonModule): String = {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    cw.visit(V1_5, ACC_PUBLIC, module.name, null, "java/lang/Object", null);

    generateConstants(module.constants, cw)
    generateVariables(module.variables, cw)

    // TODO: Ideally, we should only call the
    //  generateMainMethod if the module.stmt
    //  is defined.

    generateMainMethod(cw)

    //  TODO: this is code bellow is safer.
    //    if(module.stmt.isDefined) {
    //      generateMainMethod(module.stmt.get, cw)
    //    }


    cw.visitEnd()

    Base64.getEncoder().encodeToString(cw.toByteArray)

  }
  
  def generateExpression(expression: Expression, mv: MethodVisitor): Unit = expression match {
      case IntValue(v) => mv.visitLdcInsn(v)
      case RealValue(v) => mv.visitLdcInsn(v)
      case CharValue(v) => mv.visitLdcInsn(v)
      case BoolValue(v) => mv.visitLdcInsn(v)
      case StringValue(v) => mv.visitLdcInsn(v)
      case Brackets(exp) => { /* noop */}
      case EQExpression(left, right) => generateRelExpression(left, right, mv, IF_ICMPNE)
      case NEQExpression(left, right) => generateRelExpression(left, right, mv, IF_ICMPEQ)
      case GTExpression(left, right) => generateRelExpression(left, right, mv, IF_ICMPLE)
      case LTExpression(left, right) => generateRelExpression(left, right, mv, IF_ICMPGE)
      case GTEExpression(left, right) => generateRelExpression(left, right, mv, IF_ICMPLT)
      case LTEExpression(left, right) => generateRelExpression(left, right, mv, IF_ICMPGT)
      case AddExpression(left, right) => generateBinExpression(left, right, mv, IADD)
      case SubExpression(left, right) => generateBinExpression(left, right, mv, ISUB)
      case MultExpression(left, right) => generateBinExpression(left, right, mv, IMUL)
      case DivExpression(left, right) => generateBinExpression(left, right, mv, IDIV)
      case AndExpression(left, right) => {
        val falseLabel = new Label()
        val endLabel = new Label()

        /** coloca falso na pilha se a primeira expressão já for falsa */
        generateExpression(left, mv)
        mv.visitJumpInsn(IFEQ, falseLabel)

        /** verifica a segunda expressão */
        generateExpression(right, mv)
        mv.visitJumpInsn(IFEQ, falseLabel)

        /** coloca true na pilha se ambas passaram nos testes */
        mv.visitInsn(ICONST_1)
        mv.visitJumpInsn(GOTO, endLabel)

        /** coloca false na pilha */
        mv.visitLabel(falseLabel)
        mv.visitInsn(ICONST_0)

        mv.visitLabel(endLabel)
      }
      case OrExpression(left, right) => {
        val trueLabel = new Label()
        val endLabel = new Label()

        /** coloca true na pilha se a primeira expressão já for verdadeira */
        generateExpression(left, mv)
        mv.visitJumpInsn(IFNE, trueLabel)

        /** verifica a segunda expressão */
        generateExpression(right, mv)
        mv.visitJumpInsn(IFNE, trueLabel)

        /** coloca true na pilha se ambas passaram nos testes */
        mv.visitInsn(ICONST_0)
        mv.visitJumpInsn(GOTO, endLabel)

        /** coloca false na pilha */
        mv.visitLabel(trueLabel)
        mv.visitInsn(ICONST_1)

        mv.visitLabel(endLabel)
      }
  }

  def generateBinExpression(left: Expression, right: Expression, mv: MethodVisitor, opcode: Int): Unit = {
    generateExpression(left, mv)
    generateExpression(right, mv)
    mv.visitInsn(opcode)
  }

  def generateRelExpression(left: Expression, right: Expression, mv: MethodVisitor, opcode: Int): Unit = {
    val falseLabel = new Label()
    val endLabel = new Label()

    generateExpression(left, mv)
    generateExpression(right, mv)

    /** se as expressões forem diferentes, pule para o 0 */
    mv.visitJumpInsn(opcode, falseLabel)

    /** se não, coloque 1 (true) na pilha */
    mv.visitInsn(ICONST_1)
    mv.visitJumpInsn(GOTO, endLabel)

    /** coloca 0 na pilha */
    mv.visitLabel(falseLabel)
    mv.visitInsn(ICONST_0)

    mv.visitLabel(endLabel)
  }

  def generateVariables(variables: List[VariableDeclaration], cw: ClassWriter): Unit = {
    variables.foreach((v : VariableDeclaration) =>
      v.variableType match {
        case IntegerType =>  cw.visitField(ACC_PUBLIC, v.name, "I", null, Integer.valueOf(0)).visitEnd()
        case BooleanType => cw.visitField(ACC_PUBLIC, v.name, "Z", null, false).visitEnd()
      }
    )
  }

  def generateConstants(constants: List[Constant], cw: ClassWriter): Unit = {
    val interpreter = new Interpreter()

    val visitor = new EvalExpressionVisitor(interpreter)

    constants.map {
      case (constant) => 
        val v = constant.exp.accept(visitor)

        v match {
          case IntValue (value) => {
            cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "I", null, value).visitEnd();
          }
          case BoolValue (value) => {
            cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "Z", null, value).visitEnd();
          }
        }
    }
  }

  /*
   * Generate the body of a main method. This implementation
   * is just a draft, in order to help the students to
   * output correct JVM byte code.
   *
   * TODO: This method should pattern match on
   *  the `get` statement, and then generate the
   *  correct byte code instruction. Currently,
   *  it only generates the equivalent to the
   *  infamous System.out.println("Hello world").
   *
   * TODO: Perhaps we should create a Wrapper around this
   *  ASM library. It is still too low level. A fluent
   *  API might be an interesting choice.
   */
  def generateMainMethod(cw: ClassWriter): Unit = {
    //
    // method declaration:
    //  public static void main(String[])
    //
    val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null)

    mv.visitCode()  // the method has a body... think something like '{'

    //
    // loads the static "out" field of java.lang.System and
    // pushes it into the stack.
    //
    mv.visitFieldInsn(GETSTATIC, Type.getInternalName(classOf[System]), "out", Type.getDescriptor(classOf[PrintStream]))

    //
    // loads the String constant "Hello world" and
    // pushes it into the stack.
    //
    // mv.visitLdcInsn("Hello world")

    generateExpression(new AndExpression(new OrExpression(new BoolValue(false), new BoolValue(true)), new BoolValue(true)), mv)

    //
    // we make a call to the println method of the PrintStream
    // class. Since this method expects one argument, it
    // pops out the "Hello World" constant. Since this
    // is not a static method, the object "out" is also
    // popped out from the stack.
    //
    mv.visitMethodInsn(INVOKEVIRTUAL,                // we have different invoke instructions
      Type.getInternalName(classOf[PrintStream]),    // the base class of the method
      "println",                              // the name of the method.
      Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(classOf[Boolean])), // the method descriptor
      false)                               // if this method comes from an interface

    //
    // the return instruction. note, even void method
    // must call a return.
    //
    mv.visitInsn(RETURN)

    // please, read Section 3.2 of the ASM tutorial
    mv.visitMaxs(2, 0) // it also closes the '}'

    mv.visitEnd()
  }


}

