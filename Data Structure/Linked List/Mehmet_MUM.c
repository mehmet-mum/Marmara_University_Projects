#include <stdio.h>
#include <stdlib.h>

struct node{
	int data;
	struct node *nextPtr;
};

void insert(int a,struct node **header);
void display(struct node *cp);
struct node* multiplication(struct node *first , struct node *second);
struct node *temp(struct node* linkedList);
struct node *addTwoLinkedList(struct node* first, struct node *second);
void displayWithDot(struct node *cp , int loop);
int getLengthOfLL(struct node *cp);


main(){
	int enteredMinus, enteredDot, enteredDot2;
	int loop = 0;
	
	char c;
	

	struct node *first = NULL;
	struct node *second = NULL;
	struct node *result = NULL;
	
	printf("Please, Enter first number: ");
	
	c = getche();		//get character after user press key
	if( c == 45 ){		
		c = getche();	// if user enter " - " sign   increase the integer
		enteredMinus++;
	}
	enteredDot = 0;
	
	// this while loop for first number
	// get characters from user when he keeps enter number or dot sign
	while( c >= 48 && c <= 57 || c == 46){
		if ( c == 46){
			enteredDot = 1;	// if user enter dot sign do enteredDot  1 
		}
		else if ( c >= 48 && c <= 57 && enteredDot == 0 ){
			insert(c - 48, &first);								// if user enters integer number place this numbers to linked list normally
		}
		else if ( c >= 48 && c <= 57 && enteredDot == 1 ){		// if user enters fractional number first enteredDot will be  1 then,
			insert(c - 48, &first);								// after the dot , program will determine digit of numbers 
			loop++;
			
		}
		
		c = getche();
		
	}
	
	
	
	printf("\n");
	printf("Please, Enter second number: ");
	
	
	// the while loop for second number   logic is same as first number 
	c = getche();
	if( c == 45 ){
		c = getche();
		enteredMinus++;
	}
	enteredDot2 = 0;
	while( c >= 48 && c <= 57 || c == 46){
		if ( c == 46){
			enteredDot2 = 1;
		}
		else if ( c >= 48 && c <= 57 && enteredDot2 == 0 ){
			insert(c - 48, &second);	
		}
		else if ( c >= 48 && c <= 57 && enteredDot2 == 1 ){
			insert(c - 48, &second);
			loop++;
			
		}
		
		c = getche();
		
	}
	
	printf("\n");
	
	
	
	result = multiplication(first , second);		// get the multiplication of the two numbers
	
	loop = getLengthOfLL(result) - loop; 		// for determine to place  dot sign after how many digit   
	
	if( enteredMinus == 1 ){
		printf("Result is: -"); // if one of the numbers is minus put  minus sign before result
	}
	else{
		printf("Result is: "); // if the numbers are both minus or not minus dont  put before result
	}
	
	if( enteredDot != 0 || enteredDot2 != 0){  // if at least one of the numbers are fractional then consider the multiplication of fractional numbers 
		displayWithDot(result,loop);
	}
	else{
		display(result);	// if numbers are not fractional do normal multiplication
	}
	
	
	return 0;
}

// insert a node in linked list
void insert(int a,struct node **header){
	struct node *np;
	np = malloc( sizeof( struct node));  // desire location
	np -> data = a;
	np -> nextPtr = NULL;
	
	if(*header == NULL){
		*header = np;
		return;
	}
	
	np -> nextPtr = *header;
	*header = np;
}

// print the linked list
void display(struct node *cp){
	while( cp != NULL){
		printf("%d" , cp -> data);
		cp = cp -> nextPtr;
		
	}
	printf("\n");
}

// print the result with this function if at least one of the numbers is fractional
void displayWithDot(struct node *cp , int loop) {
	
	while( loop != 0){
		printf("%d" , cp -> data);		// until the loop be 0 print the numbers then print dot then print numbers again
		cp = cp -> nextPtr;
		loop--;
	}
	
	printf(".");
	while( cp != NULL){
		printf("%d" , cp -> data);
		cp = cp -> nextPtr;
		
	}
}

// get length of linked list
int getLengthOfLL(struct node *cp){
	int length = 0;
	while( cp != NULL){
		length++;
		cp = cp -> nextPtr;
	}
	return length;
}

// multiplication of two linked list
struct node* multiplication(struct node* first, struct node* second){
	struct node *result = NULL;    // result linked list
	struct node *multi = NULL;	  // asistant linked list at multiplication	
	struct node *prevFirst;      // a linked list to bring first linked list original form
	
	prevFirst = first;
	
	int a = 0;
	int b = 0;
	int lengthOFSecond = 1; 
	int prevLengthOFSecond = 1;
	// for example user enter 123 and 157
	// in this while loop, program multiply 123 and 7 
	while(first != NULL){
		
		a = (first -> data * second -> data) + b;
		
		b = a / 10;
	
		a = a % 10;
		
		
		insert(a, &result);
		
		first = first -> nextPtr;
	
	}
	// if have number exept 0  insert it 
	if( b != 0 ){
		insert(b, &result);
	}
	
	// in this while loop, program multiply 123 and 5  , will insert numbers into multi ll , will add  with multi ll and result ll
	// it will do it again for 123 and 1   this will last until  second ll be NULL
	second = second -> nextPtr;
	while( second != NULL){
		b = 0;
		first = prevFirst ;
		multi = NULL;
		
		// program add 0 for example when multiply 123 and 5   615  will turn 6150 and will add with 7 * 123 
		// it will do it again 123 * 1   add 2 0 it will turn 12300   add 12300 + 6150 + 7*123  = 19131  here is the result !
		while (lengthOFSecond != 0){
			insert(0, &multi);
			lengthOFSecond--;
		}
		lengthOFSecond = prevLengthOFSecond + 1;
		while(first != NULL){
			
			a = (first -> data * second -> data) + b;
	
			b = a / 10;
	
			a = a % 10;
		
		
			insert(a, &multi);
		
			first = first -> nextPtr;
			
		}
		if( b != 0 ){
			insert(b, &multi);
		}
		
		result = addTwoLinkedList(result, multi);
	
		second = second -> nextPtr;
		prevLengthOFSecond++;
	}
	
	return result;
}


struct node *addTwoLinkedList(struct node* first, struct node *second){
	struct node *result = NULL;
	
	// reverse  first ll and second ll 
	first = temp(first);
	second = temp(second);
	
	int a = 0;
	int b = 0;
	
	// untill one of them be NULL  plus numbers and insert result
	while(first != NULL && second != NULL){
		b = first -> data + second -> data + a;
		a = b / 10;
		b = b % 10;
		insert(b , &result);
		
		first = first -> nextPtr;
		second = second -> nextPtr;
		
	}
	
	// one of the numbers can have more digit when it becomes do belows
	if( first == NULL){
			while(second != NULL){
				b = second -> data + a;
				a = b / 10;
				b = b % 10;
				
				insert(b , &result);
				
				second = second -> nextPtr;
			}
	}
	
	else if( second == NULL ){
			while(first != NULL){
				b = first -> data + a;
				a = b / 10;
				b = b % 10;
				
				insert(b , &result);
				
				first = first -> nextPtr;
			}
	}
	if( a != 0){
		insert(a , &result);
	}
	return result;
}

// reverse function

struct node* temp(struct node* linkedList){
	struct node *temp = NULL;
	int a = 0;
	
	while(linkedList != NULL){
		a = linkedList -> data;
		insert(a , &temp);
		linkedList = linkedList -> nextPtr;
	}
	
	return temp;	
}
