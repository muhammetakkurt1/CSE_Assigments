#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/wait.h>
#include <time.h>
#include <errno.h>
/*Function to display commands */
void displayCommands() {
    printf("Available commands:\n");
    printf("gtuStudentGrades (All instructions of the program are listed)\n");
    printf("gtuStudentGrades file.txt (Create a file)\n");
    printf("addStudentGrade \"Name Surname\" \"Grade\" file.txt\n");
    printf("searchStudent \"Name Surname\" file.txt\n");
    printf("sortAll Number file.txt  (Number: 1 = (A-Z) 2 = (Z-A) 3 = (Ascending Grades) 4 = (Descending Grades) )\n");
    printf("showAll file.txt\n");
    printf("listGrades file.txt\n");
    printf("listSome numofEntries pageNumber file.txt\n");
    printf("Use the \" sign as indicated in the name and letter grade.\n");
}

typedef struct {
    char name[100]; /* Enough array for Name and Surname */
    char grade[3];  /* Grade */
} Student;

void writeLog(const char *message) {
    pid_t pid = fork();
    
    if (pid == 0) { /* Child process */
        int fd = open("logs.txt", O_WRONLY | O_CREAT | O_APPEND, 0644); /*Open the log file with write-only access, create if not exist, append if it exists*/
        if (fd < 0) {
            perror("Failed to open log file");
            exit(1); /*Exit with an error in the child process*/
        }

        /*Formatting the current time for the log entry*/
        char timeStr[100];
        time_t now = time(NULL);
        struct tm *tm_info = localtime(&now);
        strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", tm_info);

        pid_t currentPid = getpid(); /*Getting the current process ID */
        char logBuffer[512];
        int logLength = snprintf(logBuffer, sizeof(logBuffer), "[%s] (PID: %d) %s\n", timeStr, currentPid, message);    /*Preparing the log message with timestamp, PID, and the custom message*/
        if (write(fd, logBuffer, logLength) != logLength) {
            perror("Failed to write to log file");
        }

        close(fd);
        exit(0); /*Successfully exit after writing the log */
    } else if (pid > 0) {
        wait(NULL); /*Parent process waits for the completion of the log recording*/
    } else {
        perror("Failed to fork");
    }
}

void createFile(const char *filename) {
    pid_t pid = fork();

    if (pid == -1) {            /*Fork failed*/
        writeLog("Failed to fork for creating file");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) { /*Child process*/
        int fd = open(filename, O_WRONLY | O_CREAT | O_EXCL, 0644); /* Open the file with write-only access, create it if it does not exist. Fail if the file already exists (O_CREAT with O_EXCL).*/
        if (fd == -1) {
            if (errno != EEXIST) {      /*Print error message unless the file already exists*/
                writeLog("Failed to create file (File might already exist or another error occurred)");
                perror("Failed to create file");
                exit(EXIT_FAILURE);
            } else {
                writeLog("File already exists, no need to create");
            }
        } else {    /*File created successfully*/
            writeLog("File created successfully");
            close(fd);
        }
        exit(EXIT_SUCCESS);
    } else {        /*Parent process*/ 
        wait(NULL); /* Wait for the child process to complete */
        writeLog("Completed file creation process");
    }
}

int compareStudentsAsc(const void *a, const void *b) {  /*Compare two students by their names in ascending order*/
    const Student *studentA = (const Student *)a;
    const Student *studentB = (const Student *)b;
    return strcmp(studentA->name, studentB->name);
}

int compareStudentsDesc(const void *a, const void *b) { /*Compare two students by their names in descending order*/
    const Student *studentA = (const Student *)a;
    const Student *studentB = (const Student *)b;
    return strcmp(studentB->name, studentA->name);
}

int compareGradesAsc(const void *a, const void *b) {    /*Compare two students by their grades in ascending order*/
    const Student *studentA = (const Student *)a;
    const Student *studentB = (const Student *)b;
    return strcmp(studentA->grade, studentB->grade);
}

int compareGradesDesc(const void *a, const void *b) {   /*Compare two students by their grades in descending order*/
    const Student *studentA = (const Student *)a;
    const Student *studentB = (const Student *)b;
    return strcmp(studentB->grade, studentA->grade);
}




ssize_t readLine(int fd, char *buffer, size_t n) {  /*Reads a line of text from a file descriptor into a buffer*/
    ssize_t numRead;        /*Number of bytes read by the last read() call*/
    size_t totalRead = 0;     /*Total number of bytes read so far*/
    char *buf;              /*Pointer to the next position in buffer*/
    char ch;                /*Current character read*/

    if (n <= 0 || buffer == NULL) {     /*Validate input parameters*/
        errno = EINVAL;             /*Set errno to indicate an invalid argument error*/
        return -1;
    }

    buf = buffer;           /*Initialize buf pointer to start of buffer*/

    while (1) {
        numRead = read(fd, &ch, 1);     /* Try to read a single byte (character)*/

        if (numRead == -1) {
            if (errno == EINTR)         /*If read() was interrupted by a signal, try again*/
                continue;
            else
                return -1;           /*An actual error occurred, return error code*/
        } else if (numRead == 0) {  /* Reached end of file*/
            if (totalRead == 0)     /* If no bytes were read, return 0 indicating EOF*/
                return 0;
            else                     /*Some bytes were read before reaching EOF, end the line*/
                break;
        } else {                    /*Successfully read a byte*/
            if (totalRead < n - 1) { /*If there's room in the buffer, save the character*/
                totalRead++;
                *buf++ = ch;
            }

            if (ch == '\n')         /* If the character is a newline, end the line*/
                break;
        }
    }

    *buf = '\0';                    /* Null-terminate the string in the buffer*/
    return totalRead;               /*Return the number of bytes (characters) read, excluding the null terminator*/
}

void sortAll(const char *filename, int sortOrder) {
    pid_t pid = fork();             /*Create a new process*/
    
    if (pid == -1) {                /*Fork failed*/
        writeLog("Failed to fork in sortAll");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {          /*Child process*/
        int fd = open(filename, O_RDONLY);      /*Open the file for reading*/
        if (fd == -1) {
            writeLog("Failed to open file in sortAll");
            perror("Failed to open file");
            exit(EXIT_FAILURE);
        }

        int capacity = 10;                              /*Initial capacity*/
        Student *students = malloc(capacity * sizeof(Student));     /*Dynamically allocate an array for storing student records*/
        if (!students) {
                writeLog("Failed to allocate memory for students in sortAll");
                perror("Failed to allocate memory");
                close(fd);
                exit(EXIT_FAILURE);
        }
        int size = 0;       /*Number of students loaded*/

        char line[256];         /* Buffer for reading lines*/
        while (readLine(fd, line, sizeof(line)) > 0) {      /*Read each line from the file and populate the students array*/
            if (size >= capacity) {
                capacity *= 2;                      /*Double the capacity if needed*/
                students = realloc(students, capacity * sizeof(Student));
                if (!students) {
                        writeLog("Failed to reallocate memory for students in sortAll");
                        perror("Failed to reallocate memory");
                        close(fd);
                        exit(EXIT_FAILURE);
                }
            }
            /*Parse the student name and grade from the line*/
            char *gradeStart = strrchr(line, ' ');
            if (gradeStart && strlen(gradeStart) > 1) {             /*Ensure there's a grade following the space.*/
                strncpy(students[size].grade, gradeStart + 1, sizeof(students[size].grade) - 1);        /*Copy the grade to the student structure, ensuring not to overflow the grade field*/
                *gradeStart = '\0';                                                                 /* Null-terminate the student's name at the space's location to isolate it from the grade*/
                strncpy(students[size].name, line, sizeof(students[size].name) - 1);        /*Copy the isolated name to the student structure, ensuring not to overflow the name field.*/
                size++;                                                             /*Increment the number of successfully parsed student records*/
            }
        }

        char logMessage[100];
        /*Sorting the sequence of students in the selected order*/
        if (sortOrder == 1) {            
            qsort(students, size, sizeof(Student), compareStudentsAsc);
            snprintf(logMessage, sizeof(logMessage), "Sorted %d students by name ascending", size);
        } else if (sortOrder == 2){
            qsort(students, size, sizeof(Student), compareStudentsDesc);
            snprintf(logMessage, sizeof(logMessage), "Sorted %d students by name descending", size);
        } else if (sortOrder == 3) {
            qsort(students, size, sizeof(Student), compareGradesAsc);
            snprintf(logMessage, sizeof(logMessage), "Sorted %d students by grade ascending", size);
        } else if (sortOrder == 4) {
            qsort(students, size, sizeof(Student), compareGradesDesc);
            snprintf(logMessage, sizeof(logMessage), "Sorted %d students by grade descending", size);
        } else{                                                     /*Handle incorrect sortOrder input*/
            writeLog("Incorrect sequence selection in sortAll");
            perror("Incorrect sequence selection");
            free(students);                                     /*Free the dynamically allocated memory*/
            close(fd);                                          /* Close the file descriptor*/
            exit(EXIT_FAILURE);                                 /*Exit the child process successfully*/
        }
        writeLog(logMessage);                               /*Log the sorting outcome*/

        for (int i = 0; i < size; i++) {                            /*Print the sorted student records*/
            printf("%s %s\n", students[i].name, students[i].grade);
        }

        free(students);                         /*Free the dynamically allocated memory*/
        close(fd);                              /* Close the file descriptor*/
        exit(EXIT_SUCCESS);                     /*Exit the child process successfully*/
    }else {
        /*Parent process*/
        wait(NULL); /*Wait for the child process to complete*/
    }

}


void addStudentGrade(const char *filename, const char *studentName, const char *grade) {
    pid_t pid = fork();     /*Create a child process*/

    if (pid == -1) {                /*Fork failed*/

        writeLog("Failed to fork in addStudentGrade");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {          /*Child process*/
        int fileDescriptor = open(filename, O_WRONLY | O_APPEND, 0644); /*Open the file with write and append mode, set permissions to 644*/
        if (fileDescriptor < 0) {                                   /*If opening the file fails, log the error and exit with failure*/
            writeLog("Failed to open file in addStudentGrade");
            perror("Failed to open file");
            exit(EXIT_FAILURE);
        }
        
        char buffer[256];
        int length = snprintf(buffer, sizeof(buffer), "%s, %s\n", studentName, grade);      /*Set the data in the format to be written*/
        
        if (write(fileDescriptor, buffer, length) != length) {      /*Write the prepared data to the file*/
            /*If writing fails, log the error and exit with failure*/
            writeLog("Failed to write to file in addStudentGrade");
            perror("Failed to write to file");
            close(fileDescriptor);
            exit(EXIT_FAILURE);
        }

        writeLog("Added a new student grade."); /*Log a successful addition */ 
        close(fileDescriptor);          /*Close the file descriptor */
        exit(EXIT_SUCCESS);             /*Exit the child process successfully*/
    } else { /* Parent process*/
        wait(NULL); /*Wait for the child process to complete*/
    }
}

void searchStudent(const char *filename, const char *fullName) {
    pid_t pid = fork();     /*Create a child process*/

    if (pid == -1) {                 /*Fork failed*/
        writeLog("Failed to fork in searchStudent");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {           /*Child process*/
        int fd = open(filename, O_RDONLY);      /* Open the specified file in read-only mode*/
        if (fd == -1) {                         /*If opening the file fails, log the error and exit with failure*/
            writeLog("Failed to open file in searchStudent");
            perror("Failed to open file");
            exit(EXIT_FAILURE);
        }

        char line[256];                            /*Buffer for storing each line read from the file*/
        int found = 0;                              /*Flag to indicate if the student record was found*/

        while (readLine(fd, line, sizeof(line)) > 0) {      /*Read through the file line by line*/
            if (strstr(line, fullName)) {               /*Check if the current line contains the full name of the student*/
                printf("%s", line);                     /*If found, print the student's record*/
                found = 1;
                break;
            }
        }

        if (!found) {                           /*If the student was not found, log and indicate it to the user*/
            printf("Student not found.\n");
            char logMessage[256];
            snprintf(logMessage, sizeof(logMessage), "Search for student '%s' in '%s': Not found.", fullName, filename);
            writeLog(logMessage);
        } else{                             /*If the student was found, log this event*/
            char logMessage[256];
            snprintf(logMessage, sizeof(logMessage), "Search for student '%s' in '%s': Found.", fullName, filename);
            writeLog(logMessage);
        }
        close(fd);                  /*Close the file descriptor */
        exit(EXIT_SUCCESS);         /*Exit the child process successfully*/
    } else {        /* Parent process*/
        wait(NULL); /*Wait for the child process to complete*/
    }
}

void showAll(const char *filename) {
    pid_t pid = fork();                         /*Create a child process*/

    if (pid == -1) {                             /*Fork failed*/
        writeLog("Failed to fork in showAll");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {                       /*Child process*/   
        int fd = open(filename, O_RDONLY);      /*Open the file in read-only mode*/
        if (fd == -1) {                         /*If opening the file fails, log the error and exit with failure*/
            writeLog("Failed to open file in showAll");
            perror("Failed to open file");
            exit(EXIT_FAILURE);
        }

        char line[256];                             /* Buffer for storing each line read from the file*/
        int count = 0;                              /* Counter for the number of entries displayed*/
        while (readLine(fd, line, sizeof(line)) > 0) {          /*Read and display each line from the file until EOF*/
            printf("%s", line);                         /*Print the current line*/
            count++;                                    /*Increment the counter for each entry displayed*/
        }

        if(count == 0) {                            /*If no entries were found, log and indicate it to the user*/
            writeLog("No student grades found in file during showAll");
            printf("No student grades found.\n");
        } else {                                    /*If entries were found, log the number of entries displayed*/
            char logMessage[256];
            snprintf(logMessage, sizeof(logMessage), "Displayed all student grades from '%s', total %d entries.", filename, count);
            writeLog(logMessage);
        }

        close(fd);                      /*Close the file descriptor */
        exit(EXIT_SUCCESS);             /*Exit the child process successfully*/
    } else {    /*Parent process*/
        wait(NULL);     /* Wait for the child process to complete*/
    }
}

void listGrades(const char *filename) {
    pid_t pid = fork();                     /*Create a child process*/

    if (pid == -1) {                        /*Fork failed*/
        writeLog("Failed to fork in listGrades");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {                 /*Child process*/    
        int fd = open(filename, O_RDONLY);  /*Open the file in read-only mode*/
        if (fd == -1) {                                 /*If opening the file fails, log the error and exit with failure*/
            writeLog("Failed to open file in listGrades");
            perror("Failed to open file");
            exit(EXIT_FAILURE);
        }

        char line[256];                 /* Buffer for storing each line read from the file*/
        int count = 0;                  /* Counter for the number of entries displayed*/

        while (readLine(fd, line, sizeof(line)) > 0 && count < 5) { /*Read and print up to the first 5 lines (entries) from the file*/
            printf("%s", line);             /*Print the current line*/
            count++;                        /*Increment the counter for each entry displayed*/
        }
        if(count == 0) {                     /*If no entries were found, log and indicate it to the user*/
            writeLog("No student grades found in file during listGrades");
            printf("No student grades found.\n");
        } else {                            /*If entries were found, log the count of entries displayed*/
            char logMessage[256];
            snprintf(logMessage, sizeof(logMessage), "Listed first 5 student grades from '%s'.", filename);
            writeLog(logMessage);
        }
        close(fd);                              /*Close the file descriptor */
        exit(EXIT_SUCCESS);                     /*Exit the child process successfully*/    
    } else {         /*Parent process*/                           
        wait(NULL);  /* Wait for the child process to complete*/
    }
}

void listSome(const char *filename, int numofEntries, int pageNumber) {
    pid_t pid = fork();                     /*Create a child process*/

    if (pid == -1) {                        /*Fork failed*/
        writeLog("Failed to fork in listSome");
        perror("Failed to fork");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {                  /*Child process*/  
        int fd = open(filename, O_RDONLY);      /*Open the file in read-only mode*/
        if (fd == -1) {                         /*If opening the file fails, log the error and exit with failure*/
            writeLog("Failed to open file in listSome");
            perror("Failed to open file");
            exit(EXIT_FAILURE);
        }

        char line[256];                             /*Buffer to store each line read from the file*/
        int startLine = numofEntries * (pageNumber - 1);    /*Calculate the starting line number based on pagination*/
        int endLine = startLine + numofEntries;             /*Calculate the ending line number*/
        int currentLine = 0;                                /*Current line number tracker*/
        int printedLines = 0;                               /*Counter for printed lines*/

        while (readLine(fd, line, sizeof(line)) > 0) {          /*Read through the file and print lines within the specified range*/
            if (currentLine >= startLine && currentLine < endLine) {
                printf("%s", line);                             /*Print the current line if it's within the range*/
                printedLines++;                                 /*Increment the count of printed lines*/
            }
            currentLine++;                                      /*Move to the next line*/
            if (currentLine >= endLine) break;                  /*Stop reading if the end of the range is reached*/
        }

        if(printedLines == 0) {             /*Handle cases where no lines were printed due to pagination limits*/
            writeLog("No entries were printed in listSome due to page number or entries number being out of range");
            printf("No entries found in the specified range.\n");
        } else {                        /*Log successful listing of entries within the specified range*/
            char logMessage[256];
            snprintf(logMessage, sizeof(logMessage), "Listed entries from '%s' between lines %d and %d.", filename, startLine + 1, endLine);
            writeLog(logMessage);
        }

        close(fd);                  /*Close the file descriptor */
        exit(EXIT_SUCCESS);         /*Exit the child process successfully*/   
    } else {            /*Parent process*/
        wait(NULL);     /* Wait for the child process to complete*/    
    }
}



int main() {
    char input[256];            /*Buffer to store user input*/

    while (1) {                 /*Infinite loop to keep the program running until 'exit' command is entered*/
        printf("> ");
        if (!fgets(input, sizeof(input), stdin)) {      /*If reading input fails, skip the rest of the loop and prompt again*/
            continue;
        }

        input[strcspn(input, "\n")] = 0;        /*Remove the newline character at the end of the input, if present*/

        if (strncmp(input, "exit", 4) == 0) {           /*Check if the input command is 'exit' to terminate the program*/
            writeLog("The program was terminated.");        /*Log program termination*/
            break;
        }


        char *command = strtok(input, " ");         /*Tokenize the input to separate the command from its arguments*/
        if (command == NULL) {
            continue;  /* If no command is entered, prompt again*/
        }

        /*Handle commands with corresponding actions*/
        if (strcmp(command, "addStudentGrade") == 0) {  /*Parse arguments for adding a student grade*/
            char *name = strtok(NULL, "\"");
            strtok(NULL, "\"");             /*Skip the space between name and grade*/
            char *grade = strtok(NULL, "\"");
            char *filename = strtok(NULL, " ");
            if (name && grade && filename) {                /*If all arguments are present, call the function to add a student grade*/
                addStudentGrade(filename, name, grade);
            } else {
                printf("Invalid input for addStudentGrade.\n");
            }
        } else if (strcmp(command, "searchStudent") == 0) {     /* Parse arguments for searching a student*/
            char *name = strtok(NULL, "\"");
            char *filename = strtok(NULL, " ");
            if (name && filename) {                         /*If the required arguments are present, call the function to search for a student*/
                searchStudent(filename, name);  
            } else {
                printf("Invalid input for searchStudent.\n");
            }
        } else if (strcmp(command, "showAll") == 0) {       /*Parse filename argument for showing all grades*/
            char *filename = strtok(NULL, " ");
            if (filename) {                             /*If filename is specified, call the function to show all student grades*/
                showAll(filename);  
            } else {
                printf("Filename is missing.\n");
            }
        } else if (strcmp(command, "listGrades") == 0) {    /* Parse filename argument for listing grades*/
            char *filename = strtok(NULL, " ");
            if (filename) {                                 /*If filename is specified, call the function to list grades*/
                listGrades(filename);
            } else {
                printf("Filename is missing.\n");
            }
        } else if (strcmp(command, "sortAll") == 0) {       /*Parse arguments for sorting all entries*/
            char *filename = strtok(NULL, " ");
            char *sortOrderStr = strtok(NULL, " ");
            if (filename && sortOrderStr) {             
                int sortOrder = atoi(sortOrderStr);         /*Convert sort order from string to integer*/
                sortAll(filename,sortOrder);                /*Call the function to sort all entries based on the specified order*/
            } else {
                printf("Filename is missing.\n");
            }
        } else if (strcmp(command, "listSome") == 0) {  /* Parse arguments for listing a subset of entries*/
            char *numEntriesStr = strtok(NULL, " ");    
            char *pageNumStr = strtok(NULL, " ");
            char *filename = strtok(NULL, " ");
            if (numEntriesStr && pageNumStr && filename) {
                int numEntries = atoi(numEntriesStr);       /*Convert pagination arguments from string to integer*/
                int pageNum = atoi(pageNumStr);
                listSome(filename, numEntries, pageNum);    /*Call the function to list a subset of entries based on pagination parameters*/
            } else {    
                printf("Invalid input for listSome.\n");
            }
        }else if (strcmp(command, "gtuStudentGrades") == 0) {      /* Parse filename argument for gtuStudentGrades*/
            char *filename = strtok(NULL, " ");
            if (filename) {                             /*If filename is specified, create a file*/
                createFile(filename); 
            } else {                                    /*Show the list of commands if there are no arguments*/
            displayCommands();
            }
        }else {                                 /*Handle unknown commands*/
            writeLog("Error: Unknown command");
            perror("Unknown command");
        }
    }

    return 0;
}

