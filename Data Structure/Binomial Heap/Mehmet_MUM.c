#include<stdio.h>
#include<malloc.h>
#include<stdlib.h>
#include<math.h>
 
struct node {
    int id;
    int e;
    int t_arrange;
    int t_change;
    int e_new;
    double priority;
    int degree;
    
    struct node* parent;
    struct node* child;
    struct node* sibling;
};
 
struct node* MAKE_bin_HEAP();
int bin_LINK(struct node*, struct node*);
struct node* CREATE_NODE(int id,int e, int tarrange,int tchange, int enew);
struct node* bin_HEAP_UNION(struct node*, struct node*);
struct node* bin_HEAP_INSERT(struct node*, struct node*);
struct node* bin_HEAP_MERGE(struct node*, struct node*);
struct node* bin_HEAP_EXTRACT_MIN(struct node*);
int REVERT_LIST(struct node*);
int DISPLAY(struct node*);
struct node* FIND_NODE(struct node*, int);
int bin_HEAP_DECREASE_KEY(struct node*, int, int);
int bin_HEAP_DELETE(struct node*, int);
 
int count = 1;
 
struct node* MAKE_bin_HEAP() {
    struct node* np;
    np = NULL;
    return np;
}
 
struct node * H = NULL;
struct node *Hr = NULL;
 
int bin_LINK(struct node* y, struct node* z) {
    y->parent = z;
    y->sibling = z->child;
    z->child = y;
    z->degree = z->degree + 1;
}
 
struct node* CREATE_NODE(int id,int e, int tarrange,int tchange, int enew) {
    struct node* p;//new node;
    p = (struct node*) malloc(sizeof(struct node));
    p->id = id;
    p->e = e;
    p->t_arrange = tarrange;
    p->t_change = tchange;
    p->e_new = enew;
    p->priority = e;
    return p;
}
 
struct node* bin_HEAP_UNION(struct node* H1, struct node* H2) {
    struct node* prev_x;
    struct node* next_x;
    struct node* x;
    struct node* H = MAKE_bin_HEAP();
    H = bin_HEAP_MERGE(H1, H2);
    if (H == NULL)
        return H;
    prev_x = NULL;
    x = H;
    next_x = x->sibling;
    while (next_x != NULL) {
        if ((x->degree != next_x->degree) || ((next_x->sibling != NULL)
                && (next_x->sibling)->degree == x->degree)) {
            prev_x = x;
            x = next_x;
        } else {
            if (x->id <= next_x->id) {
                x->sibling = next_x->sibling;
                bin_LINK(next_x, x);
            } else {
                if (prev_x == NULL)
                    H = next_x;
                else
                    prev_x->sibling = next_x;
                bin_LINK(x, next_x);
                x = next_x;
            }
        }
        next_x = x->sibling;
    }
    return H;
}
 
struct node* bin_HEAP_INSERT(struct node* H, struct node* x) {
    struct node* H1 = MAKE_bin_HEAP();
    x->parent = NULL;
    x->child = NULL;
    x->sibling = NULL;
    x->degree = 0;
    H1 = x;
    H = bin_HEAP_UNION(H, H1);
    return H;
}
 
struct node* bin_HEAP_MERGE(struct node* H1, struct node* H2) {
    struct node* H = MAKE_bin_HEAP();
    struct node* y;
    struct node* z;
    struct node* a;
    struct node* b;
    y = H1;
    z = H2;
    if (y != NULL) {
        if (z != NULL && y->degree <= z->degree)
            H = y;
        else if (z != NULL && y->degree > z->degree)
            /* need some modifications here;the first and the else conditions can be merged together!!!! */
            H = z;
        else
            H = y;
    } else
        H = z;
    while (y != NULL && z != NULL) {
        if (y->degree < z->degree) {
            y = y->sibling;
        } else if (y->degree == z->degree) {
            a = y->sibling;
            y->sibling = z;
            y = a;
        } else {
            b = z->sibling;
            z->sibling = y;
            z = b;
        }
    }
    return H;
}
 
int DISPLAY(struct node* H) {
    struct node* p;
    if (H == NULL) {
        printf("\nHEAP EMPTY");
        return 0;
    }
    printf("\nTHE ROOT NODES ARE:-\n");
    p = H;
    while (p != NULL) {
        printf("%d - %d - %d - %d - %d", p->id,p->e,p->t_arrange,p->t_change,p->e_new);
        if (p->sibling != NULL)
            printf("-->");
        p = p->sibling;
    }
    printf("\n");
}
 
struct node* bin_HEAP_EXTRACT_MIN(struct node* H1) {
    int min;
    struct node* t = NULL;
    struct node* x = H1;
   
    struct node* p;
    Hr = NULL;
    if (x == NULL) {
        printf("\nNOTHING TO EXTRACT");
        return x;
    }
    //    int min=x->n;
    p = x;
    while (p->sibling != NULL) {
        if ((p->sibling)->id < min) {
            min = (p->sibling)->id;
            t = p;
            x = p->sibling;
        }
        p = p->sibling;
    }
    if (t == NULL && x->sibling == NULL)
        H1 = NULL;
    else if (t == NULL)
        H1 = x->sibling;
    else if (t->sibling == NULL)
        t = NULL;
    else
        t->sibling = x->sibling;
    if (x->child != NULL) {
        REVERT_LIST(x->child);
        (x->child)->sibling = NULL;
    }
    H = bin_HEAP_UNION(H1, Hr);
    return x;
}
 
int REVERT_LIST(struct node* y) {
    if (y->sibling != NULL) {
        REVERT_LIST(y->sibling);
        (y->sibling)->sibling = y;
    } else {
        Hr = y;
    }
}
 
struct node* FIND_NODE(struct node* H, int k) {
    struct node* x = H;
    struct node* p = NULL;
    if (x->id == k) {
        p = x;
        return p;
    }
    if (x->child != NULL && p == NULL) {
        p = FIND_NODE(x->child, k);
    }
 
    if (x->sibling != NULL && p == NULL) {
        p = FIND_NODE(x->sibling, k);
    }
    return p;
}
 
int bin_HEAP_DECREASE_KEY(struct node* H, int i, int k) {
    int temp;
    int temp2;
    int temp3;
    int temp4;
    int temp5;
    double temp6;
    struct node* p;
    struct node* y;
    struct node* z;
    p = FIND_NODE(H, i);
    
    p->id = k;
    y = p;
    z = p->parent;
    while (z != NULL && y->id < z->id) {
        temp = y->id;
        y->id = z->id;
        z->id = temp;
        temp2 = y->e;
        y->e = z->e;
        z->e = temp2;
        temp3 = y->t_arrange;
        y->t_arrange = z->t_arrange;
        z->t_arrange = temp3;
        temp4 = y->t_change;
        y->t_change = z->t_change;
        z->t_change = temp4;
        temp5 = y->e_new;
        y->e_new = z->e_new;
        z->e_new = temp5;
        temp6 = y->priority;
        y->priority = z->priority;
        z->priority = temp6;
        y = z;
        z = z->parent;
    }
    
}

int bin_HEAP_UPDATE(struct node* H, int i, int k) {
    int temp;
    struct node* p;
    struct node* y;
    struct node* z;
    p = FIND_NODE(H, i);
    
    p->e = k;
    y = p;
    z = p->parent;
    while (z != NULL && y->id < z->id) {
        temp = y->id;
        y->id = z->id;
        z->id = temp;
        y = z;
        z = z->parent;
    }
    
}

int bin_HEAP_UPDATE_PRIORITY(struct node* H, int i, double k) {
    int temp;
    struct node* p;
    struct node* y;
    struct node* z;
    p = FIND_NODE(H, i);
    
    p->priority = k;
    y = p;
    z = p->parent;
    while (z != NULL && y->id < z->id) {
        temp = y->id;
        y->id = z->id;
        z->id = temp;
        y = z;
        z = z->parent;
    }
    
}
 
int bin_HEAP_DELETE(struct node* H, int k) {
    struct node* np;
    if (H == NULL) {
        printf("\nHEAP EMPTY");
        return 0;
    }
 
    bin_HEAP_DECREASE_KEY(H, k, -1000);
    np = bin_HEAP_EXTRACT_MIN(H);
    
}


 
int main() {
	int id_input,e_input,t_arrange_input,t_change_input,e_new_input;
	int proces_in_input = 0;
	int deleted = 0;
	int waiting_time = 0;
	int e_max = 4;
	double AWT[10];
    char c;
    
	FILE *file;
	struct node* s;
	struct node* a = NULL;
	struct node* b = NULL;
	
	// determine number of process in input
	file = fopen("input.txt" , "r");
	
		if(file != NULL){
					
			while(!feof(file)){
				
				c =fgetc(file);
				fscanf(file, "%d", &id_input);
				fscanf(file, "%d", &e_input);
				fscanf(file, "%d", &t_arrange_input);
				fscanf(file, "%d", &t_change_input);
				fscanf(file, "%d", &e_new_input);
				c =fgetc(file);
				
				proces_in_input++;
				
				
					
			}
			
				
		}
		else {
			printf("There is no file!");
		}
		
		fclose(file);
	// get data's from file
	int k = 0;
	
	int inserted[30];
	int first_time[30];
    int e_in_process[30];
    int changed[30];
	while(k<30){
		inserted[k] = 0;
		first_time[k] = 0;
		e_in_process[k] = 0;
		changed[k] = 0;
		k++;
	}
	k=0;
	while(k<10){
		AWT[k] = 0;
		k++;
	}
	
	
	
    double x,y;
    
    
	
    
    int i = 1;
    
    int timer = 0;
    int q = 1;
	while(q<=10){
		
		file = fopen("input.txt" , "r");
	
		if(file != NULL){
					
			while(!feof(file)){
				
				c =fgetc(file);
				fscanf(file, "%d", &id_input);
				fscanf(file, "%d", &e_input);
				fscanf(file, "%d", &t_arrange_input);
				fscanf(file, "%d", &t_change_input);
				fscanf(file, "%d", &e_new_input);
				c =fgetc(file);
				
				s = CREATE_NODE(id_input,e_input,t_arrange_input,t_change_input,e_new_input);
	    		if(t_arrange_input <= timer && inserted[id_input-1] == 0){
					
	    			H = bin_HEAP_INSERT(H, s);
	    			inserted[s->id-1] = 1;
	    			
				}
				
					
			}
			
				
		}
		else {
			printf("There is no file!");
		}
		
		fclose(file);
		
		while(deleted<proces_in_input){
	    	// if process arragament is less than timer insert it heap
	    	file = fopen("input.txt" , "r");
	
			if(file != NULL){
						
				while(!feof(file)){
					
					c =fgetc(file);
					fscanf(file, "%d", &id_input);
					fscanf(file, "%d", &e_input);
					fscanf(file, "%d", &t_arrange_input);
					fscanf(file, "%d", &t_change_input);
					fscanf(file, "%d", &e_new_input);
					c =fgetc(file);
					
					s = CREATE_NODE(id_input,e_input,t_arrange_input,t_change_input,e_new_input);
		    		if(t_arrange_input <= timer && inserted[id_input-1] == 0){
						
		    			H = bin_HEAP_INSERT(H, s);
		    			inserted[s->id-1] = 1;
		    			
					}
					
						
				}
				
					
			}
			else {
				printf("There is no file!");
			}
			
			fclose(file);
	    	;
	    	
	    	
	    	a=NULL;
	    	b=NULL;
	    	i = 1;
	    	// first program is looking for any interrupt
	    	
	    	while(i<=proces_in_input){
	    		a = FIND_NODE(H,i);
				if( a!=NULL && a->t_change > 0 && a->t_change <= timer && changed[a->id-1] == 0){
				
					
					bin_HEAP_UPDATE(H,a->id,a->e_new);
					if(first_time[a->id-1] == 0){
						bin_HEAP_UPDATE_PRIORITY(H,a->id,a->e_new);	
					}
					else{
						y = 2.0 * a->e / 3 / e_max;
						y = -y * y * y;
				    	y = exp(y);
				    	
						x = ( 1 / y ) * a->e ;	
						bin_HEAP_UPDATE_PRIORITY(H,a->id,x);
					}
					changed[a->id - 1] = 1;
				}
				i++;
			}
			// first determine first node which is in the process
			
			i=1;
	    	a  = FIND_NODE(H,i);
	    	while(a==NULL){
	    		a = a = FIND_NODE(H,i);
	    		i++;
	    		
			}
	    	// when program find first node  then it will compare it with other process
			while(i<=proces_in_input){
				
	    		b = FIND_NODE(H,i);
				
				
	    		if(b==NULL){
	    			i++;
				}
	    		else if(a->priority > b->priority && b->t_arrange <= timer ){
	    			a = b;
	    			i++;
				}
				else if(a->priority == b->priority && b->t_arrange <= timer){
					if ( a->t_arrange > b->t_arrange){
						a = b;
					
					}
					i++;
				}
				else{
					i++;
				}
			}
			
			/*
			
				 after program find process which will process  it will consider 3 condition
			(  1  )  for 1. condition there are 4 provision
						1.there should be  an interrup on node
						2.program will decrease e value by q  this decrease shouldnt exceed t_change - timer value
						3.t_change and timer shouldnt be equal
						4.t_change should be bigger than timer
						
						*/
			if( a != NULL &&a->t_arrange<=timer) {
				if(a->t_change >=0 && a->t_change - timer < q && a->t_change - timer != 0 && a->t_change > timer){
						timer = a->t_change;
						e_in_process[a->id-1] = e_in_process[a->id-1] + a->t_change - timer + 1;
						bin_HEAP_UPDATE(H,a->id,a->e-a->t_change+timer);
				}
				
				
				/* 
				
				(  2  )  if  node's e value is bigger than  quantum ( q )   decrease e value by q
							
							( or e and q is equal  ( it actually doesnt matter i can put " = " in  3. condition) )
				*/
				
				else if(a->e >= q){
					
					timer = timer + q;
					e_in_process[a->id-1] = e_in_process[a->id-1] + q;
					bin_HEAP_UPDATE(H,a->id,a->e-q);
	
					
				}
				
				/*
				
				(  3  )  if  node's e is smaller than q  decrease e value by itself  ( it will be 0 )
				*/
				else if ( a->e < q){
					
					
					timer = timer + a->e;
					e_in_process[a->id-1] = e_in_process[a->id-1] + a->e;
					bin_HEAP_UPDATE(H,a->id,a->e-a->e);
					
					
				}
				
				// after that we need new priority value for this node
				first_time[a->id-1]=1;
				y = 2.0 * a->e / 3 / e_max;
				y = -y * y * y;
			    y = exp(y);
			    
				x = ( 1 / y ) * a->e ;
			    
			    bin_HEAP_UPDATE_PRIORITY(H,a->id,x);
			    
				a = FIND_NODE(H,a->id);
				
				// if node is 0 then delete it 
			    if( a != NULL && a->e == 0){
					waiting_time = waiting_time + (timer - a->t_arrange - e_in_process[a->id-1] );
					
					bin_HEAP_DELETE(H,a->id);
					deleted++;
				}	
				
			}
			
			
					
			
		}
			
		
		deleted =0;
	    k = 0;
		while(k<30){
			inserted[k] = 0;
			first_time[k] = 0;
			e_in_process[k] = 0;
			changed[k] = 0;
			k++;
		}
		//  write quantum value and his waiting time 
	    
	    AWT[q-1] = 1.0 * waiting_time / proces_in_input;
	    printf("q = %d AWT = %f\n" , q, AWT[q-1]);
	    waiting_time = 0;
	    timer = 0;
		q++;
	}
	
	i = 0;
	k = 1;
	
	// determine best quantum value
	while(i<9){
		if(AWT[i] > AWT[i+1]){
			k = i+1;
		}
		i++;
	}
    
    printf("\nThe best q = %d" , k);
    
    fclose(file);
    
    
}
