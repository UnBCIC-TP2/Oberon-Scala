package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.ast.{Type, UserDefinedType, _}
import org.objectweb.asm.{Type => ASMType, _}
import org.objectweb.asm.Opcodes._

import java.io.PrintStream
import java.util.Base64

object JVMCodeGenerator extends CodeGenerator {

  override def generateCode(module: OberonModule): String = {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    cw.visit(V1_5, ACC_PUBLIC, module.name, null, "java/lang/Object", null);

    generateConstants(module.constants, cw)
    generateVariables(module.variables, cw)
    generateProcedures(module.procedures, module.userTypes, cw)

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

  def getUserDefinedTypeDescriptorValue(name: String, userTypes: List[UserDefinedType]): String = {
    // TODO: refactor -> fold left
    var descriptor = ""
    userTypes.foreach((uType: UserDefinedType) => {
      uType match {
        case recordType: RecordType =>
          if (recordType.name == name) descriptor += "Ljava/lang/" + name + ";"
        case arrayType: ArrayType =>
          if (arrayType.name == name) descriptor += "[" + getTypeDescriptorValue(arrayType.variableType, userTypes.filter(_ != uType))
      }
    })
    descriptor
  }

  def getTypeDescriptorValue(t: Type, userTypes: List[UserDefinedType]): String = {
    t match {
      case IntegerType => ASMType.INT_TYPE.toString
      case RealType => ASMType.DOUBLE_TYPE.toString
      case BooleanType => ASMType.BOOLEAN_TYPE.toString
      case CharacterType => ASMType.CHAR_TYPE.toString
      case StringType => "Ljava/lang/String;"
      case UndefinedType => ASMType.VOID_TYPE.toString
      case t: ReferenceToUserDefinedType => getUserDefinedTypeDescriptorValue(t.name, userTypes)
    }
  }

  def generateProcedures(procedures: List[Procedure], userTypes: List[UserDefinedType], cw: ClassWriter): Unit = {
    procedures.foreach((p: Procedure) => {
      var argumentTypes = ""

      p.args.foreach((t: FormalArg) => {
        val argumentType = getTypeDescriptorValue(t.argumentType, userTypes)
        argumentTypes += argumentType;
      })
      val returnDescriptorArgumentType = getTypeDescriptorValue(p.returnType.get, userTypes)
      val descriptor = "(" + argumentTypes + ")" + returnDescriptorArgumentType

      cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, p.name, descriptor, null, null)

    })
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
    mv.visitFieldInsn(GETSTATIC, ASMType.getInternalName(classOf[System]), "out", ASMType.getDescriptor(classOf[PrintStream]))

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
    mv.visitMethodInsn(INVOKEVIRTUAL,                // we have different invoke instructions
      ASMType.getInternalName(classOf[PrintStream]),    // the base class of the method
      "println",                              // the name of the method.
      ASMType.getMethodDescriptor(ASMType.VOID_TYPE, ASMType.getType(classOf[String])), // the method descriptor
      false)                               // if this method comes from an interface

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
