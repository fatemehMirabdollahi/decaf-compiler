.text
.globl main
func:
    lw $t3, 0($sp)
    sw $t3, 0($t0)
    addi $sp, $sp, 4
    lw $t3, 0($t1)
    add $t3, $t0, $t3
    lw $a0, 0($t3)
    li $v0, 4
    syscall 
    jr $ra
main:
    la $t0, address
    la $t1, temp
    la $t3, str0
    sw $t3, 0($t1)
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    jal func
    lw $ra, 0($sp)
    addi $sp, $sp, 4
.data
    address: .space 68
    temp: .space 4
    buffer: .space 64
str0: .asciiz "dasd"
