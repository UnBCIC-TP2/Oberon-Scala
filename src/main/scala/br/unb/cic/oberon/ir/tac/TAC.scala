package br.unb.cic.oberon.ir.tac


class TAC(label: String, next: TAC){}



class BinOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends TAC(label, next) {}

case class AddOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}

case class SubOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}

case class MulOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}

case class DivOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}

case class RemOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}

case class AndOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}

case class OrOp(s1: Address, s2: Address, dest: Address, label: String, next: TAC) extends BinOp(s1, s2, dest, label, next) {}



class UniOp(s1: Address, dest: Address, label: String, next: TAC) extends TAC(label, next) {}

case class NegOp(s1: Address, dest: Address, label: String, next: TAC) extends UniOp(s1, dest, label, next) {}

case class NotOp(s1: Address, dest: Address, label: String, next: TAC) extends UniOp(s1, dest, label, next) {}



case class CopyOp(s1: Address, dest: Address, label: String, next: TAC) extends TAC(label, next) {}



case class Jump(destLabel: String, label: String, next: TAC) extends TAC(label, next) {}



case class JumpTrue(s1: Address, destLabel: String, label: String, next: TAC) extends TAC(label, next) {}

case class JumpFalse(s1: Address, destLabel: String, label: String, next: TAC) extends TAC(label, next) {}



class JumpCond(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends TAC(label, next) {}

case class LTEJump(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends JumpCond(s1, s2, destLabel, label, next)

case class LTJump(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends JumpCond(s1, s2, destLabel, label, next)

case class GTEJump(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends JumpCond(s1, s2, destLabel, label, next)

case class GTJump(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends JumpCond(s1, s2, destLabel, label, next)

case class EqJump(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends JumpCond(s1, s2, destLabel, label, next)

case class NeqJump(s1: Address, s2: Address, destLabel: String, label: String, next: TAC) extends JumpCond(s1, s2, destLabel, label, next)



case class Param(s1: Address, label: String, next: TAC) extends TAC(label, next) {}

case class Call(procLabel: String, n: Int, label: String, next: TAC) extends TAC(label, next) {}

case class Return(s1: Address, label: String, next: TAC) extends TAC(label, next) {}



case class ListGet(list: Address, index: Address, dest: Address, label: String, next: TAC) extends TAC(label, next) {}

case class ListSet(s1: Address, index: Address, listDest: Address, label: String, next: TAC) extends TAC(label, next) {}



case class SetPointer(s1: Address, destPointer: Address, label: String, next: TAC) extends TAC(label, next) {}

case class GetValue(sPointer: Address, dest: Address, label: String, next: TAC) extends TAC(label, next) {}

case class SetValue(s1: Address, destPointer: Address, label: String, next: TAC) extends TAC(label, next) {}
