#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <stdlib.h>
#include <dirent.h>
#include <string.h>
#include <sys/wait.h>
#include <fcntl.h>


#define MAX_LINE 80 /* 80 chars per line, per command, should be enough. */
#define c_mode (S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH)

struct node{
    char name[20];
    char al_args[MAX_LINE/2][20];
};


struct node alias_array[50];        // node array for keep alias
int element_in_alias = 0;           // number of element in alias array
int number_of_processes=0;          // number of running process

int saved_out;          // saved standard output,input and error
int saved_in;
int saved_err;


int ioDirectionError = 0;




// insert new alias to alias array
void insert(char *args[]){
    int k = 1;
    int i = 0;

    while ( args[k] != NULL ){
        k++;
    }
    k = k-2;
    
    for ( i; i< element_in_alias; i++){     // check for the alias to be wanted to insert
        if ( strcmp(alias_array[i].name, args[k+1]) == 0 ){
            fprintf(stderr,"There is another alias named same alias name\n");
            return;
        }
    }
    strcpy(alias_array[element_in_alias].name,args[k+1]);

    for ( k; k>0; k--)
        strcpy(alias_array[element_in_alias].al_args[k-1],args[k]);

    element_in_alias++;
}

// delete alias 
void delete(char *args[]){
    int k,a;
    for (k=0; k < element_in_alias; k++ ){
        if ( strcmp(args[1],alias_array[k].name) == 0 )
            break;
    }
    if ( k == element_in_alias ){   // if alias does not exist show error message
        fprintf(stderr,"The ALIAS does not exist\n");
        return;
    }
    else{
        for (k; k < element_in_alias-1; k++){
            strcpy(alias_array[k].name,alias_array[k+1].name);
            for ( a=0; a < MAX_LINE/2-1; a++)
                strcpy(alias_array[k].al_args[a], alias_array[k+1].al_args[a]);
        }
        strcpy(alias_array[k].name,"\0");
        for ( a=0; a < MAX_LINE/2-1; a++)
            strcpy(alias_array[k].al_args[a],"\0");
    }

    element_in_alias--;
}
// print alias array
void print(){
    int k;
    int a;
    if ( element_in_alias == 0 ){  // if alias array is empty show error message
        fprintf(stderr,"Alias list does not exist\n");
        return;
    }
    else{
        for ( k=0; k < element_in_alias; k++ ){
            printf("%s ",alias_array[k].name);
            printf("\"");
            for ( a = 0; a < MAX_LINE/2; a++){
                if ( alias_array[k].al_args[a] != NULL )
                    printf("%s",alias_array[k].al_args[a]);

                if ( strcmp(alias_array[k].al_args[a+1],"\0") == 0 )
                    break;
                printf(" ");
            }
            printf("\"");
            printf("\n");
        }
        printf("\n");
    }
}

// check whole alias array for a given command
void checkAlias(char *args[]){
    int k,a;
    for (k=0; k < element_in_alias; k++ ){
        if ( strcmp(args[0],alias_array[k].name) == 0 ){
            for (a=0; a< MAX_LINE/2; a++){
                if(strcmp(alias_array[k].al_args[a],"\0") == 0 ){
                    break;
                }

                args[a] = alias_array[k].al_args[a];

            }
            
            break;
        }

    }
}

// clear quoates from command for alias 
void clearQuoates(char *args[]){
    int a,b,c;
    char ch = '\0';
    char quotes = '"';
    a=0;
    while ( args[a] != NULL ){
        b= 0;
        while ( args[a][b] != ch){
            if ( args[a][b] == quotes ){
                c = b;

                while ( args[a][c] != ch ){


                    args[a][c]=args[a][c+1];
                    c++;
                }
            }
            b++;
        }
        a++;
    }

}

// clear args
void clearArgs(char *args[]){
    int k;
    for (k=0;k<MAX_LINE;k++)
        args[k] = NULL;
}

// if cd command is given do below
void changeDirectory(char *args[]){
    
    char *directory_path = getenv("PWD");  // get current directory
    
   
    
    int i;

    if ( args[1] == NULL ){     //   cd  command change directory to home 
        chdir(getenv("HOME"));
        printf("Directory has been changed to home\n");
    }
    else if ( strcmp(args[1], ".." ) == 0 ){ // cd .. upper directory
        i=0;
        // go end of the path and delete backward till division mark
        while ( directory_path[i] != '\0' ){
            i++;
        }

        while ( directory_path[i] != '/' ){
            directory_path[i] = '\0';
            i--;
        }
        directory_path[i] = '\0';

        chdir(directory_path);
        printf("Directory has been changed to : %s\n",directory_path);
    }
    else if ( strcmp(args[1], "." ) == 0 ){// cd . current directory
        printf("Current directory is : %s\n",directory_path);
    }
    else{
        i = 0;
        while ( directory_path[i] != '\0' ){
            i++;
        }
        strcat(directory_path, "/");
        strcat(directory_path, args[1]);
        if ( chdir(directory_path) == -1 ) { // if directory does not exist show error message and update pwd old directory
            fprintf(stderr,"Error: No directory\n");
            
                
            while ( directory_path[i] != '\0' ){
                directory_path[i] = '\0';
                i++;
            }
                        
            chdir(directory_path);
            return;
        }
        
        printf("Directory has been changed to : %s\n",directory_path);
        
    }
}

void checkIOdirection(char *args[]){
    int k = 0;
    int inputFile = 0;
    int outputFile = 0;
    int errorFile = 0;
    /*Available Values for oflag
     * O_RDONLY = Open the file so that it is read only.
     * O_WRONLY = Open the file so that it is write only.
     * O_APPEND = Append new information to the end of the file.
     * O_TRUNC = Initially clear all data from the file.
     * O_CREAT = If the file does not exist, create it. If the O_CREAT option is used, then you must include the third parameter.
     *
     * Available Values for mode
     * S_IRUSR = Set read rights for the owner to true.
     * S_IWUSR = Set write rights for the owner to true.
     * S_IRGRP = Set read rights for the group to true.
     * S_IROTH = Set read rights for other users to true.
     *
     *  >> defined as c_mode <<
     * */

    while ( args[k] != NULL ){
        /* open the file and clear contents
         * if file does not exist create it by defined mode */ 
        if ( strcmp(args[k],">") == 0 ){
            k++;
            outputFile = open(args[k], O_WRONLY | O_TRUNC | O_CREAT, c_mode);
            if ( outputFile == -1 ){
                ioDirectionError = 1;
                perror("Failed to open file");      // if file could not open, show error message
                return;
            }
            if ( dup2(outputFile,STDOUT_FILENO) == -1 ){
                ioDirectionError = 1;
                perror("Failed to redirect standart output");   // if redirection has error, show error message
                return;
            }
            if ( close(outputFile) == -1 ){
                ioDirectionError = 1;
                perror("Failed to close file");     // if file could not close show error message
                return;
            }

            args[k] = NULL;     // clear " > " from args
            args[k-1] = NULL;   // clear file name from args
        }
        /* open the file and don't clear contents 
         * if file does not exist create it by defined mode */
        else if ( strcmp(args[k],">>") == 0 ){
            k++;
            outputFile = open(args[k], O_WRONLY | O_APPEND | O_CREAT, c_mode);

            if ( outputFile == -1 ){
                ioDirectionError = 1;
                perror("Failed to open file");      // if file could not open, show error message
                return;
            }
            if ( dup2(outputFile,STDOUT_FILENO) == -1 ){
                ioDirectionError = 1;
                perror("Failed to redirect standart output");       // if redirection has error, show error message
                return;
            }
            if ( close(outputFile) == -1 ){
                ioDirectionError = 1;
                perror("Failed to close file");     // if file could not close show error message
                return;
            }

            args[k] = NULL;     // clear " >> " from args
            args[k-1] = NULL;   // clear file name from args

        }
        /* open the file for read 
         * if file does not exist, error message will be shown */
        else if ( strcmp(args[k],"<") == 0 ){
            k++;
            inputFile = open(args[k], O_RDONLY);
            if ( inputFile == -1 ){
                ioDirectionError = 1;
                perror("Failed to open file");  // if file could not open, show error message
                return;
            }
            if ( dup2(inputFile,STDIN_FILENO) == -1 ){
                ioDirectionError = 1;
                perror("Failed to redirect standart input");    // if redirection has error, show error message
                return;
            }
            if ( close(inputFile) == -1 ){
                ioDirectionError = 1;
                perror("Failed to close file");     // if file could not close show error message
                return;
            }
            
            args[k] = NULL;     // clear " < " from args
            args[k-1] = NULL;   // clear file name from args

        }
        else if ( strcmp(args[k],"2>") == 0 ){
            k++;
            errorFile = open(args[k], O_WRONLY | O_TRUNC | O_CREAT, c_mode);

            if ( errorFile == -1 ){
                ioDirectionError = 1;
                perror("Failed to open file");
                return;
            }
            if ( dup2(errorFile,STDERR_FILENO) == -1 ){
                ioDirectionError = 1;
                perror("Failed to redirect standart error");
                return;
            }
            if ( close(errorFile) == -1 ){
                ioDirectionError = 1;
                perror("Failed to close file");
                return;
            }

            args[k] = NULL;
            args[k-1] = NULL;

        }
        k++;
    }
}


// clear zombie processes
void clearZombieProcesses(){
    pid_t childpid;
    childpid = waitpid(-1, NULL, WNOHANG);
    while (childpid != 0)
    {
        if ((childpid == -1) && (errno != EINTR))
            break;
        else{
            childpid = waitpid(-1, NULL, WNOHANG);
            number_of_processes--;
        }
    }
}

/* The setup function below will not return any value, but it will just: read
in the next command line; separate it into distinct arguments (using blanks as
delimiters), and set the args array entries to point to the beginning of what
will become null-terminated, C-style strings. */



void setup(char inputBuffer[], char *args[],int *background)
{
    int length, /* # of characters in the command line */
            i,      /* loop index for accessing inputBuffer array */
            start,  /* index where beginning of next command parameter is */
            ct;     /* index of where to place the next parameter into args[] */

    ct = 0;

    /* read what the user enters on the command line */
    length = read(STDIN_FILENO,inputBuffer,MAX_LINE);

    /* 0 is the system predefined file descriptor for stdin (standard input),
       which is the user's screen in this case. inputBuffer by itself is the
       same as &inputBuffer[0], i.e. the starting address of where to store
       the command that is read, and length holds the number of characters
       read in. inputBuffer is not a null terminated C-string. */

    start = -1;
    if (length == 0)
        exit(0);            /* ^d was entered, end of user command stream */

/* the signal interrupted the read system call */
/* if the process is in the read() system call, read returns -1
  However, if this occurs, errno is set to EINTR. We can check this  value
  and disregard the -1 value */
    if ( (length < 0) && (errno != EINTR) ) {
        perror("error reading the command");
        exit(-1);           /* terminate with error code of -1 */
    }

    
    for (i=0;i<length;i++){ /* examine every character in the inputBuffer */

        switch (inputBuffer[i]){
            case ' ':
            case '\t' :               /* argument separators */
                if(start != -1){
                    args[ct] = &inputBuffer[start];    /* set up pointer */
                    ct++;
                }
                inputBuffer[i] = '\0'; /* add a null char; make a C string */
                start = -1;
                break;

            case '\n':                 /* should be the final char examined */
                if (start != -1){
                    args[ct] = &inputBuffer[start];
                    ct++;
                }
                inputBuffer[i] = '\0';
                args[ct] = NULL; /* no more arguments to this command */
                break;

            default :             /* some other character */
                if (start == -1)
                    start = i;
                if (inputBuffer[i] == '&'){
                    *background  = 1;
                    inputBuffer[i-1] = '\0';
                }
        } /* end of switch */
    }    /* end of for */
    args[ct] = NULL; /* just in case the input line was > 80 */

    




} /* end of setup routine */

int main(void)
{
    char inputBuffer[MAX_LINE]; /*buffer to hold command entered */
    int background; /* equals 1 if a command is followed by '&' */
    char *args[MAX_LINE/2 + 1]; /*command line arguments */
    char pipeline_arg[40];

    long mainProcessID = (long)getpid();  // save main process id

    pid_t child_pid;

    char *path = getenv("PATH"); // get path from environment 
    
    char *pipeline_path;   // for pipe line 2nd command path
    pipeline_path = (char*) calloc(100, sizeof(char));
    char *filePath;     // for command path
    filePath = (char*) calloc(100, sizeof(char));

    int j,break_loop;  
    int i = 0;      // used in a couple  of loop
    int check_pipeline = 0;  // pipeline boolean
    
    int pipe_line[2]; // get construct pipeline
    DIR * d;
    struct dirent * dir;

    saved_out = dup(STDOUT_FILENO);     // save standard output
    saved_in = dup(STDIN_FILENO);       // save standard input 
    saved_err = dup(STDERR_FILENO);     // save standard error
    
    signal(SIGTSTP, SIG_IGN);       // ignore ctrl^z signal
    
    
    while (1){
        
        /* fflush for clear buffer and dup2 redirect standar output,input and error */
        fflush(stdout);
        dup2(saved_out,STDOUT_FILENO);
        
        fflush(stdin);
        dup2(saved_in,STDIN_FILENO);
        
        fflush(stderr);
        dup2(saved_err,STDERR_FILENO);
        
        
        i = 0;
        while (filePath[i] != '\0'){        // clear filepath
            filePath[i] = 0;
            i++;
        }
        
        i = 0;
        
        while (pipeline_path[i] != '\0' ){      // clear pipeline path
            pipeline_path[i] = 0;
            i++;
        }
        
    
        strcpy(pipeline_arg,"\0");      // clear pipeline second argument
        
        // clear used variables
        background=0;
        j = 0;
        i = 0;
        break_loop = 0;
        check_pipeline = 0;
        ioDirectionError = 0;
        d = NULL;
        
        printf("myshell: ");
        fflush( stdout );
        clearArgs(args); // clear arguments
        
        setup(inputBuffer, args, &background);
        
        checkIOdirection(args);  // check io direction
        
        
        // check pipe 
        while ( args[i] != NULL ){
            if ( strcmp(args[i],"|" ) == 0 ){
                check_pipeline = 1;
                args[i] = NULL;
                i++;
                strcpy(pipeline_arg,args[i]);
                while ( args[i] != NULL ){
                    args[i] = NULL;
                    j++;
                    i++;
                }
                break;
            }
            i++;
        }
        
        i = 0;
        j = 0;
        

        // if alias array is not empty check alias array
        if ( element_in_alias != 0)
            checkAlias(args);
        



        int returnStatus = 0;
        clearZombieProcesses();
        if ( args[0] == NULL ){// if command is empty 
            fprintf(stderr,"Empty command");
        }
        else if (strcmp(args[0],"fg") == 0){ // move all background processes to foreground
            
            if ( number_of_processes == 0 )// if there is no background processes show error message
                fprintf(stderr,"There is no background process\n");
            
            clearZombieProcesses();// clear zombie processes
            for ( int i = 0; i<number_of_processes; i++ ){
                waitpid(-1, &returnStatus, 0); // wait for the moved new foreground processes
                // Parent process waits here for child to terminate.

            }

            number_of_processes = 0; // declare number of processes to 0
        }
        /* if there is no background processes exit shell,otherwise show error message */
        else if ( strcmp(args[0],"exit") == 0 ){

            if ( number_of_processes > 0 )
                fprintf(stdout, "There are background processes still running\n");
            else
                exit(0);
        }
        else if ( strcmp(args[0],"alias") == 0 && strcmp(args[1],"-l") == 0){  // if alias list wanted to print (alias -l), print
            print();
        }
        else if ( strcmp(args[0],"alias") == 0 ){  // if alias is given insert alias to the array
            clearQuoates(args);
            insert(args);
        }
        else if ( strcmp(args[0],"unalias") == 0 ){  // if unalias is given delete alias from array
            delete(args);
        }
        else if ( strcmp(args[0],"clr") == 0 ) { // clear the screen
            system("clear");
        }
        else if ( strcmp(args[0], "cd") == 0){ // change directory
            changeDirectory(args);
        }
        else if ( check_pipeline == 1 && ioDirectionError == 0 ){ // if there is pipe and no ioDirectionError  
            while (j<98){

                // scan every subdirectories to find given command
                while (path[j] != ':'){
                    filePath[i] = path[j];
                    j++;
                    i++;
                }

                d = opendir(filePath);


                // for the directory entries
                while ((dir = readdir(d)) != NULL) // if we were able to read something from the directory
                {

                    if(strcmp(dir->d_name, args[0]) == 0){ // if the command has found 
                        break_loop = 1; // for outer while loop
                        break;// stop inner while loop
                    }

                }

                closedir(d); // finally close the directory

                if ( break_loop == 1 )  // if path has found stop outer loop
                    break;




                // clear the file path
                while (i != 0){
                    filePath[i] = 0;
                    i--;
                }


                j++;
            }


            number_of_processes++; // increase number of processes by 1

            filePath = strcat(filePath, "/");
            filePath = strcat(filePath, args[0]); // concatenate "/" and file name with file path
            
            
            // clear variables
            i = 0;
            j = 0;
            d = NULL;
            break_loop = 0;
            
            // do same for second command
            // pipeline_path for second command
            while (j<98){


                while (path[j] != ':'){
                    pipeline_path[i] = path[j];
                    j++;
                    i++;
                }

                d = opendir(pipeline_path);


                // for the directory entries
                while ((dir = readdir(d)) != NULL) // if we were able to read somehting from the directory
                {

                    if(strcmp(dir->d_name, pipeline_arg) == 0){
                        break_loop = 1;
                        break;
                    }

                }

                closedir(d); // finally close the directory

                if ( break_loop == 1 )  // if path has found stop loop
                    break;





                while (i != 0){
                    pipeline_path[i] = 0;
                    i--;
                }


                j++;
            }
        

            

            pipeline_path = strcat(pipeline_path, "/");
            pipeline_path = strcat(pipeline_path, pipeline_arg);
            
            
            if ( mainProcessID == (long)getpid() )  // main process create a child process 
                child_pid = fork();
            
            
            if ( child_pid == -1 )// if child process could not created show error message
                fprintf(stderr,"Error, child process could not created");
            else if ( child_pid == 0 ){
                
                pipe(pipe_line); // construct pipeline
                child_pid = fork(); // child process also create his child process
                
                if ( child_pid == -1 )
                    fprintf(stderr,"Error, child process could not created");
                else if ( child_pid == 0 ){// child's child process does this
                    dup2(pipe_line[1], STDOUT_FILENO);// redirect output to the pipelie 
                    close(pipe_line[0]);// close pipeline
                    close(pipe_line[1]);// close pipe line
                    
                    // executes output to the pipeline
                    if ( execl(filePath, args[0],args[1],args[2],args[3], NULL) == -1 ){  
                        perror("Error");
                        exit(getpid());
                    }
                }
                else{ // child process does this
                    wait(NULL); // waits for his child to finish process
                    dup2(pipe_line[0], STDIN_FILENO); // get input from pipeline
                    close(pipe_line[0]); // close pipeline
                    close(pipe_line[1]); // close pipeline
                    if ( execl(pipeline_path, pipeline_arg, NULL) == -1 ){
                        perror("Error");
                        exit(getpid());
                    }
                }
            }
            else{// main parent process waits for finish child and child's child process
                if ( background == 0 && mainProcessID == (long)getpid()){
                    number_of_processes = number_of_processes - 1;  // decrease number of process 
                    wait(NULL);
                }
            }
            
        }
        else if ( ioDirectionError == 0 ){  // if there is no pipe and ioDirectionError error
            
            // get path of command
            while (j<98){


                while (path[j] != ':'){
                    filePath[i] = path[j];
                    j++;
                    i++;
                }

                d = opendir(filePath);


                // for the directory entries
                while ((dir = readdir(d)) != NULL) // if we were able to read something from the directory
                {

                    if(strcmp(dir->d_name, args[0]) == 0){
                        break_loop = 1;
                        break;
                    }

                }

                closedir(d); // finally close the directory

                if ( break_loop == 1 )  // if path has found stop loop
                    break;





                while (i != 0){
                    filePath[i] = 0;
                    i--;
                }


                j++;
            }


            number_of_processes++;

            filePath = strcat(filePath, "/");
            filePath = strcat(filePath, args[0]);
            
            
            if ( mainProcessID == (long)getpid() )  // main process create a child
                child_pid = fork();

            
            if ( child_pid == -1 ){
                fprintf(stderr,"Error, child process could not created");
            }
            else if ( child_pid == 0 ){
                
                if ( background == 0 )// if command is not background active ctrl^z signal
                    signal(SIGTSTP, NULL);
                
                if ( execl(filePath, args[0],args[1],args[2],args[3], NULL) == -1 ){
                    perror("Error");
                    exit(getpid());
                }


            }
            else if ( child_pid > 0 ) {// parrent waits for finish child process if it is not background process 
                
                if ( background == 0 && mainProcessID == (long)getpid()){
                    number_of_processes = number_of_processes - 1;// decrease number of process
                    wait(NULL);
                }
                
            
            }
            
        }






        /** the steps are:
        (1) fork a child process using fork()
        (2) the child process will invoke execv()
        (3) if background == 0, the parent will wait,
        otherwise it will invoke the setup() function again. */
    }
}
