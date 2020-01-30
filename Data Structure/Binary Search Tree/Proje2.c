#include <stdio.h>
#include <stdlib.h>



struct node{
	int number1;				// keep number 1
	int number2;				// keep number 2 
	int depth;    				// keep depth level of node
	int place;					// keep location of node on depth level
	struct node *leftPtr;		// left child
	struct node *rightPtr;		// right child
	struct node *parrent;		// parrent
};

int maxDepthLevel = 0; // maximum depth level in tree

void insert(struct node **tree, int n1, int n2,int a){
	struct node *newNode;
	newNode = malloc( sizeof( struct node)); 
	newNode -> number1 = n1;
	newNode -> number2 = n2;
	newNode -> leftPtr = NULL;
	newNode -> rightPtr = NULL;
	
	
	struct node *current;
	
	current = *tree;
	// if tree is NULL root is new node
	if(*tree == NULL){
		printf("node_id : %d\n", n1);
		newNode -> depth = 0;
		newNode -> place = 1;
		newNode -> parrent = NULL;
		*tree = newNode;
		
	}
	else{
		// if node's depth level is even number  consider first number
		if ( a  % 2 == 0 ){
			
			if ( n1 < current -> number1){
				
				// if given number1 is less than node's number1 and node's left child is empty insert new node
				if ( current -> leftPtr == NULL){
					
					newNode -> place = 2 * (current -> place) - 1; 
					newNode -> depth = a + 1;
					
					/* if new node's depth level is bigger than tree's maximum depth level
					tree new maximum depth level will become new node's depth level */
					
					if( newNode -> depth > maxDepthLevel){
						maxDepthLevel = newNode -> depth;
					}
				
					
					newNode -> parrent = *tree;  // tree becomes node's parrent
					current -> leftPtr = newNode;
				}
				
				// if it is not NULL insert recursively
				
				else{
					
					a++;
					insert(&current -> leftPtr ,n1 , n2 ,a);
				
				}
			
			}
			else{
				
				// if given number1 is equal or bigger than node's number1 and node's right child is empty insert new node
				
				if ( current -> rightPtr == NULL){
					
					newNode -> place = 2 * (current -> place); 
					newNode -> depth = a + 1;
					
					/* if new node's depth level is bigger than tree's maximum depth level
					tree new maximum depth level will become new node's depth level */
					
					if( newNode -> depth > maxDepthLevel){
						maxDepthLevel = newNode -> depth; // tree becomes node's parrent
						
					}
					
					newNode -> parrent = *tree;
					current -> rightPtr = newNode;
				}
				
				// if it is not NULL insert recursively
				else{
					a++;
					insert(&current -> rightPtr, n1 , n2 ,a);
				}	
			}
		}
		
		// if node's depth level is odd number consider first number

		else if ( a % 2 == 1 ){
			// if given number2 is less than node's number2 and node's left child is empty insert new node
			if ( n2 < current -> number2){
				if ( current -> leftPtr == NULL){
					newNode -> place = 2 * (current -> place) - 1; 
					newNode -> depth = a + 1;
					
					/* if new node's depth level is bigger than tree's maximum depth level
					tree new maximum depth level will become new node's depth level */
					
					if( newNode -> depth > maxDepthLevel){
						maxDepthLevel = newNode -> depth;
					}
				
					newNode -> parrent = *tree;
					current -> leftPtr = newNode;
				}
				else{
					// if it is not NULL insert recursively
					a++;
					insert(&current -> leftPtr, n1 , n2 ,a);
				}
			}
			
			else{
				// if given number2 is equal or bigger than node's number2 and node's right child is empty insert new node
				
				if ( current -> rightPtr == NULL){
					newNode -> place = 2 * (current -> place); 
					newNode -> depth = a + 1;
					
					/* if new node's depth level is bigger than tree's maximum depth level
					tree new maximum depth level will become new node's depth level */
					if( newNode -> depth > maxDepthLevel){
						maxDepthLevel = newNode -> depth;
					}
					
					newNode -> parrent = *tree;
					current -> rightPtr = newNode;
				}
				else{
					// if it is not NULL insert recursively
					a++;
					insert(&current -> rightPtr, n1 , n2 ,a);
				}	
			}
		}
		*tree = current ;	
	}
	
	
	
	
}
struct node *maxNode;
void max(struct node *tree,int a){
	// scan all tree and indicate max Node
	if( tree != NULL){
		
		if(a % 2 == 0){
			
			if( tree->number1 > maxNode->number1){
				maxNode = tree;
			}
			
		}
		else if(a % 2 == 1){
			
			if( tree->number2 > maxNode->number2){
				maxNode = tree;
				
			}
			
		}
		
		max(tree->leftPtr, a);
		max(tree->rightPtr, a);
	}
	
}

struct node *minNode;
void min(struct node *tree,int a){
	// scan all tree and indicate min Node
	if( tree != NULL){
		
		if(a % 2 == 0){
		
			if( tree->number1 < minNode->number1){
				minNode = tree;		
			}
			
		}
		else if(a % 2 == 1){
			
			if( tree->number2 < minNode->number2){
				minNode = tree;
			}
			
		}
		
		min(tree->leftPtr, a);
		min(tree->rightPtr, a);
	}
	
}

void delete(struct node *tree, int n1, int n2,int a){
	// if node has no child go its parent and delete
	if(tree->number1 == n1 && tree -> number2 == n2){
		
		if(tree->leftPtr == NULL && tree->rightPtr == NULL){
			if(tree->parrent->depth % 2 == 0){
				if(tree->parrent->number1 > tree->number1){
					tree->parrent->leftPtr = NULL;	
				}
				else{
					tree->parrent->rightPtr = NULL;
				}
			}
			else if(tree->parrent->depth % 2 == 1){
				if(tree->parrent->number2 > tree->number2){
					tree->parrent->leftPtr = NULL;	
				}
				else{
					tree->parrent->rightPtr = NULL;
				}
			}
			
			
					
		}
		// if node has left child and it has no child delete that node replace his left child
		
		else if(tree->leftPtr != NULL && tree->leftPtr->leftPtr == NULL && tree->leftPtr->rightPtr == NULL){
			tree->number1 = tree->leftPtr->number1;
			tree->number2 = tree->leftPtr->number2;
			tree->leftPtr = NULL;
		}
		
		// if node has no left child and  has right child (and it has no child) delete that node replace his right child
		else if(tree->leftPtr == NULL && tree->rightPtr != NULL && tree->rightPtr->leftPtr == NULL && tree->rightPtr->rightPtr == NULL){
			tree->number1 = tree->rightPtr->number1;
			tree->number2 = tree->rightPtr->number2;
			tree->rightPtr = NULL;
		}
		  
		  
		else if(maxNode == NULL && minNode == NULL){
			/* if node's left child is not empty  go left child find max node and replace it instead of node which wanted to be deleted
			if max node has no child delete it vice versa 
			max node has child or children then repeat this function
			
			if node's left child is empty and right child is not empty go right child find min node and replace it insead of node which wanted to be deleted
			if min node has no child delete it vice versa
			it has child or children then repeat this function */
			if(tree->leftPtr != NULL){
				maxNode = tree->leftPtr;
				max(tree->leftPtr , tree->depth);
				tree->number1 = maxNode -> number1;
				tree->number2 = maxNode -> number2;
				
				delete(tree->leftPtr,maxNode->number1, maxNode->number2, tree->leftPtr->depth);
			}
			else if(tree->rightPtr != NULL){
				minNode = tree->rightPtr;
				min(tree->rightPtr , tree->depth);
				tree->number1 = minNode -> number1;
				tree->number2 = minNode -> number2;
				
				delete(tree->rightPtr,minNode->number1, minNode->number2, tree->rightPtr->depth);
			}
			
		}
		else if(maxNode -> leftPtr != NULL || maxNode -> rightPtr != NULL || minNode -> leftPtr != NULL || minNode -> rightPtr != NULL ){
			
			
			if(tree->leftPtr != NULL){
				maxNode = tree->leftPtr;
				max(tree->leftPtr , tree->depth);
				tree->number1 = maxNode -> number1;
				tree->number2 = maxNode -> number2;
				if(maxNode -> leftPtr == NULL && maxNode -> leftPtr == NULL){
					tree->leftPtr = NULL;
				}
				else{
					delete(tree->leftPtr,maxNode->number1, maxNode->number2, tree->leftPtr->depth);
				}
			}
			else if(tree->leftPtr == NULL && tree->rightPtr != NULL){
				
				minNode = tree->rightPtr;
				min(tree->rightPtr , tree->depth);
				tree->number1 = minNode -> number1;
				tree->number2 = minNode -> number2;
				
				if(minNode -> leftPtr == NULL && minNode -> leftPtr == NULL){
					tree->leftPtr = NULL;
				}
				else{
					delete(tree->rightPtr,minNode->number1, minNode->number2, tree->rightPtr->depth);
				}
				
			}
			
		}
		 
		
	}

	// this section for finding given numbers in tree 
	else{
		if ( a % 2 == 0){
			if(n1 < tree -> number1){
				if( tree->leftPtr == NULL){
					printf("There is No node!");
					return;
				}
				else{
					a++;
					delete(tree->leftPtr, n1, n2, a );
				}
			}
			else{
				if( tree->rightPtr == NULL){
					printf("There is No node!");
					return;
				}
				else{
					a++;
					delete(tree->rightPtr, n1, n2, a );
				}
			}
		}
		else if( a % 2 ==1){
			if(n2 < tree -> number2){
				if( tree->leftPtr == NULL){
					printf("There is No node!");
					return;
				}
				else{
					a++;
					delete(tree->leftPtr, n1, n2, a );
				}
			}
			else{
				if( tree->rightPtr == NULL){
					printf("There is No node!");
					return;
				}
				else{
					a++;
					delete(tree->rightPtr, n1, n2, a );
				}
			}
		}
	}
}

// this function for search how many nodes there in maximum depth level 
// if there is no node in maximum level  then   integer maxDepthLevel will decrease
int maxNodeN1 = 0;
void searchMaxDepthLevel(struct node *tree){
	if(tree != NULL){
		if(tree->depth == maxDepthLevel){
			maxNodeN1++;
			
			
		}
		searchMaxDepthLevel(tree->leftPtr);
		searchMaxDepthLevel(tree->rightPtr);
	}
}

void display(struct node *tree){
	
	// look for what is maximum depth level of tree
	while(maxNodeN1 == 0 ){
		searchMaxDepthLevel(tree);
		if(maxNodeN1==0){
			maxDepthLevel--;
			
		}
	}
		
	maxNodeN1 = 0;
	int a = 0;

	int b = 1;
	int k ;
	int numberOfNode = 1;
	int currentNumberOfNode = 1;
	struct node* current;
	
	printf("%d : " , a);
		
	while(a <= maxDepthLevel){
		current = tree;
		
		while(current != NULL && currentNumberOfNode != 0){
			if(current -> depth == a && current -> place == b){
				printf (" %d,%d ", current -> number1, current ->number2);
				b++;
				currentNumberOfNode = 0;
			}
				
			else{
				if(b <= currentNumberOfNode){
					current = current -> leftPtr;
					currentNumberOfNode = currentNumberOfNode - k;
					k = k / 2;
				
				
				}
				else{
					current = current -> rightPtr;
					currentNumberOfNode = currentNumberOfNode +  k;
					k = k / 2;
				}
			}
		}
		if( current == NULL){
				printf(" X ");
				b++;
				currentNumberOfNode = 0;
			}
		
		if(b == numberOfNode + 1){
			printf("\n");
		
			a++;
			if(a <= maxDepthLevel)printf("%d : " , a);
			b = 1;
			numberOfNode = numberOfNode * 2;
					
		}
		currentNumberOfNode = numberOfNode / 2;
		k = currentNumberOfNode / 2;	
		
	}
	
		
}


int main(){
	
	char c;
	int number1,number2,demand;

	
	FILE *file;
	struct node *tree;
	
	
	
	printf("0-Exit\n");
	printf("1-Insert numbers BST from input file\n");
	printf("2-Insert a node BST\n");
	printf("3-Delete a node BST\n");
	printf("4-Print BST\n");
	printf("\nWhat do you want to do : ");
	
	scanf("%d", &demand);
	printf("\n");
	
	
	while(demand == 1 || demand == 2 || demand == 3 || demand == 4){
	
		// open file take numbers and insert them
		if(demand == 1){
			file = fopen("input.txt" , "r");
	
	
	
			if(file != NULL){
				
				while(!feof(file)){
					fscanf(file, "%d", &number1);
					c =fgetc(file);
					fscanf(file, "%d", &number2);
					
					insert(&tree,number1,number2,0);
						
				}
				
				
			}
			else {
				printf("There is no file!");
			}
			fclose(file);
			
			printf("\nWhat do you want to do : ");
			scanf("%d", &demand);
			printf("\n");
			
		}
		// take numbers from user and insert them
		if(demand == 2){
			
			printf("Enter numbers : ");		
			scanf("%d %c %d" , &number1,&c,&number2);
			
			insert(&tree,number1,number2,0);
			
		
			printf("\nWhat do you want to do : ");
			scanf("%d", &demand);
			printf("\n");
			
		}
		// take numbers from user and delete them
		else if(demand == 3){
			
			printf("Enter numbers : ");		
			scanf("%d %c %d" , &number1,&c,&number2);
			maxNode=NULL;
			minNode=NULL;
			delete(tree, number1, number2, 0);
			
			printf("\nWhat do you want to do : ");
			scanf("%d", &demand);
			printf("\n");
			
		}
		// draw tree
		else if(demand == 4){
			display(tree);
			
			printf("\nWhat do you want to do : ");
			scanf("%d", &demand);
			printf("\n");
			
		}
		else{
			printf("Error!");
			demand = 0;
		}
	}
	return 0;
	
}
