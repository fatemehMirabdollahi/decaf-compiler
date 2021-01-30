.text
.globl main
sum:
    lw $t3, 0($sp)
    sw $t3, 0($t0)
    addi $sp, $sp, 4
    lw $a0, 0($t0)
    li $v0, 1
    syscall 
    li $v0, 4
    la $a0, nl
    syscall 
    jr $ra
main:
    la $t0, address
    la $t1, temp
    li $t3, 8
    sw $t3, 0($t1)
    li $t3, 1
    li $t4, 4
    mult $t4, $t3
    mflo $t3
    lw $t4, 0($t0)
    add $t3, $t3, $t4
    sw $t3, 4($t1)
    add $t3, $t3, $t0
    lw $t4, 0($t3)
    sw $t4, 0($t1)
    lw $t8, 4($t1)
    add $t8, $t8, $t0
    li $t3, 213
    sw $t3, 0($t8)
    li $t3, 1
    li $t4, 4
    mult $t4, $t3
    mflo $t3
    lw $t4, 0($t0)
    add $t3, $t3, $t4
    sw $t3, 4($t1)
    add $t3, $t3, $t0
    lw $t4, 0($t3)
    sw $t4, 0($t1)
    addi $sp, $sp, -16
    lw $t3, 0($t0)
    sw $t3, 8($sp)
    lw $t3, 0($t0)
    sw $t3, 12($sp)
    sw $ra, 4($sp)
    lw $t3, 0($t1)
    sw $t3, 0($sp)
    jal sum
    lw $t3, 4($sp)
    sw $t3, 0($t0)
    lw $t3, 8($sp)
    sw $t3, 0($t0)
    lw $ra, 0($sp)
    addi $sp, $sp, 12
.data
    address: .space 12
    temp: .space 8
    buffer: .space 64
nl: .asciiz "\n"