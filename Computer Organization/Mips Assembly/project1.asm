.data
heapArray: .space 400 #define array with 100 elements
mdArray: .space 400
string: .space 150
prg2Str: .space 100
addrOfmdArray: .space 16
message0: .asciiz "\nMain Menu:\n1.Build a min-heap\n2.Evaluate an expression\n3.Construct a 2D array\n4.Exit\nPlease select an option: "
message1: .asciiz "\nChoose operation:\n1.Insert an item to the heap.\n2.Delete an item from the heap.\n3.Print the heap.\n4.Return back\nPlease select an option:"
message2: .asciiz "\nPlease enter the value of item:"
message3: .asciiz "Deleting smallest value.\n"
message4: .asciiz "Heap is Full!\n"
message5: .asciiz "The value of "
message6: .asciiz " is placed at location "
message7: .asciiz " of the heap array!\n"
NewLine: .asciiz  "\n"
message8: .asciiz  "The size of heap is "
x: .asciiz "X"
space: .asciiz  "     "
dash: .asciiz " - "
openBigPrnts: .asciiz "["
closeBigPrnts: .asciiz "] "			# this string holds base address of 2D array in hex form
message100: .asciiz "\nEnter the input string: " 
message200: .asciiz "\nEnter the number of rows: "
message300: .asciiz "The 2D array is:\n"
message400: .asciiz "\n\nEnter the row index: "
message500: .asciiz "Enter the column index: "
message600: .asciiz "The beginning address of the 2D is 0x"
message700: .asciiz "\nThe memory address of the cell Array["
message800: .asciiz "]["
message900: .asciiz "] is 0x"
message1000: .asciiz "\nThe Min Value between Array[0][0] and Array["
message1100: .asciiz "\nThe Max Value between Array[0][0] and Array["
message1200: .asciiz "] is: "
newline: .asciiz "\n"
spaceChar: .asciiz " "
zeroChar: .asciiz "0"
oneChar: .asciiz "1"
twoChar: .asciiz "2"
threeChar: .asciiz "3"
fourChar: .asciiz "4"
fiveChar: .asciiz "5"
sixChar: .asciiz "6"
sevenChar: .asciiz "7"
eightChar: .asciiz "8"
nineChar: .asciiz "9"
aChar: .asciiz "A"
bChar: .asciiz "B"
cChar: .asciiz "C"
dChar: .asciiz "D"
eChar: .asciiz "E"
fChar: .asciiz "F"

.text
	addi $t1, $zero, 0
	sw $t1, heapArray($t1) #added 0 to first index of array
main:
	li $v0, 4
	la $a0, message0
	syscall
	li $v0, 5
	syscall
	add $t0, $zero, $v0 #writing input to t0
	addi $t1, $zero, 1  #compare with 1
	bne $t0, $t1, CheckPrg2 #if input is not equal to 1, go to else if statement L1
	jal program1
	addi $t1, $t1, -1 #resetting to 0
	j Restart #after if statement is done, go to outside of it.
CheckPrg2:
	addi $t1, $zero, 2  #t1 equals 2 now
	bne $t0, $t1, CheckPrg3 #if input is not equal to 2, go to else statement L2
	jal program2
	addi $t1, $t1, -2 #resetting to 0
	j Restart #after this condition done, go outside of if-else statement
CheckPrg3:	
	addi $t1, $zero, 3  #t1 equals 2 now
	bne $t0, $t1, CheckExit #if input is not equal to 2, go to else statement L2
	jal program3
	addi $t1, $t1, -3 #resetting to 0
	j Restart #after this condition done, go outside of if-else statement
CheckExit:
	addi $t1, $zero, 4 #exit
	beq $t0, $t1, AfterRestart
Restart:	
	j main
AfterRestart:
	li $v0, 10
	syscall
	
program1:
	
   main2:
	#printing welcome message
	li $v0, 4
	la $a0, message1
	syscall
	#getting input
	li $v0, 5
	syscall
	add $t0, $zero, $v0 #writing input to t0
	addi $t1, $zero, 1  #compare with 1
	bne $t0, $t1, Label1 #if input is not equal to 1, go to else if statement L1
	addi $sp, $sp, -4		#save $ra
	sw $ra, ($sp)
	jal insert
	
	lw $ra, ($sp)
	addi $sp, $sp, 4
	addi $t1, $t1, -1 #resetting to 0
	j Label4 #after if statement is done, go to outside of it.
	
	Label1:
	addi $t1, $zero, 2  #t1 equals 2 now
	bne $t0, $t1, Label2 #if input is not equal to 2, go to else statement L2
	addi $sp, $sp, -4		#save $ra
	sw $ra, ($sp)
	jal delete
	lw $ra, ($sp)
	addi $sp, $sp, 4
	
	addi $t1, $t1, -2 #resetting to 0
	j Label4 #after this condition done, go outside of if-else statement
	
	Label2:
	addi $t1, $zero, 3  #t1 equals 2 now
	bne $t0, $t1, Label3
	la $s6, heapArray
	lw $s1, ($s6)
	addi $s6, $s6, 4
	addi $t1, $zero, 0
	addi $t2, $zero, 1
	addi $t3, $zero, 0
	addi $t4, $zero, 0
	addi $t5, $zero, 0
	
	li $v0, 4
	la $a0, message8	# print the size of heap
	syscall
	
	li $v0, 1
	la $a0, ($s1)		# value of size of heap
	syscall
	
	li $v0, 4
	la $a0, NewLine		# new line 
	syscall
	addi $sp, $sp, -4		#save $ra
	sw $ra, ($sp)
	
	jal PrintHeap
	lw $ra, ($sp)
	addi $sp, $sp, 4
	
	addi $t1, $t1, -3 #resetting to 0
	j Label4
	
	Label3:
	addi $t1, $zero, 4 #exit
	beq $t0, $t1, Label5
Label4:	
	j main2
Label5:
	jr $ra
		
insert:
	lw $t2, heapArray #number of elements in heap
	la $a1, heapArray #loaded adress of heap to s0
	addi $t3, $zero, 100 #max num of elements
	beq $t2, $t3, returnFull #if equal to max number of elements return
	addi $t3, $t3, -100
	#printing message
	li $v0, 4
	la $a0, message2
	syscall
	#getting input value
	li $v0, 5
	syscall
	add $s1, $zero, $v0 #writing input to s1
	addi $t3, $t2, 1 #next index to add
	sll $t3, $t3, 2 #index shifted by 2
	add $t3, $a1, $t3 #the address next value going to added
	sw $s1, 0($t3) #new value added to array
	add $s1, $zero, $zero #cleared s1
	addi $t2, $t2, 1 # increased number of elements in array
	sw $t2, 0($a1) #stored updated value of num of elements in array
	add $t3, $zero, $zero #cleared t3 register
	add $t3, $t2, $zero # now t3 equals to index of last added element 
loop:	
	#checking for heap property
	li $t4, 1 # base case
	beq $t3, $t4, done
	andi $t5, $t3, 1 #checking for even and odd
	sub $t5, $t3, $t5 #if it is odd, t3 will be even after this, if it is even it does not change
	sll $t6, $t5, 1 # parent index multiplied by 4
	sll $t4, $t3, 2 # child index multiplied by 4
	add $t4, $t4, $a1 # address of child
	add $t6, $t6, $a1 #address of parent
	lw $s1, 0($t6) #parent value
	lw $s2, 0($t4) #child value
	blt $s2, $s1, swap #if child is smaller than parent, swap them, else return.
	j done
swap: 
	#basic swapping function
	sw $s2, 0($t6)
	sw $s1, 0($t4)
	#after swapping
	sub $t6, $t6, $a1 #returning to old value
	srl $t6, $t6, 2 #index number of parent
	srl $t4, $t4, 2 #index number of child
	add $t3, $t6, $zero #changed index value of swapped child to it's parent's index to check it with it's new parent
	add $t6, $zero, $zero #cleared t6
	j loop		
done:
	
	li $v0, 4
	la $a0, message5
	syscall
	
	
	add $t3, $zero, $zero #cleared t3 register	  
	add $t2, $zero, $zero #cleared $t2 register
	add $a1, $zero, $zero #cleared s0 register
	add $s1, $zero, $zero #cleared s1 register
	add $t4, $zero, $zero #cleared t4 register
	jr $ra
returnFull:
	li $v0, 4
	la $a0, message4
	syscall
	jr $ra
delete:
	#printing message
	li $v0, 4
	la $a0, message3
	syscall
	la $s0, heapArray #array
	lw $t9, 0($s0) #num of elements
	li $t8, 1
	beq $t9, $t8, clearArray1	
	lw $t7, 4($s0) #stored root
	sll $t9, $t9, 2 #mul by 4
	add $t9, $t9, $s0 #address of last element
	lw $s3, 0($t9) #loaded last element
	sw $s3, 4($s0) #stored last element of array in the root.
	add $s3, $zero, $zero #cleared
	sub $t9, $t9, $s0
	srl $t9, $t9, 2 #returned back to old value
	addi $t9, $t9, -1 #decreased number of elements in the heap
	sw $t9, 0($s0) #stored decreased value of heap size
	li $t8, 1 #used as index of rootafter this.
	#the part after this line is for preserving min-heap property.
MinHeapify:	
	add $t4, $t8, $zero #smallest = i
	add $t8, $t4, $zero #i itself
	sll $t6, $t4, 1 #left child
	addi $t5, $t6, 1 #right child	
	bge $t6, $t9, else1 #if l > heap size skip this.
	sll $t6, $t6, 2 #shifted 2
	sll $t4, $t4, 2
	add $t6, $t6, $s0 #added address of heap
	add $t4, $t4, $s0
	lw $s1, 0($t6) #loaded to comparison
	lw $s2, 0($t4)
	sub $t6, $t6, $s0
	sub $t4, $t4, $s0
	srl $t4, $t4, 2
	srl $t6, $t6, 2
	bge $s1, $s2, else1
	add $t4, $t6, $zero # if left<heapsize and heap[left] < heap[index] make l smallest
		
else1:
	bge $t5, $t9, else2 #if r >= heap size skip this.
	sll $t5, $t5, 2 #shifted 2
	sll $t4, $t4, 2
	add $t5, $t5, $s0 #added address of heap
	add $t4, $t4, $s0
	lw $s1, 0($t5) #loaded to comparison
	lw $s2, 0($t4)
	sub $t5, $t5, $s0
	sub $t4, $t4, $s0
	srl $t4, $t4, 2
	srl $t5, $t5, 2
	bge $s1, $s2, else2
	add $t4, $t5, $zero # if right<heapsize and heap[right] < heap[smallest] make right smallest
else2:	
	beq $t4, $t8, FinishDeletion
	sll $t4, $t4, 2
	sll $t8, $t8, 2
	add $t4, $t4, $s0
	add $t8, $t8, $s0
	lw $s1, 0($t4) #loaded s1 child
	lw $s2, 0($t8) #loaded s2 parent
	sw $s1, 0($t8) #swapped
	sw $s2, 0($t4)

	add $s1, $zero, $zero
	add $s2, $zero, $zero
	add $t5, $zero, $zero
	add $t6, $zero, $zero
	sub $t4, $t4, $s0
	srl $t4, $t4, 2
	add $t8, $t4, $zero
	add $t4, $zero, $zero
	j MinHeapify
clearArray1:	#remove the only element in the heap.
	addi $t9, $t9, -1 #decreased number of elements in the heap
	sw $t9, 0($s0)
FinishDeletion:
	jr $ra
		
	# s1 number of elements in the heap
	# t1 written elements in the heap
	# t2 allowed max number in the row 
	# t3 written numbers in the row 
	# t4 for parenthesis or dash  if it is 0 print space if it is 1 print dash
PrintHeap:
	beq $s1, 0, PrintComplete	# if heap has 0 element finish print
	beq $t1, $s1, completeRow	# if all elements are written in the heap then complete row with 'X'
	addi $t1, $t1, 1
	li $v0, 4
	la $a0, openBigPrnts		# print the information of element ( th element )
	syscall
	
	li $v0, 1
	la $a0, ($t1)
	syscall
	
	li $v0, 4
	la $a0, closeBigPrnts
	syscall
	
	li $v0, 1
	lw $a0, ($s6)			# print the number
	syscall
	addi $s6, $s6,4
	
	addi $t3, $t3, 1
	
	beq $t3, $t2, goNewLine		# if written numbers are equal max numbers in the row go new line
	beq $t4, 0, printDash		# print dash or space
	beq $t4, 1, printSpace
goNewLine:
	li $v0, 4
	la $a0, NewLine
	syscall
	sll $t2, $t2, 1			# extend allowed max numbers in the row by 2 
	addi $t3, $zero, 0
	addi $t4, $zero, 0
	j PrintHeap
printDash:
	li $v0, 4
	la $a0, dash
	syscall
	addi $t4, $zero, 1
	j PrintHeap
printSpace:
	li $v0, 4
	la $a0, space
	syscall
	addi $t4, $zero, 0
	j PrintHeap
completeRow:				# complete the row with Xs
	beq $t3, 0, PrintComplete
	beq $t3, $t2, PrintComplete
	li $v0, 4
	la $a0, x
	syscall
	addi $t3, $t3, 1
	beq $t4, 0, rowPrintDash
	beq $t4, 1, rowPrintSpace
rowPrintDash:
	li $v0, 4
	la $a0, dash
	syscall
	addi $t4, $zero, 1
	j completeRow
rowPrintSpace:
	li $v0, 4
	la $a0, space
	syscall
	addi $t4, $zero, 0
	j completeRow
PrintComplete:
	jr $ra	#return after printing finishes
	
	
	# briefly summary of this program, read the input character by character push numbers and operators to the stack
	# when parenthesis is closed do last calcution and push the result
	# when string is finished scan stack twice
	# first make divisions and multiplications
	# second make additions and subtractions
program2:
		addi $sp, $sp, -4		#save $ra
		sw $ra, ($sp)
		la $s4, ($sp)
		li $v0, 4			# print message
		la $a0, message100		
		syscall
	
		li $v0, 8			# read string
		la $a0, prg2Str
		li $a1, 100
		syscall
		
		la $t8, prg2Str
		addi $t1, $zero, 0
		addi $t9, $zero, 2
		
		jal prg2StrToInt
				
		li $v0, 1			#print the result
		la $a0, 0($s0)
		syscall
		
		lw $ra ($sp)			#reload old value of $ra
		addi $sp, $sp, 4
		jr $ra
	prg2StrToInt:
		lb $t7, 0($t8)
		
		beq $t7, '0', add0		# if character is 0 go to add0
		beq $t7, '1', add1		# if character is 1 go to add1
		beq $t7, '2', add2		# if character is 2 go to add2
		beq $t7, '3', add3		# if character is 3 go to add3
		beq $t7, '4', add4		# if character is 4 go to add4
		beq $t7, '5', add5		# if character is 5 go to add5
		beq $t7, '6', add6		# if character is 6 go to add6
		beq $t7, '7', add7		# if character is 7 go to add7
		beq $t7, '8', add8		# if character is 8 go to add8
		beq $t7, '9', add9		# if character is 9 go to add9
		beq $t7, '+', pushToStack	# if character is '+','-','*','/' go to addTo2Darray
		beq $t7, '-', pushToStack	
		beq $t7, '*', pushToStack	
		beq $t7, '/', pushToStack
		beq $t7, '(', nextStr
		beq $t7, ')', doCalculation	# at the end of the parenthesis do last calculation
		addi $t8, $sp, 0
		beq $t7, '\n', priorityEnd	# at the end of the string go to priorityEnd
	pushToStack:
		beq $t1, 0, addMathOperation	#push the number is if character is not an operation
		addi $sp, $sp, -8
		sw $t1, 4($sp)
		sw $t7, 0($sp)
		addi $t1, $zero, 0		# clear $t1
		addi $t8, $t8, 1
		j prg2StrToInt 
	addMathOperation:			# push math operation 
		addi $sp, $sp, -4
		sw $t7, 0($sp)
		addi $t8, $t8, 1
		j prg2StrToInt
	doCalculation:				#make calculation
		bne $t1, 0, addNmbr		# if there is a number we need to push first push it then make the calculation
		lw $t2, 0($sp)			# take 2 numbers and math operation
		lw $t3, 8($sp)
		lw $t4, 4($sp)
		addi $t8, $t8, 1
		beq $t4, '+', addition
		beq $t4, '-', subtraction
		beq $t4, '*', multiplication
		beq $t4, '/', division
	addNmbr:
		addi $sp, $sp, -4
		sw $t1, 0($sp)
		addi $t1, $zero, 0
		j doCalculation
	# make the necessary calculation and push the result to the stack
	addition:				
		addi $sp, $sp, 12
		add $t2, $t3, $t2
		addi $sp, $sp -4
		sw $t2, 0($sp)
		j prg2StrToInt
	subtraction:
		addi $sp, $sp, 12
		sub $t2, $t3, $t2
		addi $sp, $sp -4
		sw $t2, 0($sp)
		j prg2StrToInt
	multiplication:
		addi $sp, $sp, 12
		mul $t2, $t3, $t2
		addi $sp, $sp -4
		sw $t2, 0($sp)
		j prg2StrToInt	
	division:
		addi $sp, $sp, 12
		div $t2, $t3, $t2
		addi $sp, $sp -4
		sw $t2, 0($sp)
		j prg2StrToInt
	nextStr:
		addi $t8, $t8, 1
		j prg2StrToInt
	# after string is finish clear the stack ( do the remain calculations and load result to $s0 )
	# first look at division and multiplication
	priorityEnd:
		bne $t1, 0, pushLastNmbr	# if there a number we need to push, push it
		lw $t2, 0($t8)
		beq $t2, '*', priorityMul
		beq $t2, '/', priorityDiv
		beq $t8, $s4, endOfStr
		addi $t8, $t8, 4
		j priorityEnd
	pushLastNmbr:
		addi $sp, $sp, -4
		sw $t1, 0($sp)
		addi $t1, $zero, 0
		j priorityEnd
	priorityMul:
		addi $t8, $t8, -4
		lw $t2, ($t8)
		lw $t3, 8($t8)
		mul $t2, $t3, $t2
		sw $t2, 8($t8)
		j rearrange
	priorityDiv:
		addi $t8, $t8, -4
		lw $t2, 0($t8)
		lw $t3, 8($t8)
		div $t2, $t3, $t2
		sw $t2, 8($t8)
		j rearrange
	# rearrange the stack if any multiplicaton or division done
	rearrange:
		addi $t8, $t8, -4
		lw $t2, 0($t8)
		sw $t2, 8($t8)
		blt $t8, $sp, endArrange	# at the end of the stack go the beginning of the stack 
						# and this time make all calculation 
		j rearrange
	endArrange:
		addi $sp, $sp, 8
		addi $t8, $sp, 0
		j priorityEnd
	endOfStr:				# make remain calculations
		lw $t2, 0($sp)
		lw $t3, 8($sp)
		lw $t4, 4($sp)
		beq $t4, '+', lastAddition
		beq $t4, '-', lastSubtraction
		j calculationFinished
	lastAddition:
		addi $sp, $sp, 12
		add $t2, $t3, $t2
		addi $sp, $sp -4
		sw $t2, 0($sp)
		j endOfStr
	lastSubtraction:
		addi $sp, $sp, 12
		sub $t2, $t3, $t2
		addi $sp, $sp -4
		sw $t2, 0($sp)
		j endOfStr
	calculationFinished:			# when calculations are done load result to register $s0
		lw $s0, 0($sp)
		addi $sp, $sp, 4
		jr $ra
	#$s0 holds number of rows
	#$s1 holds number of columns
	#$s2 holds given row index
	#$s3 holds given column index
	#$s4 holds min value between given indexes and beginning of the array
	#$s5 holds max value between given indexes and beginning of the array
	#$s7 holds address of string and addrOfmdArray
	#$s6 holds address of mdArray
	
program3:
	addi $sp, $sp, -4
	sw $ra, ($sp)
	addi $t1, $zero, 0
	la $s6 mdArray
	la $s7, string
	jal clearArray			# clear array
	
	la $s7, string
	addi $t1, $zero, 0
	jal clearString			#clear string
	
	jal clearAll
	
	li $v0, 4			# print message
	la $a0, message100		
	syscall
	
	li $v0, 8			# read string
	la $a0, string
	li $a1, 150
	syscall
	
	li $v0, 4			#print message
	la $a0, message200
	syscall
	
	li $v0, 5			# read input ( number of rows )
	syscall
	add $s0, $zero, $v0		# move input into the $s0
	
	la $s7, string			# take address of string
	la $s6, mdArray			# take address of 2dArray
	
	
	
	add $t8, $zero, $s7	
	add $s1, $s1, $zero
	jal numberOfColumn		# calculate the number of column
	
	add $s1, $zero, $t2		# hold number of column in $s1
	add $t1, $t1, $zero  		# $t1 = 0
	add $t2, $zero, $zero		#$t2,$t3,$t7 
	add $t3, $zero, $zero		 
	add $t7, $zero, $zero
	
	add $t8, $zero, $s7		#$t8 = $s7
	add $t6, $zero, $s6		#$t6 = $s6
	addi $t9, $zero, 1
	jal stringToInteger		# go to string partition
	
	add $t7, $zero, $zero		#$t1,$t7,$t8 are cleared and they is available to use (boşta) 
	add $t1, $zero, $zero
	add $t8, $zero, $zero
	
	add $t6, $zero, $s6		#$t6 = $s6
	add $t2, $zero, $s1
	add $t0, $zero, $zero
	
	li $v0, 4
	la $a0, message300
	syscall
	
	jal display2Darray			# print 2D array
	
	li $v0, 4
	la $a0, message400
	syscall
	
	li $v0, 5
	syscall
	add $s2, $zero, $v0		# $s2 holds row index
	
	li $v0, 4
	la $a0, message500
	syscall
	
	li $v0, 5
	syscall
	add $s3, $zero, $v0		# $s2 holds column index
	
	li $v0, 4
	la $a0, message600
	syscall
	
	add $t1, $zero, $s6
	la $s7, addrOfmdArray
	addi $t8, $s7, 7		# last char of string
	jal decimalToHex		# conver decimal to hexadecimal of base address
	
	li $v0, 4
	la $a0, addrOfmdArray
	syscall
	
	mul $t1, $s1, $s2
	add $t1, $t1, $s3
	sll $t1, $t1, 2
	add $t1, $t1, $s6
	addi $t8, $s7, 7
	jal decimalToHex		# conver decimal to hexadecimal of given indexes address
	
	li $v0, 4			#print message
	la $a0, message700
	syscall
	
	li $v0, 1			#print message
	la $a0, ($s2)
	syscall
	
	li $v0, 4			#print message
	la $a0, message800
	syscall
	
	li $v0, 1			#print message
	la $a0, ($s3)
	syscall
	
	li $v0, 4			#print message
	la $a0, message900
	syscall
	
	
	
	li $v0, 4
	la $a0, addrOfmdArray
	syscall
	lw $t2, ($s6)
	
	add $s4, $t2, $zero
	add $s5, $t2, $zero
	add $t1, $zero, $zero
	add $t0, $zero, $zero
	jal findMaxAndMinValue		# find min and max value 
	
	li $v0, 4			#print message
	la $a0, message1000
	syscall
	
	
	li $v0, 1			
	la $a0, ($s2)
	syscall
	
	
	li $v0, 4			#print message
	la $a0, message800
	syscall
	
	
	li $v0, 1			
	la $a0, ($s3)
	syscall
	
	li $v0, 4			#print message
	la $a0, message1200
	syscall
	
	
	li $v0, 1			
	la $a0, ($s4)			#min value
	syscall
	
	li $v0, 4			#print message
	la $a0, message1100
	syscall
	
	
	li $v0, 1			
	la $a0, ($s2)
	syscall
	
	
	li $v0, 4			#print message
	la $a0, message800
	syscall
	
	
	li $v0, 1			
	la $a0, ($s3)
	syscall
	
	li $v0, 4			#print message
	la $a0, message1200
	syscall
	
	li $v0, 1			#max value
	la $a0, ($s5)
	syscall
	
	addi $t1, $zero, 0
	jal clearArray			# clear array
	
	la $s7, string
	addi $t1, $zero, 0
	jal clearString			#clear string
	
	jal clearAll
	lw $ra ($sp)
	addi $sp, $sp, 4
	jr $ra
stringToInteger:
	
	lb $t7, 0($t8)
	
	beq $t7, '0', add0		# if character is 0 go to add0
	beq $t7, '1', add1		# if character is 1 go to add1
	beq $t7, '2', add2		# if character is 2 go to add2
	beq $t7, '3', add3		# if character is 3 go to add3
	beq $t7, '4', add4		# if character is 4 go to add4
	beq $t7, '5', add5		# if character is 5 go to add5
	beq $t7, '6', add6		# if character is 6 go to add6
	beq $t7, '7', add7		# if character is 7 go to add7
	beq $t7, '8', add8		# if character is 8 go to add8
	beq $t7, '9', add9		# if character is 9 go to add9
	beq $t7, ' ', addTo2Darray	# if character is 'space' go to addTo2Darray
	beq $t7, '\n', endOfString	# at the end of the string go to endOfString
	
	# in the add labels multiply 10 with old number and add new number  then increment $t8 for next character of string 
	add0:
		mul $t1, $t1, 10
		addi $t1, $t1, 0
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add1:
		mul $t1, $t1, 10
		addi $t1, $t1, 1
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add2:
		mul $t1, $t1, 10
		addi $t1, $t1, 2
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add3:	
		mul $t1, $t1, 10
		addi $t1, $t1, 3
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add4:
		mul $t1, $t1, 10
		addi $t1, $t1, 4
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add5:
		mul $t1, $t1, 10
		addi $t1, $t1, 5
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add6:
		mul $t1, $t1, 10
		addi $t1, $t1, 6
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add7:
		mul $t1, $t1, 10
		addi $t1, $t1, 7
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add8:
		mul $t1, $t1, 10
		addi $t1, $t1, 8
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	add9:
		mul $t1, $t1, 10
		addi $t1, $t1, 9
		addi $t8, $t8, 1
		beq $t9, 1, stringToInteger
		beq $t9, 2, prg2StrToInt
	
	# at the space character insert the number into the 2D array
	addTo2Darray:
		addi $t8, $t8, 1
		sw $t1, 0($t6)
		addi $t6, $t6, 4
		add $t1, $zero, $zero
		
		j stringToInteger
	numberOfColumn:			# when see the space increment number of elements at the end divide it with row number
		lb $t7, ($t8)
		beq $t7, '0', incAddr
		beq $t7, ' ', incrementNumberOfElement
		beq $t7, '\n', return
		addi $t8, $t8, 1
		j numberOfColumn
	incAddr:
		addi $t8, $t8, 1
		j numberOfColumn
	incrementNumberOfElement:
		addi $s1, $s1, 1
		addi $t8, $t8, 1
		j numberOfColumn
	return:
		addi $s1, $s1, 1
		addi $t5, $s1, 0
		div $t2, $s1, $s0
		mul $t3, $t2, $s0
		bne $t3, $s1 incrementColumnByOne	# if there is remainder increment column by 1
		jr $ra
	incrementColumnByOne:
		addi $t2, $t2, 1
		jr $ra 
	endOfString:
		add $t8, $zero, $zero
		sw $t1, ($t6)
		jr $ra
	display2Darray:					
		beq $t0, $t5, endOfFnct		# t0 printed elemnts t5 number of elemnts
		lw $t1, ($t6)
		addi $t6, $t6, 4
		beq $t2, 0, newLine		# t2 max number in the row count down when it is 0 go new line
		addi $t2, $t2, -1
		li $v0, 1
		la $a0, ($t1)
		syscall
		li $v0, 4
		la $a0, spaceChar
		syscall
		addi $t0, $t0, 1
		j display2Darray
	newLine:				
		add $t2, $zero, $s1
		addi $t2, $t2, -1
		li $v0, 4
		la $a0, newline
		syscall
		li $v0, 1
		la $a0, ($t1)
		syscall
		li $v0, 4
		la $a0, spaceChar
		syscall
		
		addi $t0, $t0, 1
		j display2Darray
	decimalToHex:				# divide the number with 16 and
						# insert remainder into the string untill number is less than 16
		addi $t4, $zero, 16
		slt $t6, $t1, $t4
		beq $t6, 1, decimalToHexLastNumber
		div $t2, $t1, 16
		mul $t3, $t2, 16
		sub $t4, $t1, $t3
		add $t1, $zero, $t2
		j convertToHex
	convertToHex:
		beq $t4, 0, hex0
		beq $t4, 1, hex1
		beq $t4, 2, hex2
		beq $t4, 3, hex3
		beq $t4, 4, hex4
		beq $t4, 5, hex5
		beq $t4, 6, hex6
		beq $t4, 7, hex7
		beq $t4, 8, hex8
		beq $t4, 9, hex9
		beq $t4, 10, hex10
		beq $t4, 11, hex11
		beq $t4, 12, hex12
		beq $t4, 13, hex13
		beq $t4, 14, hex14
		beq $t4, 15, hex15
	hex0:
		la $t5, zeroChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex1:
		la $t5, oneChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex2:
		la $t5, twoChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex3:
		la $t5, threeChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex4:
		la $t5, fourChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex5:
		la $t5, fiveChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex6:
		la $t5, sixChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex7:
		la $t5, sevenChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex8:
		la $t5, eightChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex9:
		la $t5, nineChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex10:
		la $t5, aChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex11:
		la $t5, bChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex12:
		la $t5, cChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex13:
		la $t5, dChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex14:
		la $t5, eChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	hex15:
		la $t5, fChar
		lb $t0, ($t5)
		sb $t0, ($t8)
		addi $t8, $t8, -1
		beq $t6, 0, decimalToHex
		jr $ra
	decimalToHexLastNumber:			 # insert the last number as a hexadecimal
		beq $t1, 0, hex0
		beq $t1, 1, hex1
		beq $t1, 2, hex2
		beq $t1, 3, hex3
		beq $t1, 4, hex4
		beq $t1, 5, hex5
		beq $t1, 6, hex6
		beq $t1, 7, hex7
		beq $t1, 8, hex8
		beq $t1, 9, hex9
		beq $t1, 10, hex10
		beq $t1, 11, hex11
		beq $t1, 12, hex12
		beq $t1, 13, hex13
		beq $t1, 14, hex14
		beq $t1, 15, hex15
	findMaxAndMinValue:	
		
		mul $t8, $t0, $s1
		add $t8, $t8, $t1
		sll $t8, $t8, 2
		add $t8, $t8, $s6
		lw $t2, ($t8)
		blt $s5, $t2, maxValue
		blt $t2, $s4, minValue
		beq $t1, $s3, increaseRow
		addi $t1, $t1, 1
		j findMaxAndMinValue 
	minValue:
		add $s4, $t2, $zero
		beq $t1, $s3, increaseRow
		addi $t1, $t1, 1
		j findMaxAndMinValue
	maxValue:
		add $s5, $t2, $zero
		beq $t1, $s3, increaseRow
		addi $t1, $t1, 1
		j findMaxAndMinValue
	increaseRow:
		beq $t0, $s2, endOfFnct
		addi $t0, $t0, 1
		add $t1, $zero, $zero
		j findMaxAndMinValue
	clearArray:
		beq $t1, 400, endOfFnct
		sb $zero, ($s6)
		addi $s6, $s6, 1
		addi $t1, $t1, 1
		j clearArray
	clearString:
		beq $t1, 150, endOfFnct
		sb $zero, ($s7)
		addi $s7, $s7, 1
		addi $t1, $t1, 1
		j clearString
	clearAll:
		addi $t0, $zero, 0		# clear all registers
		addi $t1, $zero, 0
		addi $t2, $zero, 0
		addi $t3, $zero, 0
		addi $t4, $zero, 0
		addi $t5, $zero, 0
		addi $t6, $zero, 0
		addi $t7, $zero, 0
		addi $t8, $zero, 0
		addi $t9, $zero, 0
		addi $s0, $zero, 0
		addi $s1, $zero, 0
		addi $s2, $zero, 0
		addi $s3, $zero, 0
		addi $s4, $zero, 0
		addi $s5, $zero, 0
		addi $s6, $zero, 0
		addi $s7, $zero, 0
		jr $ra
	endOfFnct:
		jr $ra










