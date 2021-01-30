.text
.globl main
main:
    la $t0, address
    la $t1, temp
    li $t3, 64
    sw $t3, 0($t1)
    li $v0, 8
    la $a0, 64($t0)
    li $a1, 64
    syscall
    lw $a0, 0($t0)
    li $v0, 4
    syscall
.data
    address: .space 128
    temp: .space 4
