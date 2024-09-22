package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._
import org.objectweb.asm._
import org.objectweb.asm.Opcodes._

import java.io.PrintStream
import java.util.Base64

object JVMCodeGenerator extends CodeGenerator[String] {

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

  def generateVariables(
      variables: List[VariableDeclaration],
      cw: ClassWriter
  ): Unit = {
    variables.foreach((v: VariableDeclaration) =>
      v.variableType match {
        case IntegerType =>
          cw.visitField(ACC_PUBLIC, v.name, "I", null, Integer.valueOf(0))
            .visitEnd()
        case BooleanType =>
          cw.visitField(ACC_PUBLIC, v.name, "Z", null, false).visitEnd()
        case _ => throw new Exception("Non-exhaustive match in case statement.")
      }
    )
  }

  def generateConstants(constants: List[Constant], cw: ClassWriter): Unit = {
    val interpreter = new Interpreter()
    val env = new Environment[Expression]()

    constants.map {
      case (constant) => 
        val v = interpreter.evalExpression(constant.exp).runA(env).value

      v match {
        case IntValue(value) => {
          cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "I", null, value)
            .visitEnd();
        }
        case BoolValue(value) => {
          cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "Z", null, value)
            .visitEnd();
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
    val mv = cw.visitMethod(
      ACC_PUBLIC + ACC_STATIC,
      "main",
      "([Ljava/lang/String;)V",
      null,
      null
    )

    mv.visitCode() // the method has a body... think something like '{'

    //
    // loads the static "out" field of java.lang.System and
    // pushes it into the stack.
    //
    mv.visitFieldInsn(
      GETSTATIC,
      Type.getInternalName(classOf[System]),
      "out",
      Type.getDescriptor(classOf[PrintStream])
    )

    //
    // loads the String constant "Hello world" and
    // pushes it into the stack.
    //
    mv.visitLdcInsn("Hello world")

    //
    // we make a call to the println method of the PrintStream
    // class. Since this method expects one argument, it
    // pops out the "Hello World" constant. Since this
    // is not a static method, the object "out" is also
    // popped out from the stack.
    //
    mv.visitMethodInsn(
      INVOKEVIRTUAL, // we have different invoke instructions
      Type.getInternalName(
        classOf[PrintStream]
      ), // the base class of the method
      "println", // the name of the method.
      Type.getMethodDescriptor(
        Type.VOID_TYPE,
        Type.getType(classOf[String])
      ), // the method descriptor
      false
    ) // if this method comes from an interface

    //
    // the return instruction. note, even void method
    // must call a return.
    //
    mv.visitInsn(RETURN)

    // please, read Section 3.2 of the ASM tutorial
    mv.visitMaxs(0, 0) // it also closes the '}'

    mv.visitEnd()
  }

}
