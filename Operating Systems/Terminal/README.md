Creating personel terminal using exec function

  - The terminal supports alias names.
      - alias/unalias – set/remove an alias for a command.
          myshell> alias “ls -l” list
          myshell> alias "ps -a” proc
          myshell> alias -l
                    list "ls -l"
                    proc "ps -a"
          myshell> proc
                    PID TTY TIME CMD
                    6052 pts/0 00:00:00 ps
          myshell> unalias list
          myshell> alias -l
                    proc "ps -a"

  - ^Z - Stop the currently running foreground process, as well as any descendants of that process (e.g., any child processes that it forked). If there is no foreground process, then the signal should have no effect.
  
  - clr - clear the screen.
  
  - fg - Move all the background processes to foreground. Note that for this, you have to keep track of all the background processes.
  
  - exit - Terminate your shell process. If the user chooses to exit while there are background processes, notify the user that there are background processes still running and do not terminate the shell process unless the user terminates all background processes.


  The shell must support I/O-redirection on either or both stdin and/or stdout and it can include arguments as well. For example, if you have the following commands at the command line:
  
  - myshell: myprog [args] > file.out
    - Writes the standard output of myprog to the file file.out. file.out is created if it does not exist and truncated if it does.
  
  - myshell: myprog [args] >> file.out
    - Appends the standard output of myprog to the file file.out. file.out is created if it does not exist and appended to if it does.
  
  - myshell: myprog [args] < file.in
    - Uses the contents of the file file.in as the standard input to program myprog.
    
  - myshell: myprog [args] 2> file.out
    - Writes the standard error of myprog to the file file.out.
  
  - myshell: myprog [args] < file.in > file.out
    -Executes the command myprog which will read input from file.in and stdout of the command is directed to the file file.out
    
  - And the terminal supports pipe operation.
Implemented with C programming language
