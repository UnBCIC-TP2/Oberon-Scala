package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Type => OberonType, _}
import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

object JimpleCodeGenerator extends CodeGenerator[ClassOrInterfaceDeclaration] {
    override def generateCode(module: OberonModule): ClassOrInterfaceDeclaration = {
        ClassDecl(
            modifiers = List(PublicModifer),
            classType = TObject(module.name),
            superClass = TObject("java.lang.Object"),
            interfaces = List.empty[Type],
            fields = List.empty[Field],
            methods = List.empty[Method]
        )
    }

    def generateConstants(module: OberonModule): List[Field] = {
        val visitor = new ExpressionTypeVisitor(new TypeChecker())

        module.constants.map(constant => Field(
            modifiers = List(PublicModifer, FinalModifier),
            fieldType = jimpleType(visitor.visitExpression(constant.exp), module),
            name = constant.name
        ))
    }

    def generateVariables(module: OberonModule): List[Field] = ???

    def generateUserDefinedTypes(module: OberonModule): List[Type] =
        module.userTypes.map(userType => jimpleUserDefinedType(userType.name, module))

    def generateMethodSignatures(module: OberonModule): List[MethodSignature] = ???

    def jimpleType(oberonType: Option[OberonType], module: OberonModule): Type = oberonType match {
        case Some(someType) => jimpleType(someType, module)
        case None => TVoid
    }

    def jimpleType(oberonType: OberonType, module: OberonModule): Type = oberonType match {
        case IntegerType => TInteger
        case RealType => TFloat
        case BooleanType => TBoolean
        case CharacterType => TCharacter
        case StringType => TString
        case UndefinedType => TUnknown
        case NullType => TNull

        case ReferenceToUserDefinedType(name) => jimpleUserDefinedType(name, module)

        case _ => throw new Exception("Non-exhaustive match in case statement.")
    }

    def jimpleUserDefinedType(name: String, module: OberonModule): Type =
        jimpleUserDefinedType(module.userTypes.find(userType => userType.name == name).get, module)

    def jimpleUserDefinedType(userType: UserDefinedType, module: OberonModule): Type = userType.baseType match {
        case RecordType(_) => TObject(userType.name)
        case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

        case _ => throw new Exception("Non-exhaustive match in case statement.")
    }
}