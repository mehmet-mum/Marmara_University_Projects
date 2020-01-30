#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <time.h>


void prime_numbers(int numbers, int number_of_threads);
int check_prime(int the_number);
void print_primes(int number);
void negate(int number);
void memory_allocate(int number);

int **prime_array;      // array for keep prime numbers  and  which thread calculated it
// prime_array[i][0]  keeps prime or not
// prime_array[i][1]  keeps which thread calculate it 


int main(){
    int number;
    printf("Enter a number: ");
    scanf("%d",&number);        // get number from user
    printf("\n");

    if( number < 2 ){
        printf("The number is less than 2, please give a number which is bigger than 2\n");
        return 1;
    }

    srand(time(0));

    int num_of_threads = (rand() % 3) + 3;      // random number of threads between 3 - 5

    
    memory_allocate(number);    // memory allocate for the array
    
    negate(number);     // negate uncalculated numbers yet

    prime_numbers(number, num_of_threads);      // calculate prime numbers


    print_primes(number);       // print prime numbers
}


void prime_numbers(int number, int number_of_threads){
    int i, check,tid; 
    
    #pragma omp parallel for private(tid) schedule( dynamic) num_threads(number_of_threads)
    for( i = 2; i < number; i++){
        tid = omp_get_thread_num();
        check = check_prime(i);         // check primeness
        if ( check == 1 ){
            prime_array[i][0] = 1;      // if prime mark as 1
            prime_array[i][1] = tid;    // and the thread
        }
        else{
            prime_array[i][0] = 0;      // if not mark as 0
        }
    }
}


int check_prime(int the_number){
    int i;
    // look for previous prime numbers to decide primeness
    for( i = 0; i < the_number; i++){
        if ( prime_array[i][0] == -1 ){     // if decision is negate 
            while(prime_array[i][0] == -1); // wait for the number's decision       // busy wait // 

            if ( prime_array[i][0] == 1){   // after decision if the number is prime
                if ( the_number % i == 0 )  // and divisible to the number return 0
                    return 0;
            }
        }
        else if ( prime_array[i][0] == 1){  // if the number already decided to prime 
            if ( the_number % i == 0 )      // check whether it is divisible or not  // if divisible return 0
                return 0;
        }
    }

    // check all previous prime numbers and if the number is not divisible none of them then, return 1
    return 1;
}

// print prime numbers
void print_primes(int number){
    int i;
    for (i = 0; i<number; i++){
        if ( prime_array[i][0] == 1)
            printf("Thread %d Prime %d\n", prime_array[i][1] , i);
    }
}

void negate(int number){
    prime_array[0][0] = 0;      //0
    prime_array[1][0] = 0;      //1   is not prime 

    int i = 2;
    for( i ; i < number; i++)
        prime_array[i][0] = -1;     // negate numbers which are bigger than 1

}

// allocate memory for the array
// prime_array[number][2]
void memory_allocate(int number){
    prime_array = malloc( number* sizeof( int*));

    int i;
    for( i = 0; i < number; i++)
        prime_array[i] = calloc( 2, sizeof( int ));
}