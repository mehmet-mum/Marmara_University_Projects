#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <time.h>

int number_of_customers, number_of_sellers,number_of_simulation_day,number_of_products;

//  barrier variable and its mutex for customers
pthread_mutex_t barrier_seller = PTHREAD_MUTEX_INITIALIZER;
int barrier_count_seller;

//  barrier variable and its mutex for sellers
pthread_mutex_t barrier_customer = PTHREAD_MUTEX_INITIALIZER;
int barrier_count_customer;


// total number of transaction that have been taken by sellers
int seller_tranc = 0;
pthread_mutex_t seller_tranc_mutex = PTHREAD_MUTEX_INITIALIZER;  // its mutex

// total number of transaction that have been sent by customers
int customer_tranc = 0;
pthread_mutex_t customer_tranc_mutex = PTHREAD_MUTEX_INITIALIZER; // its mutex

// mutex for each seller
pthread_mutex_t *seller_mutex;

// mutex for each product
pthread_mutex_t *product_mutex;

// keeps information about number of bought / reservated / canceled  production 
int **used_product;
pthread_mutex_t *used_product_mutex;

// number of each product beginning of a day
int *products;

// number of each avaliable products
int *available_products;

// info of each customer's operation limit and reservation limit
int **customer_informations;

// info of each customer's current operation number and current reservation number
int **current_informations;

/* ---- Job transaction ---- */ 
/* ---- [operation_id] - [product_id] - [number_of_product] ---- */
// info transaction from customer to seller
int **job_transaction;

// job for seller // customers writes their own id to seller work so, sellers understands which customer is doing operation
int *seller_work;

// availability for customer
int *customer_availability;

// condition variable for day ends
int day_over_customer = 0;		// for customers

int day_over_seller = 0;		// for sellers

int simulation_begins = 0;		// simulation begins condition variable

// current day
int current_day = 1;

int ***reservations;	// all reservations are kept in this variable 

int *customer_reservations;		// keeps number of reservations for each customer

//customer threads
void *customer(void *threadid){
	long thread_id = (long) threadid;
	
	srand(time(0)+thread_id);	
	int random_operation;	
	int random_product;
	int random_product_number;
	int locked, i = 0;

	while(simulation_begins == 0);		// wait for simulation begins

	// maintain simulation for given days
	while( current_day <= number_of_simulation_day ) {

		// if day doesn't end continue	
		if ( day_over_customer == 1 ){

			// if customer doesn't reach his operation limit then continue
			if( current_informations[thread_id][0] < customer_informations[thread_id][0] ){
				// take a random operation
				random_operation = rand() % 3;

				// if there is no reservation so, protect for cancel operation
				while( customer_reservations[thread_id] == 0 && random_operation == 2){
					random_operation = rand() % 3;
				}
				
				// if a seller is working for the customer , so the customer should wait until
				// he is available or day end
				while( customer_availability[thread_id] == 1 && day_over_customer == 1);
				
				if ( random_operation == 1 && customer_informations[thread_id][1] == current_informations[thread_id][1] )
					random_operation = 0;


				// if day is not over then try to lock a seller
				if ( day_over_customer == 1 && current_day <= number_of_simulation_day){
					
					// according to operation fill the customer's transaction table

					// buy operation
					if( random_operation == 0){
						random_product = rand() % number_of_products;
						random_product_number = rand() % products[random_product];
						job_transaction[thread_id][0] = random_operation;
						job_transaction[thread_id][1] = random_product;
						job_transaction[thread_id][2] = random_product_number + 1;
					}

					// reserve operation
					else if( random_operation == 1){
						random_product = rand() % number_of_products;
						random_product_number = rand() % (customer_informations[thread_id][1] - current_informations[thread_id][1]);
						job_transaction[thread_id][0] = random_operation;
						job_transaction[thread_id][1] = random_product;
						job_transaction[thread_id][2] = random_product_number + 1;
					}

					// cancel reserve operation
					else if ( random_operation == 2){
						job_transaction[thread_id][0] = random_operation;
						customer_reservations[thread_id]--;
					}
					locked = -1;
					
					// try to lock until lock some one or day end
					while( locked != 0 && current_day <= number_of_simulation_day && day_over_customer == 1){
						locked = pthread_mutex_trylock(&seller_mutex[i]);
						
						// if lock is succesfull
						if( locked == 0 ){
							seller_work[i] = thread_id;		// write seller_work[seller_id] to customer_id

							// increase customer_transaction number
							pthread_mutex_lock(&customer_tranc_mutex);
							customer_tranc++;
							pthread_mutex_unlock(&customer_tranc_mutex);
							
							customer_availability[thread_id] = 1;		// set availability to not available
							current_informations[thread_id][0]++;		// increase current 
						}
						
						// try to lock next seller
						if ( i + 1 == number_of_sellers ){
							i = 0;
						}
						else{
							i++;
						}
					}
				}				
			}
		}

		//	if day ends  reach  the end line 
		else if ( day_over_customer == 0 ){
			
			pthread_mutex_lock(&barrier_customer);
			barrier_count_customer++;
			pthread_mutex_unlock(&barrier_customer);
			
			while( day_over_customer == 0 );
			
			pthread_mutex_lock(&barrier_customer);
			barrier_count_customer--;
			pthread_mutex_unlock(&barrier_customer);
			
		}
			


	}
	
	pthread_exit(NULL);
	
		
}



//seller threads
void *seller(void *threadid){
	long thread_id = (long) threadid;	

	int customer_operation;
	int customer_id;	
	int product_id;
	int product_number;

	while(simulation_begins == 0);		// wait for simulation begin

	// maintain simulation for given days
	while ( current_day <= number_of_simulation_day ){
		
		// if day is not over continue
		if ( day_over_seller == 1 ){
			
			while( seller_work[thread_id] == -1 ); // wait for a work


			// if seller got a work from a customer
			if ( seller_work[thread_id] >= 0 ){

				customer_id = seller_work[thread_id];		//  take customer id from seller_work[thread_id]
				customer_operation = job_transaction[customer_id][0];	// take op. product_id and product number
				product_id = job_transaction[customer_id][1];
				product_number = job_transaction[customer_id][2];
				seller_work[thread_id] = -1;					// re_initialize seller_work 
				pthread_mutex_unlock(&seller_mutex[thread_id]);		// unlock seller_mutex
				
				// increase seller transaction
				pthread_mutex_lock(&seller_tranc_mutex);
				seller_tranc++;
				pthread_mutex_unlock(&seller_tranc_mutex);
				

				// customer's buy operation
				if( customer_operation == 0 ){
					// lock product 
					pthread_mutex_lock(&product_mutex[product_id]);
					if ( available_products[product_id] < product_number ){
						pthread_mutex_unlock(&product_mutex[product_id]);								
					}
					else{
						// if avaliable product then decrease number and unlock product
						available_products[product_id] = available_products[product_id] - product_number;
						pthread_mutex_unlock(&product_mutex[product_id]);

						// lock used product and update values
						pthread_mutex_lock(&used_product_mutex[product_id]);
						used_product[product_id][0] = used_product[product_id][0] + product_number;
						pthread_mutex_unlock(&used_product_mutex[product_id]);
					}
				}
				// customer's reserve operation
				else if ( customer_operation == 1 ){
					pthread_mutex_lock(&product_mutex[product_id]);
					if ( available_products[product_id] < product_number ){
						pthread_mutex_unlock(&product_mutex[product_id]);								
					}
					else{
						available_products[product_id] = available_products[product_id] - product_number;
						pthread_mutex_unlock(&product_mutex[product_id]);
						current_informations[customer_id][1] = current_informations[customer_id][1] + product_number;
						reservations[customer_id][customer_reservations[customer_id]][0] = product_id;
						reservations[customer_id][customer_reservations[customer_id]][1] = product_number;
						customer_reservations[customer_id]++;
						pthread_mutex_lock(&used_product_mutex[product_id]);
						used_product[product_id][1] = used_product[product_id][1] + product_number;
						pthread_mutex_unlock(&used_product_mutex[product_id]);
					}
				}
				// customer's cancel operation
				else if ( customer_operation == 2 ){
					// to cancel a reservation get product_id and product_number from reservations
					product_id = reservations[customer_id][customer_reservations[customer_id]][0];
					product_number = reservations[customer_id][customer_reservations[customer_id]][1];
					
					// lock product and update value
					pthread_mutex_lock(&product_mutex[product_id]);
					available_products[product_id] = available_products[product_id] + product_number;
					pthread_mutex_unlock(&product_mutex[product_id]);
					
					// ----- //
					pthread_mutex_lock(&used_product_mutex[product_id]);
					used_product[product_id][2] = used_product[product_id][2] + product_number;
					pthread_mutex_unlock(&used_product_mutex[product_id]);
				}
				else{
					printf("Undefined operation\n");
				}
				// write 
				printf("Customer = %d , Op. = %d , Day = %d , Product = %d , %d \n",customer_id, customer_operation, current_day, product_id, product_number);
				customer_availability[customer_id] = 0;	// set availability to available for the customer
			}
		}

		// if day ends all seller should reach this point
		else if ( day_over_seller == 0 ){
			
			pthread_mutex_lock(&barrier_seller);
			barrier_count_seller++;
			pthread_mutex_unlock(&barrier_seller);
			
			while( day_over_seller == 0 );
			
			pthread_mutex_lock(&barrier_seller);
			barrier_count_seller--;
			pthread_mutex_unlock(&barrier_seller);
			
		}
	}
	pthread_exit(NULL);
	

}

// initialize variables according to number_of_customer
void customer_var_initialization(){
	int i = 0;

	// create [number_of_customers][2] global_matrix   
    customer_informations = calloc( number_of_customers, sizeof (int *));
    for (i = 0; i < number_of_customers; i++)
     	customer_informations[i] = calloc( 2, sizeof (int));

	// create [number_of_customers][2] global_matrix   
    current_informations = calloc( number_of_customers, sizeof (int *));
    for (i = 0; i < number_of_customers; i++)
     	current_informations[i] = calloc( 2, sizeof (int));

	// availability array for every customer
	customer_availability = calloc( number_of_customers, sizeof (int));

	// transaction
	job_transaction = calloc( number_of_customers, sizeof (int *));
       	for (i = 0; i < number_of_customers; i++)
      	 	job_transaction[i] = calloc( 3, sizeof (int));

	// number of reservations for every customer
	customer_reservations = calloc( number_of_customers, sizeof(int));

}

void seller_var_initialization(){
	int i = 0;

	// allocate memory for seller_work and initialize all to -1
	seller_work	 = calloc( number_of_sellers, sizeof (int));
	for(i = 0; i < number_of_sellers; i++)
		seller_work[i] = -1;
	
	// initialize seller mutex for each seller
	seller_mutex = malloc( sizeof(pthread_mutex_t) * number_of_sellers);
	for(i = 0; i < number_of_sellers; i++)
		pthread_mutex_init(&seller_mutex[i], 0);	


	

}


void product_var_initialization(){
	int i;

	// allocate memory for products
	products = calloc( number_of_products, sizeof (int));
	available_products = calloc( number_of_products, sizeof (int));
	
	// initialize product mutex for each product
	product_mutex = malloc( sizeof(pthread_mutex_t) * number_of_products);
	for(i = 0; i < number_of_products; i++)
		pthread_mutex_init(&product_mutex[i], 0);

	// allocate memory for used products 
	// used_product[number_of_product][3] 	first column for buy, second for rezerve, third for cancel
	used_product = calloc( number_of_products, sizeof (int *));
       	for (i = 0; i < number_of_products; i++)
      	 	used_product[i] = calloc( 3, sizeof (int));

	// of course mutex for them
	used_product_mutex = malloc( sizeof(pthread_mutex_t) * number_of_products);
	for(i = 0; i < number_of_products; i++)
		pthread_mutex_init(&used_product_mutex[i], 0);

		
}

// re_initialize for necessary variables
void re_initialization(){
	int i;

	for ( i = 0; i < number_of_products; i++ )
		available_products[i] = products[i];

	for ( i = 0; i < number_of_customers; i++ ){
		current_informations[i][0] = 0;
		current_informations[i][1] = 0;
		customer_availability[i] = 0;
		customer_reservations[i] = 0;
	}

	for( i = 0; i < number_of_sellers; i++){
		pthread_mutex_unlock(&seller_mutex[i]);
		seller_work[i] = -1;
	}

	for( i = 0; i < number_of_products; i++){
		used_product[i][0] = 0;
		used_product[i][1] = 0;
		used_product[i][2] = 0;
	}

	customer_tranc = 0;
	seller_tranc = 0;
}

int main(){
	// redirect to txt file
	int log_file = open("log.txt", O_WRONLY | O_TRUNC | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
    dup2(log_file,STDOUT_FILENO);

	int i;

	FILE *file;
	
	file = fopen("input.txt" , "r");
		
	/*-------------  Read informations from input file and fill necessary fields --------------*/	
	
	if(file != NULL){
		fscanf(file, "%d", &number_of_customers);		// take number of customers	
		
		fscanf(file, "%d", &number_of_sellers);			// take number of sellers	
		
		fscanf(file, "%d", &number_of_simulation_day);		// take number of simulation day

		fscanf(file, "%d", &number_of_products);		// take number of products
		

		

		customer_var_initialization();
		seller_var_initialization();
		product_var_initialization();
		

		// take number of each product and put it into products array
		int trash;
		
		for( i = 0; i < number_of_products; i++ ){
			fscanf(file, "%d", &trash);
			products[i] = trash;
			available_products[i] = trash;
		}
		
		
		// take number of each product and put it into products array
		for( i = 0; i < number_of_customers; i++ ){
			int customer_id;
			fscanf(file, "%d", &customer_id);
			fscanf(file, "%d", &customer_informations[customer_id-1][0]);		// [i][0] keeps # of operations allowed
			fscanf(file, "%d", &customer_informations[customer_id-1][1]);		// [i][1] keeps # of reservable products
		}


		// allocate memory for reservation 3D array
		reservations = calloc( number_of_customers, sizeof( int **));
		for( i = 0; i < number_of_customers; i++)
		reservations[i] = calloc (customer_informations[i][0], sizeof( int *));

		for( i = 0; i < number_of_customers; i++){
			int max_op = customer_informations[i][0];
			int j;
			for( j = 0; j < max_op; j++)
				reservations[i][j] = calloc(2, sizeof(int));
		}

	}
	else {
		printf("There is no file!");
	}
	fclose(file); 

	

	/*----------------------------------------------------------------------------------------------------------------*/

	// create therads
 	pthread_t customer_thread[number_of_customers];
    pthread_t seller_thread[number_of_sellers];
    long t;

	for ( t=0; t < number_of_customers; t++)
       	pthread_create(&customer_thread[t], NULL, customer, (void *)t);
    
    for ( t=0; t < number_of_sellers; t++)
       	pthread_create(&seller_thread[t], NULL, seller, (void *)t);
	

	// simulation begins
	while( current_day <= number_of_simulation_day ) {
		printf("DAY : %d\n", current_day);
		day_over_customer = 1;
		day_over_seller = 1;
		simulation_begins = 1;
		sleep(3);		// day time
		day_over_customer= 0;	//  day ends for customers so they can not send a work to sellers

		while( barrier_count_customer != number_of_customers ); // first wait for customers to reach end of the day line
		
		// when all customers reach the point
		// seller should reach the point
		// for the sellers who doesn't have a work set their seller_work[i] value to -2 so they can exit the loop
		for ( i = 0; i < number_of_sellers; i++){
			if ( seller_work[i] == -1 )
				seller_work[i] = -2;		// to take them out from while( seller_work[thread_id] == -1 );  loop
		}
		day_over_seller = 0;
		while ( barrier_count_seller != number_of_sellers ); 	// then wait for sellers to finish their current jobs 
		
		// print number of transactions from customer side and also seller side
		printf("Customer_transactions: %d , Seller_transactions : %d\n", customer_tranc, seller_tranc);
		
		// print product informations at the end of the day 
		for( i = 0; i < number_of_products; i++)
			printf("Product_%d		%d		%d		%d		-- Remaining product = %d\n",i+1,used_product[i][0],used_product[i][1],used_product[i][2], available_products[i]);

		re_initialization(); // re_initialize the variables
		 
		current_day++;	// next day :)
	}	
	day_over_customer = 1;
	day_over_seller = 1;
	

	// at the end wait for customers and sellers 
	for ( t=0; t< number_of_customers; t++)
    	pthread_join(customer_thread[t],NULL);

    for ( t=0; t< number_of_sellers; t++)
    	pthread_join(seller_thread[t],NULL);



	
}
