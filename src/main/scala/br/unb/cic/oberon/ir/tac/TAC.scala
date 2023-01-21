package br.unb.cic.oberon.ir.tac


class TAC(label: String){}



class BinOp(s1: Address, s2: Address, dest: Address, label: String) extends TAC(label) {}

case class AddOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class SubOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class MulOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class DivOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class RemOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class AndOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class OrOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class SLTOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}

case class SLTUOp(s1: Address, s2: Address, dest: Address, label: String) extends BinOp(s1, s2, dest, label) {}



class UniOp(s1: Address, dest: Address, label: String) extends TAC(label) {}

case class NegOp(s1: Address, dest: Address, label: String) extends UniOp(s1, dest, label) {}

case class NotOp(s1: Address, dest: Address, label: String) extends UniOp(s1, dest, label) {}



case class CopyOp(s1: Address, dest: Address, label: String) extends TAC(label) {}



case class Jump(destLabel: String, label: String) extends TAC(label) {}



case class JumpTrue(s1: Address, destLabel: String, label: String) extends TAC(label) {}

case class JumpFalse(s1: Address, destLabel: String, label: String) extends TAC(label) {}



class JumpCond(s1: Address, s2: Address, destLabel: String, label: String) extends TAC(label) {}

case class LTEJump(s1: Address, s2: Address, destLabel: String, label: String) extends JumpCond(s1, s2, destLabel, label)

case class LTJump(s1: Address, s2: Address, destLabel: String, label: String) extends JumpCond(s1, s2, destLabel, label)

case class GTEJump(s1: Address, s2: Address, destLabel: String, label: String) extends JumpCond(s1, s2, destLabel, label)

case class GTJump(s1: Address, s2: Address, destLabel: String, label: String) extends JumpCond(s1, s2, destLabel, label)

case class EqJump(s1: Address, s2: Address, destLabel: String, label: String) extends JumpCond(s1, s2, destLabel, label)

case class NeqJump(s1: Address, s2: Address, destLabel: String, label: String) extends JumpCond(s1, s2, destLabel, label)



case class Param(s1: Address, label: String) extends TAC(label) {}

case class Call(procLabel: String, n: Int, label: String) extends TAC(label) {}

case class Return(s1: Address, label: String) extends TAC(label) {}



case class ListGet(list: Address, index: Address, dest: Address, label: String) extends TAC(label) {}

case class ListSet(s1: Address, index: Address, listDest: Address, label: String) extends TAC(label) {}



case class SetPointer(s1: Address, destPointer: Address, label: String) extends TAC(label) {}

case class GetValue(sPointer: Address, dest: Address, label: String) extends TAC(label) {}

case class SetValue(s1: Address, destPointer: Address, label: String) extends TAC(label) {}
