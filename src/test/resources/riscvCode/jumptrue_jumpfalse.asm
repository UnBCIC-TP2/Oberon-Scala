.data
.text

li x5,2
add x8,x5,x0
beq x8,x0,.L0
li x5,6
add x9,x5,x0
bne x8,x0,.L1
.L0:
and x10,x8,x9
.L1:

