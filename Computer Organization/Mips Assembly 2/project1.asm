.data


message_mainmenu: .asciiz "\nMain Menu:\n1. Prim's Algorithm\n2. Number Series\n3. Encrypt/Decrypt\n4.Exit\nPlease select an option: "
message_graph: .asciiz "Enter the graph: "

message_firstnumber: .asciiz "\nEnter the first number in the series: "
message_numberofintegers: .asciiz "Enter the number of integers in the series: "
message_offset: .asciiz "Enter the offset between two successive integers in the series: "
message_theseries: .asciiz "\nThe series is: "
message_sum: .asciiz "\nThe summation of the numbers is "
space: .asciiz " "
new_line: .asciiz "\n"
msp: .asciiz "Minimum Spanning Tree:\n"


message_inputstring: .asciiz "Enter an input string: "
message_offssetvalue: .asciiz "Enter an offset value: "
message_source: .asciiz "SOURCE: "
message_processed: .asciiz "PROCESSED: "

message_prims: .asciiz "Total weight is "

print_node: .space 2		# print a single node
trash_array1: .space 1
node_names: .space 50		# all nodes in the graph
trash_array2: .space 1
min_span_tree: .word 20		# nodes in minimum spanning tree with added order
trash_array3: .space 1
input_string: .space 1890	
trash_array4: .space 1
weight_array: .word 260		# 2d array that keeps all weights in the graph
trash_array5: .space 1

.text

main:
	
	li $v0, 4		# print main menu
	la $a0, message_mainmenu
	syscall			# take option from user
	li $v0, 5
	syscall
	add $s0, $zero, $v0	# write selected option to t0
	beq $s0, 1, program1
	beq $s0, 2, program2
	beq $s0, 3, program3
	beq $s0, 4, exit
	j main			# jump back to main 
	
program1:
	add $s1, $zero, $zero
	add $s0, $zero, $zero
	
	# Load addresses
	la $s7, input_string
	la $s6, node_names
	la $s5, weight_array
	la $s4, min_span_tree
	

	li $v0, 4			# print input message
	la $a0, message_graph
	syscall
	
	li $v0, 8			# read string
	la $a0, input_string
	li $a1, 1000
	syscall
	
	##################################################################
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	
	add $a0, $s7, $zero
	add $a1, $s6, $zero
	jal analyze_input			# fill node names and return number of nodes
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	add $s0, $v0, $zero			# keep number of nodes in $s0
	
	##################################################################
	
	addi $sp, $sp, -16
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	sw $a2, 8($sp)
	sw $a3, 12($sp)
	add $a0, $s7, $zero
	add $a1, $s6, $zero
	add $a2, $s5, $zero
	add $a3, $s0, $zero
	jal fill_weight_array			# fill weight_array
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	lw $a2, 8($sp)
	lw $a3, 12($sp)
	addi $sp, $sp, 16
	
	##################################################################	

	li $v0, 4
	la $a0, msp
	syscall
	
	##################################################################
	
	addi $sp, $sp, -16
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	sw $a2, 8($sp)
	sw $a3, 12($sp)
	add $a0, $s5, $zero
	add $a1, $s4, $zero
	add $a2, $s0, $zero
	add $a3, $s6, $zero
	jal prims_algorithm			# go prim's algorithm
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	lw $a2, 8($sp)
	lw $a3, 12($sp)
	addi $sp, $sp, 16
	
	add $s1, $v0, $zero
	
	li $v0, 4
	la $a0, message_prims
	syscall
	
	li $v0, 1
	la $a0, 0($s1)
	syscall
	
	##################################################################
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	add $a0, $s6, $zero
	addi $a1, $zero, 50
	jal clearString			#clear node names
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	##################################################################
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	add $a0, $s7, $zero
	addi $a1, $zero, 1890
	jal clearString			#clear input string
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	##################################################################
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	
	add $a0, $s4, $zero
	addi $a1, $zero, 20
	jal clear_array			# clear msp
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	##################################################################
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	
	add $a0, $s5, $zero
	addi $a1, $zero, 260
	jal clear_array			# clear weight array
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	##################################################################
		
	j main
	
program2:
	add $s0, $zero, $zero 		# clear $s0   ( $s0 keeps sum )
	
	li $v0, 4			# print input message
	la $a0,	message_firstnumber
	syscall
	li $v0, 5			# read the first number in the series
	syscall
	add $s1, $zero, $v0		# save input into the $s1
	
	li $v0, 4
	la $a0, message_numberofintegers   # print input message
	syscall
	
	li $v0, 5			# read number of integers in the series
	syscall
	add $s2, $zero, $v0		# save input into the $s2
	
	li $v0, 4
	la $a0, message_offset		# print input message
	syscall
	
	li $v0, 5			# read the offset between two succesive integers in the series
	syscall
	add $s3, $zero, $v0		# save input into the $s0
	
	
	li $v0, 4			# print the message
	la $a0, message_theseries
	syscall
	
	loop_program2:
		add $s0, $s0, $s1		# add number to sum
	
		li $v0, 1			# print the number
		la $a0, 0($s1)
		syscall
	
		li $v0, 4			# print space
		la $a0, space
		syscall
		
		add $s1, $s1, $s3		# increase the number by the offset 
		addi $s2, $s2, -1		# decrease the number of elements
	
		bne $s2, $zero, loop_program2	# if the number of elements is 0 end program
	
		li $v0, 4
		la $a0, message_sum		
		syscall
	
		li $v0, 1			# print sum
		la $a0, 0($s0)
		syscall 
	
	j main 
	
program3:
	la $s7, input_string		# load base address of input_string to register s7
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	add $a0, $s7, $zero
	addi $a1, $zero, 1890
	jal clearString			#clear string
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	
	li $v0, 4			# print input message
	la $a0, message_inputstring
	syscall
	
	li $v0, 8			# read string
	la $a0, input_string
	li $a1, 1000
	syscall
	
	li $v0, 4			# print input message
	la $a0, message_offssetvalue
	syscall
	
	li $v0, 5			# read offset
	syscall
	add $s0, $zero, $v0
	
	##################################################################
	
	addi $sp, $sp, -4
	sw $a0, 0($sp)
	add $a0, $s7, $zero
	jal upperLetter			# Upper case all letters in the input screen
	lw $a0, 0($sp)
	addi $sp, $sp, 4
	
	##################################################################
	li $v0, 4
	la $a0, message_source
	syscall
	
	li $v0, 4			# print input message
	la $a0, input_string
	syscall
	
	##################################################################
	
	addi $sp, $sp, -8
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	add $a0, $s7, $zero
	add $a1, $s0, $zero
	jal encrypt			# encrypt the string
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, 8
	
	##################################################################
	
	li $v0, 4
	la $a0, message_processed
	syscall
	
	li $v0, 4			# print input message
	la $a0, input_string
	syscall
	
	j main
	
exit:
	li $v0, 10
	syscall
	

encrypt:
	addi $sp, $sp, -12
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	sw $s2, 8($sp)
	add $s0, $zero, $zero
	add $s1, $zero, $zero
	add $s2, $zero, $zero
	
	slti $s1, $a1, 0
	beq $s1, 1, makePositive	
	encrypt_loop:
		
		lb $s2, 0($a0)
		slti $s1, $s2, 65
		beq $s1, 1, notLetter
		slti $s1, $s2, 91
		beq $s1, 0, notLetter
		add $s2, $s2, $a1
		slti $s1, $s2, 91
		bne $s1, 0, notOverflow
		addi $s2, $s2, -26
		
		notOverflow:			# check overflow case
		sb $s2, 0($a0)			# change the letter
		
		notLetter:			# dont change not letter chars
		addi $s0, $s0, 1
		addi $a0, $a0, 1
		bne $s0, 1890, encrypt_loop
		
		lw $s0, 0($sp)
		lw $s1, 4($sp)
		lw $s2, 8($sp)
		addi $sp, $sp, 12
		
		jr $ra
		
		makePositive:			# add 26 to value if offset is negative
			addi $a1, $a1, 26
		j encrypt_loop
			

clearString:				# clearString(String, length)
	addi $sp, $sp, -4
	sw $s0, 0($sp)
	add $s0, $zero, $zero
	
	clear_loop:
		add $t0, $s0, $a0
		sb $zero, 0($t0)
		
		addi $s0, $s0, 1
		bne $s0, $a1, clear_loop
	
	lw $s0, 0($sp)
	addi $sp, $sp, 4
	jr $ra
	
clear_array:				# clearArray(Array[], length)
	addi $sp, $sp, -4
	sw $s0, 0($sp)
	add $s0, $zero, $zero
	
	clear_array_loop:
		sll $t0, $s0, 2
		add $t0, $t0, $a0
		sw $zero, 0($a0)
		
		addi $s0, $s0, 1
		bne $s0, $a1, clear_array_loop
	
	lw $s0, 0($sp)
	addi $sp, $sp, 4
	jr $ra
	

	
upperLetter:				# if ascii code of the char is between 97 - 122 ( lower case )
	addi $sp, $sp, -8
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	add $s0, $zero, $zero
	add $s1, $zero, $zero
	
	
	upperLoop:
		lb $s1, 0($a0)
		slti $t0, $s1, 97
		bne $t0, $zero, conditionLetter
		slti $t0, $s1, 123
		beq $t0, $zero, conditionLetter
		addi $s1, $s1, -32
		sb $s1, 0($a0)
		conditionLetter:
			addi $a0, $a0, 1
			addi $s0, $s0, 1
			bne $s1, 10, upperLoop
			lw $s0, 0($sp)
			lw $s1, 4($sp)
			addi $sp, $sp, 8
			jr $ra
	
	
	# analyzes the entered input, fills the node_names array and returns number of nodes
analyze_input: 

	addi $sp, $sp, -28
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	sw $s2, 8($sp)
	sw $s3, 12($sp)
	sw $s4, 16($sp)
	sw $s5, 20($sp)
	sw $s6, 24($sp)
	
	add $s0, $zero, $zero
	add $s1, $zero, $zero
	add $s2, $zero, $zero
	add $s3, $zero, $zero
	add $s4, $zero, $zero
	add $s5, $zero, $zero
	add $s6, $zero, $zero	
	
	analyze_loop:
		add $t7, $s0, $a0
		lb $s2, 0($t7)			# get the first char
		beq $s2, 10, end_analyze	# input is done
		beq $s2, 32, next		# white space
		
		addi $s5, $s5, 1			
		lb $s3, 1($t7)			# get the second char
		beq $s3, 10, end_analyze	# input is done
		beq $s5, 3, passLoop		# pass the weight
		
		# dont add the node to node_names if already exists
		compareLoop:
		add $t6, $s1, $a1
		lb $s4, 0($t6)
		beq $s4, 0, addNode		# node is not in node_names, then add it
		beq $s4, 32, next2		# white space
		
		bne $s2, $s4, notEqual
		lb $s4, 1($t6)
		bne $s3, $s4, notEqual
		addi $s0, $s0, 1
		add $s1, $zero, $zero
		beq $s3, 32, analyze_loop	
		addi $s0, $s0, 1
		
		j analyze_loop
		
		# if nodes are not equal, check the next one
		notEqual:
		addi $s1, $s1, 1
		beq $s4, 0, compareLoop
		beq $s4, 32, compareLoop
		addi $s1, $s1, 1
		j compareLoop
		
		# go 2 chars
		next2:
		addi $s1, $s1, 1
		j compareLoop
		
		# go 1 char in case of chr = white_space
		next:
		addi $s0, $s0, 1
		j analyze_loop
		
		# pass the weight
		passLoop:
		add $s5, $zero, $zero
		addi $s0, $s0, 1
		beq $s3, 32, analyze_loop
		addi $s0, $s0, 1
		j analyze_loop
		
		# add new node to the end of node_names array
		addNode:
		add $s1, $zero, $zero
		addi $s0, $s0, 1
		sb $s2, 0($t6)
		beq $s3, 32, end_add_node
		addi $t6, $t6, 1
		addi $s0, $s0, 1
		sb $s3, 0($t6)
		end_add_node:
		addi $t1, $zero, 32
		addi $t6, $t6, 1
		sb $t1, 0($t6)
		addi $s6, $s6, 1
		
		j analyze_loop
		
		end_analyze:
		add $v0, $s6, $zero
		
		lw $s0, 0($sp)
		lw $s1, 4($sp)
		lw $s2, 8($sp)
		lw $s3, 12($sp)
		lw $s4, 16($sp)
		lw $s5, 20($sp)
		lw $s6, 24($sp)
		addi $sp, $sp, 28
		jr $ra

# add weights of all edges in the graph
fill_weight_array:

	addi $sp, $sp, -24
	sw $s0, 0($sp)
	sw $s3, 4($sp)
	sw $s4, 8($sp)
	sw $s5, 12($sp)
	sw $s6, 16($sp)
	sw $s7, 20($sp)
		
	add $s0, $zero, $zero
	add $s3, $zero, $zero
	add $s4, $zero, $zero
	add $s5, $zero, $zero
	add $s6, $zero, $zero
	add $s7, $zero, $zero
	
	# every element of weight_array = -1
	addi $t1, $zero, -1
	negative_loop:
		
		mul $t0, $a3, $s7
		add $t0, $t0, $s6
		sll $t0, $t0, 2
		add $t0, $t0, $a2
		
		sw $t1, 0($t0)
		
		addi $s7, $s7, 1
		bne $s7, $a3, negative_loop
		addi $s6, $s6, 1
		add $s7, $zero, $zero
		bne $s6, $a3, negative_loop
		# negativize is done
		
		get_element_loop:
		add $t7, $s0, $a0
		lb $s3, 0($t7)
		beq $s3, 10, end_fill_weight_array
		beq $s3, 0, end_fill_weight_array
		beq $s3, 32, next_element
		
		addi $s5, $s5, 1
		lb $s4, 1($t7)
		beq $s5, 3, add_weight
		
		add $sp, $sp, -16
		sw $ra, 0($sp)
		sw $a0, 4($sp)
		sw $a1, 8($sp)
		sw $a2, 12($sp)
		
		###################################################################
		
		add $a0, $a1, $zero
		add $a1, $s3, $zero
		add $a2, $s4, $zero
		jal get_index			# returns index of the node
		lw $ra, 0($sp)
		lw $a0, 4($sp)
		lw $a1, 8($sp)
		lw $a2, 12($sp)
		add $sp, $sp, 16
				
		###################################################################
		
		beq $s5, 1, index_i
		add $s7,$v0,$zero	# index j
		j index_j
		index_i:
		add $s6,$v0,$zero	# index i
		index_j:
		
		add $s0, $s0, 1
		next_element:
		add $s0, $s0, 1
		j get_element_loop
		
		add_weight:
		mul $t0, $a3, $s7
		add $t0, $t0, $s6
		sll $t0, $t0, 2
		add $t0, $t0, $a2
		
		addi $s3, $s3, -48
		# convert to int
		beq $s4, 32, one_digit
		beq $s4, 10, one_digit
		# 2 digits
		addi $s4, $s4, -48
		mul $s3, $s3, 10
		add $s3, $s3, $s4
		one_digit:
		# weight_array[i][j] = weight
		sw $s3, 0($t0)
		
		mul $t0, $a3, $s6
		add $t0, $t0, $s7
		sll $t0, $t0, 2
		add $t0, $t0, $a2
		
		# weight_array[j][i] = weight
		sw $s3, 0($t0)
		
		add $s5, $zero, $zero
		add $s0, $s0, 2
		j get_element_loop
		
		end_fill_weight_array:
		
		lw $s0, 0($sp)
		lw $s3, 4($sp)
		lw $s4, 8($sp)
		lw $s5, 12($sp)
		lw $s6, 16($sp)
		lw $s7, 20($sp)
		addi $sp, $sp, 24
		jr $ra
	
# returns index of the node	
get_index:
	addi $sp, $sp, -8
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	add $s0, $zero, $zero
	add $s1, $zero, $zero
	
	index_label:
		add $t0, $a0, $s0
		lb $t1, 0($t0)
		
		beq $t1, 32, increment_one
		
		bne $a1, $t1, increment_two 
		lb $t1, 1($t0)
		bne $a2, $t1, increment_two
		
		# return_index
		add $v0, $s1, $zero
		
		lw $s0, 0($sp)
		lw $s1, 4($sp)
		addi $sp, $sp, 8
		jr $ra
		
		increment_two:
		add $s0, $s0, 1
		add $s1, $s1, 1
		increment_one:
		add $s0, $s0, 1
		j index_label
		
# fill the minimum spanning tree and print all edges in msp
prims_algorithm:
	
	addi $sp, $sp, -32
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	sw $s2, 8($sp)
	sw $s3, 12($sp)
	sw $s4, 16($sp)
	sw $s5, 20($sp)
	sw $s6, 24($sp)
	sw $s7, 28($sp)
		
	add $s0, $zero, $zero
	add $s1, $zero, $zero
	add $s2, $zero, $zero
	add $s3, $zero, $zero
	add $s4, $zero, $zero
	add $s5, $zero, $zero
	add $s6, $zero, $zero
	add $s7, $zero, $zero
	
	
	addi $sp, $sp, 8
	
	sw $a0, 0($sp)
	sw $a1, 4($sp)
	
	#li $v0, 42				# generate random integer between number of nodes and 0
	#la $a1, 0($a2)				# does not work on QTSPIM, so we took the first node
	#syscall
	
	add $s2, $zero, $zero
	
	lw $a0, 0($sp)
	lw $a1, 4($sp)
	addi $sp, $sp, -8
	
	add $t1, $a1, $s1
	sw $s2, 0($t1)					# add random node
	addi $s5, $s5, 1
	
	add $s2, $zero, $zero
	
	msp_loop:
	beq $s5, $a2, return_prims
	add $s1, $zero, $zero
	addi $s7, $zero, 100				# weight can not be more than 99
	min_weight_loop:
		add $s0, $zero, $zero
		sll $t1, $s1, 2
		add $t1, $t1, $a1			# load msp node name
		lw $s3, 0($t1)
		
		find_min:
			mul $t0, $a2, $s3
			add $t0, $t0, $s0
			sll $t0, $t0, 2
			add $t0, $t0, $a0
			
			lw $s4, 0($t0)
			beq $s4, -1, next_weight	# edge does not exist
			
			slt $t3, $s4, $s7
			beq $t3, 0, next_weight
			add $s7, $s4, $zero			 
			add $s6, $s0, $zero			
			add $t4, $s3, $zero
		
			next_weight:
			addi $s0, $s0, 1
			
			bne $s0, $a2, find_min
			 
			addi $s1, $s1, 1
			bne $s1, $s5, min_weight_loop
			
			
			addi $s5, $s5, 1		 # node found and added to msp
			
			sll $t2, $s1, 2
			add $t2, $t2, $a1
			sw $s6, 0($t2)			# add new index to msp
			add $s2, $s2, $s7		# add new weight to sum
			
			#################################################
			
			addi $sp, $sp, -12
			sw $ra, 0($sp)
			sw $a0, 4($sp)
			sw $a1, 8($sp)
			add $a0, $t4, $zero
			add $a1, $a3, $zero
			jal print_node_names			# print first node
			lw $ra, 0($sp)
			lw $a0, 4($sp)
			lw $a1, 8($sp)
			addi $sp, $sp, 12
			
			################################################
			
			addi $sp, $sp, -12
			sw $ra, 0($sp)
			sw $a0, 4($sp)
			sw $a1, 8($sp)
			add $a0, $s6, $zero
			add $a1, $a3, $zero
			jal print_node_names			# print second node
			lw $ra, 0($sp)
			lw $a0, 4($sp)
			lw $a1, 8($sp)
			addi $sp, $sp, 12
			
			################################################
			
			addi $sp, $sp, -4
			sw $a0, 0($sp)
			
			li $v0, 1
			la $a0, 0($s7)
			syscall					# print weight
			
			li $v0, 4
			la $a0, new_line
			syscall					# print("\n")
			
			lw $a0, 0($sp)
			addi $sp, $sp, 4

			################################################
			
			addi $sp, $sp, -20
			sw $ra, 0($sp)
			sw $a0, 4($sp)
			sw $a1, 8($sp)
			sw $a2, 12($sp)
			sw $a3, 16($sp)
			add $a0, $a0, $zero
			add $a1, $a1, $zero
			add $a2, $a2, $zero
			add $a3, $s1, $zero
			jal delete_edges			# delete all weights of edges that is added to msp with all previous nodes
			lw $ra, 0($sp)
			lw $a0, 4($sp)
			lw $a1, 8($sp)
			lw $a2, 12($sp)
			lw $a3, 16($sp)
			addi $sp, $sp, 20
			
			################################################
			
			j msp_loop

return_prims:
	
	add $v0, $s2, $zero	# return total weight
	
	lw $s0, 0($sp)
	lw $s1, 4($sp)
	lw $s2, 8($sp)
	lw $s3, 12($sp)
	lw $s4, 16($sp)
	lw $s5, 20($sp)
	lw $s6, 24($sp)
	lw $s7, 28($sp)
	
	addi $sp, $sp, 32
	
	jr $ra

# delete the edges between added node and all previous nodes in msp
delete_edges:

	addi $sp, $sp, -16
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	sw $s2, 8($sp)
	sw $s3, 12($sp)

	addi $s0, $a3, -1 
	addi $s3, $zero, -1		# s3 = -1
	sll $t0, $a3, 2
	add $t0, $t0, $a1
	lw $s1,	0($t0)
	
	delete_loop:
		sll $t0, $s0, 2
		add $t0, $t0, $a1
		lw $s2, 0($t0)
		
		mul $t0, $s1, $a2
		add $t0, $t0, $s2
		sll $t0, $t0, 2
		add $t0, $t0, $a0
		
		sw $s3, 0($t0)
		
		mul $t0, $s2, $a2
		add $t0, $t0, $s1
		sll $t0, $t0, 2
		add $t0, $t0, $a0
		
		sw $s3, 0($t0)
		
		addi $s0, $s0, -1
		
		bne $s0, -1, delete_loop
		lw $s0, 0($sp)
		lw $s1, 4($sp)
		lw $s2, 8($sp)
		lw $s3, 12($sp)
		addi $sp, $sp, 16
		
		jr $ra
	
	# print name of a single node
	# p(i){
	# printf(nde_names[i]);
print_node_names:
	addi $sp, $sp, -16
	sw $s0, 0($sp)
	sw $s1, 4($sp)
	sw $s2, 8($sp)
	sw $s7, 12($sp)
	add $s0, $zero, $zero
	add $s1, $zero, $zero
	add $s2, $zero, $zero
	
	la $s7, print_node
		
	print_node_loop:
	
	add $t0, $a1, $s2
	lb $s0, 0($t0)
	beq $s0, 32, print_inc_one
	
	lb $s1, 1($t0)
	
	addi $a0, $a0, -1
	addi $s2, $s2, 1
	print_inc_one:	
	addi $s2, $s2, 1
	
	bne $a0, -1, print_node_loop
	sb $zero, 0($s7)
	sb $zero, 1($s7)

	sb $s0, 0($s7)
	
	beq $s1, 32, return_print
	sb $s1, 1($s7)
	
	return_print:
	li $v0, 4
	la $a0, print_node		# print the name of the node
	syscall
	
	li $v0, 4
	la $a0, space			# print white space
	syscall

	lw $s0, 0($sp)
	lw $s1, 4($sp)
	lw $s2, 8($sp)
	lw $s7, 12($sp)
	addi $sp, $sp, 16
	jr $ra
