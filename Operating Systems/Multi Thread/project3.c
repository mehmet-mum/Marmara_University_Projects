#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>

#define c_mode (S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH)

pthread_mutex_t generate_mutex = PTHREAD_MUTEX_INITIALIZER; //set up generate_mutex
pthread_mutex_t log_mutex = PTHREAD_MUTEX_INITIALIZER;//set up log_mutex
pthread_mutex_t mod_mutex = PTHREAD_MUTEX_INITIALIZER; //set up mod_mutex
pthread_mutex_t add_mutex = PTHREAD_MUTEX_INITIALIZER;//set up add_mutex

pthread_mutex_t queue1_mutex = PTHREAD_MUTEX_INITIALIZER; //set up queue1_mutex
pthread_mutex_t queue2_mutex = PTHREAD_MUTEX_INITIALIZER; //set up queue2_mutex
pthread_mutex_t print_mutex = PTHREAD_MUTEX_INITIALIZER; //set up print_mutex


// 5x5 sub_matrix
struct sub_matrices{
    
    int sub_matrix[5][5];
};



struct sub_matrices *sub_matrix_array;  // array for sub_matrices 
struct sub_matrices *mod_matrix_array;  // array for mod_matrices




int **global_matrix;   // global_matrix
int global_matrix_size = 0; // size of global_matrix

int submatrix_number;  // number of sub_matrices

int sum = 0;  // global sum



int generate_counter = 0;   // hangi generator thread hangi submatrix in üreteceğini belirler
int log_counter = 0;        // hangi log thread hangi submatrix i okuyacağını belirler
int mod_counter = 0;        // hangi thread hangi submatrix i mod edeceğini belirler
int add_counter = 0;        // hangi thread hangi mod edilen submatrix i okuyacağını belirler

int existence_global_matrix = 0;    // boolean variable for creating global matrix
int existence_queue1 = 0;           // boolean variable for creating queue1         
int existence_queue2 = 0;           // boolean variable for creating gueue2


int *queue1;        // queue1
int queue1_index = 0;   // queue1 index

int *queue2;        // queue2 
int queue2_index = 0;   // queue2 index



// generate thread works
void *generator_function(void *threadid){
    int generating_submatrix = 0;  // local variable  hangi thread in hangi submatrix i oluşturduğu
    int i , j, random;
    
    // thread id
    long tid;
    tid = (long)threadid;
    
    srand(time(NULL) * tid);    //random seed
    
    // oluşturulan submatrix sayısı toplam sub_matrix i geçerse dursun 
    while ( generate_counter < submatrix_number ){
        
        // thread burda hangi matrix i oluşturacağını belirler
        pthread_mutex_lock(&generate_mutex);
        generating_submatrix = generate_counter;        // generate_counter değerini  local değere ver  generate counter i bir attır
        generate_counter++;
        
        // first thread that enters critical section creates queue1
        if ( existence_queue1 == 0 ){
            sub_matrix_array = calloc( submatrix_number, sizeof (*sub_matrix_array));
            queue1 = (int *) calloc( submatrix_number, sizeof (int));
            existence_queue1 = 1;
            
        }
        pthread_mutex_unlock(&generate_mutex);
        sleep(0.4);// sleep olmadın mı belli bi ara sadece biri çalışıyo
        
        // thread in oluşturacağı submatrix in indexi  toplam submatrixten büyükse boşuna oluşturmasın
        if ( generating_submatrix < submatrix_number ){
            
            
            for ( i = 0; i<5;i++){
                for ( j=0; j<5; j++){
                    random = rand() % 99 + 1;
                    
                    // oluşturduğu random değeri direk submatrix lerin oluşturduğu array in içine atıyo 
                    sub_matrix_array[generating_submatrix].sub_matrix[i][j] = random;
                }
                
            }   
            
            // bitirdiği zamanda  queue yi güncelliyo  hangi submatrix i bitirdiyse bitirdiği matrixin indexini queue1 e atıyo
            pthread_mutex_lock(&queue1_mutex);
            queue1[queue1_index] = generating_submatrix;        
            queue1_index++;
            pthread_mutex_unlock(&queue1_mutex);
            
            pthread_mutex_lock(&print_mutex);
            // print
            printf("Generator_%ld generated following matrix:\n", tid);
            printf("[");
            for ( i = 0; i < 5; i++){
                for (j = 0; j < 5; j++){
                    printf("%d",sub_matrix_array[generating_submatrix].sub_matrix[i][j]);
                    if ( i != 4 || j != 4 ){
                        printf(",");
                    }
                }
                if ( i != 4)
                    printf("\n ");
            }
            printf("]\n");
            printf("This matrix is [%d,%d] submatrix\n\n", generating_submatrix/(global_matrix_size/5), generating_submatrix%(global_matrix_size/5));
            
            pthread_mutex_unlock(&print_mutex);
        
        }
        
        
        /* özetle
         * iki tane arrayim var biri submatrixlerin olduğu array  diğeri indexlerin olduğu array 
         * her thread hangi submatrix i oluşturacağını belirliyor  ( in critical section )
         * random sayıları direk sub_matrices array in içine yazıyo  oluşturdukca her thread yazıyo oraya 
         * her thread farklı submatrix i oluşturduğu için submatrix arrayin içinde kendi alanına yazıyolar
         * sonrada bitiren thread bitirdiği submatrix in index ini queue1 e yazıyo ( in critical section ) 
        */
    }
    pthread_exit(NULL);
}


// log tread works 
void *log_function(){
    int log_index = 0;  // local variable  hangi log thread in hangi oluşturulan submatrix i okuduğu
    int i , j;
    int big_matrix_row;
    int big_matrix_column;
    
    // okunan submatrix sayısı toplam submatrix sayısını geçerse dursun
    while ( log_counter < submatrix_number ){
        
        pthread_mutex_lock(&log_mutex);         // log thread in hangi submatrix i okuyacağını belirler
        log_index = log_counter;        // hangisini okuyacağı local de tutulur
        log_counter++;              // geneldekinde de sonraki submatrixe geçilir
        
        // first log thread creates global matrix
        if ( existence_global_matrix == 0){
            // create [n][n] global_matrix   
            global_matrix = calloc( global_matrix_size, sizeof (int *));
            int i;
            for (i = 0; i < global_matrix_size; i++)
                global_matrix[i] = (int *) calloc( global_matrix_size, sizeof (int));
            
            existence_global_matrix = 1;
        }
        pthread_mutex_unlock(&log_mutex);
        sleep(0.4);// sleep olmadın mı belli bi ara sadece biri çalışıyo
        
        
        // thread in okuyacağı submatrix in indexi  toplam submatrixten büyükse boşuna okumasın segmentation fault alabiliriz  
        if ( log_index < submatrix_number ){
            
            
            
            // log thread in okuyacağı submatrixin oluşturulması tamamlanmadıysa beklesin
            while ( log_index >= queue1_index );
            
            
            log_index = queue1[log_index]; // hangi submatrix i okuyacağını  queue den al
            
    
            big_matrix_row = log_index/(global_matrix_size/5);       // row unu belirle   
            big_matrix_column = log_index%(global_matrix_size/5);       // column u belirle
            
            for ( i = 0; i<5;i++){
                for ( j=0; j<5; j++){
                    
                    // submatrix i oku direk global matrix e yaz 
                    global_matrix[i + big_matrix_row * 5][j + big_matrix_column * 5] = sub_matrix_array[log_index].sub_matrix[i][j];
                }
                
            }             
            
        }
        
        
    }
    
    /* özetle
     * log thread ler hangi submatrixi okuyacağını belirler ( in critical section )
     * aslında queue 1 de bitirilme sırasına göre tutuluyor indexler 
     * kritik section da belirlenen integer 3 diyelim 
     * queue[3] tede 8 var diyelim  submatrix in indexi 8 oluyo  8. submatrix i okuyup yazıyr
     * belirlendikten sonra paralel bir şekilde okuduklarını direk global matrix e yazarlar 
     * */
    
    pthread_exit(NULL);
}

// mod thread works
void *mod_function(void *threadid){
    int mod_index = 0;      // local variable  hangi mod thread in hangi oluşturulan submatrix i okuduğu ve yazdığı
    int i , j;
    
    long tid;
    tid = (long)threadid;
    
    // okunan submatrix sayısı toplam submatrix sayısını geçerse dursun
    while(mod_counter < submatrix_number ){
        
        // mod matrixin log da ki ve generate de ki olduğu gibi bir tane submatrix belirleniyor
        pthread_mutex_lock(&mod_mutex);
        mod_index = mod_counter;
        mod_counter++;
        
        // first mod thread creates mod matrix array and queue2  
        if ( existence_queue2 == 0 ){
            mod_matrix_array = calloc( submatrix_number, sizeof (*sub_matrix_array));
            queue2 = (int *) calloc( submatrix_number, sizeof (int));
            existence_queue2 = 1;
            
        }
        
        pthread_mutex_unlock(&mod_mutex);
        sleep(0.4);// sleep olmadın mı belli bi ara sadece biri çalışıyo
        
        
        // thread in okuyacağı submatrix in indexi  toplam submatrixten büyükse boşuna okumasın segmentation fault alabiliriz 
        if ( mod_index < submatrix_number ) {
            
            // mod thread in okuyacağı submatrixin oluşturulması tamamlanmadıysa beklesin
            while ( mod_index >= queue1_index );
            
            
            mod_index = queue1[mod_index];      // queue1 den submatrix in indexini al            
            
            for ( i=0; i<5; i++){
                for ( j=0; j<5; j++ ){
                    // her elemanın 1. elemana göre modunu al mod arraya yaz 
                    mod_matrix_array[mod_index].sub_matrix[i][j] = sub_matrix_array[mod_index].sub_matrix[i][j] % sub_matrix_array[mod_index].sub_matrix[0][0];
                }
            }
            
            
            
            pthread_mutex_lock(&queue2_mutex);
            // mod oluşturma bitince queue 2 ye bitirilen matrix in indexini yaz
            queue2[queue2_index] = mod_index;
            queue2_index++;
            pthread_mutex_unlock(&queue2_mutex);
            
            pthread_mutex_lock(&print_mutex);
            printf("Mod_%ld generated following matrix:\n", tid);
            printf("[");
            for ( i = 0; i < 5; i++){
                for (j = 0; j < 5; j++){
                    printf("%d",mod_matrix_array[mod_index].sub_matrix[i][j]);
                    
                    if ( i != 4 || j != 4 ){
                        printf(",");
                    }
                }
                if ( i != 4)
                    printf("\n ");
            }
            printf("]\n");
            printf("This matrix is [%d,%d] submatrix\n\n", mod_index/(global_matrix_size/5), mod_index  %(global_matrix_size/5));
            
            pthread_mutex_unlock(&print_mutex);
        }
        /* özetle 
         * mod thread in hangi submatrix okuyacağı belirlenir ( in critical section )
         * queue 2 den gerçek index alınır
         * oluşturulur 
         * bitirdiği zamanda başka queue ye ben bunu bitiridim diye yazar ( in critical section )
         */
    }
    
    pthread_exit(NULL);
}

// add thread works
void *add_function(void *threadid){
    int add_index;  // hangi mod_sub_matrix in okunacağını tutar 
    
    int local_sum; // local sum
    int i,j;
    long tid;
    tid = (long)threadid;
    
    
    while ( add_counter < submatrix_number ){
        
        // okunacak submatrix in belirlendiği kritik section
        pthread_mutex_lock(&add_mutex);
        add_index = add_counter;
        add_counter++;
        pthread_mutex_unlock(&add_mutex);
        
        
        sleep(0.4);
        local_sum = 0; // clear local sum every time
        if ( add_index < submatrix_number ){
            
            
            
            // log waits mod threads  okuyacağı submatrix oluşturulmamışsa 
            while ( add_index >= queue2_index );
            
            
            add_index = queue2[add_index]; // queue2 den submatrix in gerçek değerini al
            
            for ( i = 0; i<5;i++){
                for ( j=0; j<5; j++){                    
                    local_sum = local_sum + mod_matrix_array[add_index].sub_matrix[i][j];  // değeri oku local sum ile topla
                }
                
            }
            
            
            // print 
            pthread_mutex_lock(&print_mutex);
            printf("Add_%ld has local sum: %d by [%d,%d] submatrix, global sum before/after update: %d/", tid,local_sum,add_index/(global_matrix_size/5), add_index  %(global_matrix_size/5), sum);  
             sum = sum + local_sum;
            printf("%d\n", sum );
            pthread_mutex_unlock(&print_mutex);
        }
    }
    pthread_exit(NULL);
}

// convert string to integer
void stringToInt(char c[],int *number){
    int i = 0;
    int coeff = 1;
    int length = 0;
    
    while ( c[i] != '\0' ){
        length++;
        i++;
    }
    length--;
    
    for ( length ; length >= 0; length--  ){
        *number = *number + ( coeff * ( c[length] - '0' ));
        coeff = coeff * 10;
    }
    
}

int main(int argc, char* argv[]){
    
    int number_of_generate_thread = 0;
    int number_of_log_thread = 0;
    int number_of_mod_thread = 0;
    int number_of_add_thread = 0;
    
    // convert line arguments to integer
    stringToInt(argv[2], &global_matrix_size);
    stringToInt(argv[4], &number_of_generate_thread);
    stringToInt(argv[5], &number_of_log_thread);
    stringToInt(argv[6], &number_of_mod_thread);
    stringToInt(argv[7], &number_of_add_thread);
    
    // calculate submatrix number
    submatrix_number = global_matrix_size/5*global_matrix_size/5;    
    
    
    // create therads
    pthread_t generate_thread[number_of_generate_thread];
    pthread_t log_thread[number_of_log_thread];
    pthread_t mod_thread[number_of_mod_thread];
    pthread_t add_thread[number_of_add_thread];
    long t;
    
    // initialize all threads
    for ( t=0; t < number_of_generate_thread; t++)
        pthread_create(&generate_thread[t], NULL, generator_function, (void *)t);
    
    for ( t=0; t < number_of_log_thread; t++)
        pthread_create(&log_thread[t], NULL, log_function, NULL);
    
    for ( t=0; t< number_of_mod_thread; t++)
        pthread_create(&mod_thread[t], NULL, mod_function, (void *)t);
    
    for ( t=0; t < number_of_add_thread; t++)
        pthread_create(&add_thread[t], NULL, add_function, (void *)t);
    
    // wait for all threads
    for ( t=0; t< number_of_generate_thread; t++)
        pthread_join(generate_thread[t],NULL);

    for ( t=0; t< number_of_log_thread; t++)
        pthread_join(log_thread[t],NULL);
    
    for ( t=0; t< number_of_mod_thread; t++)
        pthread_join(mod_thread[t],NULL);
    
    for ( t=0; t< number_of_add_thread; t++)
        pthread_join(add_thread[t],NULL);
     
    
    // redirect to file
    int outputFile = open("outputfile.txt", O_WRONLY | O_TRUNC | O_CREAT, c_mode);
    dup2(outputFile,STDOUT_FILENO);
    // print global matrix and global sum
    int k,j;
    
    printf("The matrix is\n");
    printf("[");
    for( k=0; k< global_matrix_size; k++){
        for ( j=0; j<global_matrix_size; j++){
            if ( global_matrix[k][j] < 10 )
                printf("0");
            printf("%d", global_matrix[k][j]);
            if ( k != global_matrix_size -1  || j != global_matrix_size - 1 ){
                printf(",");
            }
        }
        if ( k != global_matrix_size - 1)
            printf("\n ");
    }
    printf("]\n");
    
    printf("\nThe global sum is : %d.\n", sum);
    
}









