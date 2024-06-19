#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <time.h>
#include <ctype.h>

#define FIFO1 "fifo1"   /* Define constant for FIFO1 name*/
#define FIFO2 "fifo2"   /* Define constant for FIFO2 name*/

volatile sig_atomic_t child_count = 0;  /*Declare a volatile variable to track the number of child processes that have finished*/

void signal_handler(int signal) {
    int status;
    pid_t pid;
    while ((pid = waitpid(-1, &status, WNOHANG)) > 0) {     /*Loop to handle multiple child processes exiting at once*/
        dprintf(STDERR_FILENO, "Child with PID %ld exited with status %d.\n", (long)pid, WEXITSTATUS(status));  /* Print the PID and exit status of the child process*/
        child_count++;          /*Increment the count of completed child processes*/
    }
}
void setup_signal_handling() {
    struct sigaction sa;
    sa.sa_handler = signal_handler;     /*Set the handler function for SIGCHLD*/
    sa.sa_flags = SA_RESTART | SA_NOCLDSTOP;                    /*Ensure that interrupted system calls are restarted and no signal is sent when children stop*/
    sigemptyset(&sa.sa_mask);           /*Initialize the signal mask and exclude all signals during the handler*/
    sigaction(SIGCHLD, &sa, NULL);      /*Register the signal action for SIGCHLD*/
}

int validate_input(const char *str) {   /*Check if the input string is a valid integer*/
    if (*str == '0') {          /*Return 0 if input starts with "0"*/
        return 0; 
    }

    while (*str) {              
        if (!isdigit(*str)) {
            return 0;           /* Return 0 if the input contains non-digit characters*/
        }
        str++;
    }
    return 1;           /*Return 1 if all characters are digits*/
}

int main(int argc, char *argv[]) {  
    if (argc != 2 || !validate_input(argv[1])) {            /*Validate command line arguments*/
        fprintf(stderr, "%s [enter an integer value for the size of the random number to be generated]\n", argv[0]);    
        exit(EXIT_FAILURE);
    }

    int array_size = atoi(argv[1]);         /*Convert the input string to an integer for array size*/
    int fd1, fd2;                           /*File descriptors for FIFOs*/
    pid_t pid1, pid2;                       /*Process IDs for child processes*/
    int *random_numbers = malloc(array_size * sizeof(int));        /*Dynamically allocate memory for the numbers array*/
    if (random_numbers == NULL) {
        perror("Memory allocation failed");                 /*Print error and exit if memory allocation fails*/
        exit(EXIT_FAILURE);
    }

    if (mkfifo(FIFO1, 0666) == -1 || mkfifo(FIFO2, 0666) == -1) {   /*Create two FIFOs with read and write permissions*/
        perror("Failed to create FIFOs");                           /*Print error and exit if FIFO creation fails*/
        exit(EXIT_FAILURE);
    }

    srand(time(NULL));
    int i, len;
    char buffer[100];
    len = snprintf(buffer, sizeof(buffer), "Randomly generated numbers: ");     /*Prepare initial output message*/
    write(STDOUT_FILENO, buffer, len);                                          /*Write the initial message to standard output*/
    for (i = 0; i < array_size; i++) {
        random_numbers[i] = rand() % 10;                                   /*Generate random numbers between 0 and 9*/
        len = snprintf(buffer, sizeof(buffer), "%d ", random_numbers[i]);  /*Prepare number output message*/    
        write(STDOUT_FILENO, buffer, len);                          /*Write each number to standard output*/
    }
    write(STDOUT_FILENO, "\n", 1);

    setup_signal_handling();                            /*Setup signal handling for child process termination*/
    
    pid1 = fork();
    if (pid1 == -1) {
    perror("Failed to fork first child process");
    exit(EXIT_FAILURE);
    } else if(pid1 == 0) {                                     /*Fork to create a first child process*/
        sleep(10);                                  /*All child processes sleep for 10 seconds, execute their tasks, and then exit*/
        fd1 = open(FIFO1, O_RDONLY);                /*Open FIFO1 for reading in the child process*/
        if (fd1 == -1) {
            perror("Failed to open FIFO1 for reading");     /*Print error and exit if opening fails*/
            exit(EXIT_FAILURE);
        }
        int sum = 0, temp;
        while (read(fd1, &temp, sizeof(temp)) > 0) {    /*Read integers from FIFO1 and calculate their sum*/
            sum += temp;
        }
        close(fd1);                         /*Close FIFO1 after reading all data*/

        fd2 = open(FIFO2, O_WRONLY);            /*Open FIFO2 for writing in the child process*/
        if (fd2 == -1) {
            perror("Failed to open FIFO2 for writing");     /*Print error and exit if opening fails*/
            exit(EXIT_FAILURE);
        }
        if (write(fd2, &sum, sizeof(sum)) == -1) {            /*Write the sum result to FIFO2*/
            perror("Failed to write sum to FIFO2");         /*Print error and exit if writing fails*/
            close(fd2);
            exit(EXIT_FAILURE);
        }
        close(fd2);                                 /*Close FIFO2 after writing*/
        exit(EXIT_SUCCESS);                         /*Exit successfully from the first child process*/
    }

    sleep(2);                           /*Sleep to stagger the operation of child processes*/
    write(STDOUT_FILENO, "proceeding\n", 11);
    sleep(2);
    write(STDOUT_FILENO, "proceeding\n", 11);  
    sleep(2);
    write(STDOUT_FILENO, "proceeding\n", 11); 

    pid2 = fork();
    if (pid2 == -1) {
    perror("Failed to fork second child process");
    exit(EXIT_FAILURE);
    } else if (pid2 == 0) {        /*Fork to create a second child process*/
        sleep(10);                      /*All child processes sleep for 10 seconds, execute their tasks, and then exit*/
        fd2 = open(FIFO2, O_RDONLY);        /*Open FIFO2 for reading in the second child process.*/
        if (fd2 == -1) {
            perror("Failed to open FIFO2 for reading");     /*Print error and exit if opening fails.*/
            exit(EXIT_FAILURE);
        }
        int received_sum;
        char command[32];
        /*Read the sum and command from FIFO2*/
        if (read(fd2, &received_sum, sizeof(received_sum)) > 0 && read(fd2, command, sizeof(command)) > 0) {                        
            /*Print the received command and result to standard output*/
            len = snprintf(buffer, sizeof(buffer), "Received sum: %d and command: %s in child process\n", received_sum, command);
            write(STDOUT_FILENO, buffer, len);
            if (strcmp(command, "multiply") == 0) {
                int multiplication_result = 1, temp;
                while (read(fd2, &temp, sizeof(temp)) > 0) {    /*Read random numbers from FIFO2 and calculate their multiplication*/
                    multiplication_result *= temp;              /*Multiply the numbers*/
                }
                len = snprintf(buffer, sizeof(buffer), "Multiplication result: %d\n", multiplication_result);
                write(STDOUT_FILENO, buffer, len);
                int total_result = received_sum + multiplication_result;  /* Calculate total result of sum and multiplication*/
                len = snprintf(buffer, sizeof(buffer), "Total result (sum + multiplication): %d\n", total_result);
                write(STDOUT_FILENO, buffer, len);
            }
        } else {
            perror("Failed to read data from FIFO2");       /*Print error and exit if reading fails*/
            exit(EXIT_FAILURE);
        }
        close(fd2);                             /*Close FIFO2 after reading all data*/
        exit(EXIT_SUCCESS);                     /*Exit successfully from the second child process*/
    }

    fd1 = open(FIFO1, O_WRONLY);                /*Open FIFO1 for writing in the parent process*/
    if (fd1 == -1) {
        perror("Failed to open FIFO1 for writing");         /*Print error and exit if opening fails*/
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < array_size; i++) {                      /*Write all numbers to FIFO1*/
        if (write(fd1, &random_numbers[i], sizeof(random_numbers[i])) == -1) {    
            perror("Error writing numbers to FIFO1");           /*Print error and exit if writing fails*/
            close(fd1);
            exit(EXIT_FAILURE);
        }
    }
    close(fd1);                 /* Close FIFO1 after writing all numbers*/

    fd2 = open(FIFO2, O_WRONLY);                    /*Open FIFO2 for writing in the parent process*/
    if (fd2 == -1) {
        perror("Failed to open FIFO2 for writing");     /*Print error and exit if opening fails*/
        exit(EXIT_FAILURE);
    }
    sleep(2);                           /* Sleep to ensure first child has time to read from FIFO1*/
    write(STDOUT_FILENO, "proceeding\n", 11);
    sleep(2);
    write(STDOUT_FILENO, "proceeding\n", 11);  
    sleep(2);
    write(STDOUT_FILENO, "proceeding\n", 11); 
    
    const char *command = "multiply";
    if (write(fd2, command, strlen(command) + 1) == -1) {    /*Write command to FIFO2*/
        perror("Error writing command to FIFO2");                   /*Print error and exit if writing fails.*/
        close(fd2);
        exit(EXIT_FAILURE);
    }
    sleep(2);                           /*Sleep to ensure command is read before writing numbers*/
    write(STDOUT_FILENO, "proceeding\n", 11);
    sleep(2);
    write(STDOUT_FILENO, "proceeding\n", 11);  
    sleep(2);
  
    for (i = 0; i < array_size; i++) {                              /*Write all numbers to FIFO2*/
        if (write(fd2, &random_numbers[i], sizeof(random_numbers[i])) == -1) {
            perror("Error writing numbers to FIFO2");                /*Print error and exit if writing fails.*/
            close(fd2);
            exit(EXIT_FAILURE);
        }
    }
    close(fd2);                         /* Close FIFO2 after writing all data*/

    while (child_count < 2) {
    	write(STDOUT_FILENO, "proceeding\n", 11);          /*Write a message to indicate that the parent is waiting for child processes*/
        sleep(2);                                          /*Sleep to wait for child processes to exit*/
    }

    unlink(FIFO1);                  /*Remove FIFO1 file*/
    unlink(FIFO2);                  /*Remove FIFO2 file*/
    free(random_numbers);                  /*Free dynamically allocated memory for numbers*/
    const char *message = "All child processes have completed.\n";      /*Prepare final message*/
    write(STDOUT_FILENO, message, strlen(message));                 /*Write final message to indicate completion*/
    return 0;
}

