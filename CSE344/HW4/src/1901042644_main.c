#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>
#include <signal.h>
#include <limits.h>
#include <errno.h>

#define MAX_PATH_LEN (PATH_MAX + 256)       /*Define the maximum path length for files*/

/*Structure for items in the buffer used for copying files*/
typedef struct {
    int src_fd;                         /*File descriptor for the source file*/
    int dst_fd;                         /*File descriptor for the destination file*/
    char src_name[MAX_PATH_LEN];        /*Path of the source file*/
    char dst_name[MAX_PATH_LEN];        /*Path of the destination file*/
    int is_fifo;                        /*Flag indicating if the file is a FIFO*/
} buffer_item;
/*Structure for passing arguments to threads*/
typedef struct {
    char *src_dir;      /*Source directory path*/
    char *dst_dir;      /*Destination directory path*/
} thread_args;
/*Global variables to keep track of files and directories processed*/
int regular_files = 0, fifo_files = 0, directories = 0;
long long total_bytes_copied = 0;
struct timeval start, end;          /*Variables for timing the execution*/
/* Buffer and synchronization tools*/
buffer_item *buffer = NULL;
int buffer_size = 0;
int workers_num = 0;
int buffer_count = 0;
int buffer_index = 0;
volatile sig_atomic_t done = 0;             /*Flag for signaling completion*/
pthread_mutex_t buffer_mutex;
pthread_cond_t buffer_not_full;
pthread_cond_t buffer_not_empty;

pthread_t manager_tid;                  /*Thread identifier for the manager thread*/
pthread_t *worker_tids;                 /*Array of thread identifiers for worker threads*/

void print_usage();
void handle_signal(int sig);
void print_statistics();
void release_resources();
void *worker_thread(void *arg);
void copy_file(buffer_item *item);
void *manager_thread(void *arg);
void perror_with_context(const char *message, const char *file);
void process_directory(const char *src_dir, const char *dst_dir);
void setup_signal_handling();

int main(int argc, char *argv[]) {
    if (argc != 5) {                    /*Check for correct number of arguments*/
        print_usage();  
        return EXIT_FAILURE;
    }
    /*Initialize buffer and worker threads*/
    buffer_size = atoi(argv[1]);        
    workers_num = atoi(argv[2]);
    char *src_dir = argv[3];
    char *dst_dir = argv[4];
    /*Allocate memory for buffer and worker thread IDs*/
    buffer = malloc(buffer_size * sizeof(buffer_item));
    worker_tids = malloc(workers_num * sizeof(pthread_t));
    if (!buffer || !worker_tids) {
        fprintf(stderr, "Failed to allocate memory for buffer or worker_tids\n");
        free(buffer);
        free(worker_tids);
        return EXIT_FAILURE;
    }
    /*Initialize mutex and condition variables*/
    pthread_mutex_init(&buffer_mutex, NULL);
    pthread_cond_init(&buffer_not_full, NULL);
    pthread_cond_init(&buffer_not_empty, NULL);
    setup_signal_handling();

    gettimeofday(&start, NULL);                 /*Start timing the operation*/
    /*Start the manager thread*/
    thread_args args = {src_dir, dst_dir};
    if (pthread_create(&manager_tid, NULL, manager_thread, &args) != 0) {
        perror("Failed to create manager thread");
        release_resources();
        return EXIT_FAILURE;
    }
    /*Start worker threads*/
    for (int i = 0; i < workers_num; i++) {
        if (pthread_create(&worker_tids[i], NULL, worker_thread, NULL) != 0) {
            perror("Failed to create worker thread");
            done = 1;
            pthread_cond_broadcast(&buffer_not_empty);     /*Notify all waiting threads to exit*/
            break;
        }
    }

    /*Wait for the manager thread to finish*/
    pthread_join(manager_tid, NULL);

    /*Ensure all worker threads are finished*/
    for (int i = 0; i < workers_num; i++) {
        if (worker_tids[i]) {
            pthread_join(worker_tids[i], NULL);
        }
    }

    gettimeofday(&end, NULL);   /*End timing*/
    print_statistics();         /*Print statistics about the operation*/
    release_resources();        /*Clean up resources*/
    return EXIT_SUCCESS;
}
void setup_signal_handling() {         /*Function to set up signal handling for shutdown*/
    struct sigaction sa;    
    sa.sa_handler = handle_signal;      /*Points to the handler function for signals*/
    sigemptyset(&sa.sa_mask);           /*Initializes the signal set to empty, so no signals are blocked during execution of the handler*/
    sa.sa_flags = 0;                    /*No flags are set*/
    sigaction(SIGINT, &sa, NULL);       /*Associates SIGINT (Ctrl+C) with the specified action*/
    sigaction(SIGTERM, &sa, NULL);      /*Associates SIGTERM (sent from another process) with the specified action*/
}
void handle_signal(int sig) {                       /*Signal handler function to handle SIGINT and SIGTERM.*/
    done = 1;                                       /*Set the global 'done' flag to true, indicating the program should terminate*/
    pthread_cond_broadcast(&buffer_not_empty);      /*Wake up all worker threads potentially waiting on the 'buffer_not_empty' condition variable*/
}
/*Function to release allocated resources*/
void release_resources() {
    if (buffer) {
        free(buffer);           /*Frees the dynamically allocated buffer used for storing file information*/
        buffer = NULL;          /*Nullifies the pointer after freeing to avoid dangling references*/
    }   
    if (worker_tids) {
        free(worker_tids);      /*Frees the dynamically allocated array of worker thread identifiers*/
        worker_tids = NULL;     /*Nullifies the pointer to avoid dangling references*/
    }
    pthread_mutex_destroy(&buffer_mutex);       /*Destroys the mutex to free up resources*/
    pthread_cond_destroy(&buffer_not_full);     /*Destroys the condition variable used for indicating the buffer is not full*/
    pthread_cond_destroy(&buffer_not_empty);    /*Destroys the condition variable used for indicating the buffer is not empty*/
}


void print_statistics() {                   /*Outputs the statistics of the file copy operation, including timing and file count information*/
    char buffer[1024];
    int len;

    long seconds = end.tv_sec - start.tv_sec;
    long microseconds = end.tv_usec - start.tv_usec;
    long total_time_in_milliseconds = (seconds * 1000) + (microseconds / 1000);
    long minutes = total_time_in_milliseconds / 60000;
    long seconds_remainder = (total_time_in_milliseconds % 60000) / 1000;
    long milliseconds = total_time_in_milliseconds % 1000;

    len = snprintf(buffer, sizeof(buffer),
        "\n---------------STATISTICS--------------------\n"
        "Consumers: %d - Buffer Size: %d\n"
        "Number of Regular Files: %d\n"
        "Number of FIFO Files: %d\n"
        "Number of Directories: %d\n"
        "TOTAL BYTES COPIED: %lld bytes\n"
        "TOTAL TIME: %02ld:%02ld.%03ld (min:sec.mili)\n",
        workers_num, buffer_size, regular_files, fifo_files, directories,
        total_bytes_copied, minutes, seconds_remainder, milliseconds);

    write(STDOUT_FILENO, buffer, len);
}

void print_usage() {                        /*Function to print program usage instructions*/
    const char message[] = "Usage: ./MWCp <buffer_size> <workers_num> <src_dir> <dst_dir>\n";
    write(STDOUT_FILENO, message, sizeof(message) - 1);  
}

void *worker_thread(void *arg) {            /* Worker thread function for processing files from the buffer*/
    (void)arg;  /*Unused parameter*/ 

    while (1) {                         /*Continuously process items until signaled to terminate*/
        pthread_mutex_lock(&buffer_mutex);          /*Lock the buffer for exclusive access*/

        while (buffer_count == 0 && !done) {                /*Wait until there is an item in the buffer or the operation is done*/
            pthread_cond_wait(&buffer_not_empty, &buffer_mutex);
        }

        if (buffer_count == 0 && done) {                /*Exit loop if no items are left and operation is done*/
            pthread_mutex_unlock(&buffer_mutex);        /*Always release the mutex before exiting*/
            break;
        }
        /*Get the next item from the buffer for processing*/
        buffer_item item = buffer[(buffer_index - buffer_count + buffer_size) % buffer_size];
        buffer_count--;                 /*Decrement the count of items in the buffer*/
       
        pthread_cond_signal(&buffer_not_full);       /*Signal that there is space in the buffer now*/
        pthread_mutex_unlock(&buffer_mutex);        /*Unlock the buffer*/

        if (item.is_fifo) {                             /* Handle FIFO files differently than regular files*/
            if (mkfifo(item.dst_name, 0644) != 0) {
                perror_with_context("Failed to create FIFO", item.dst_name);
            } else {
                pthread_mutex_lock(&buffer_mutex);
                fifo_files++;                           /*Increment the count of FIFO files processed*/
                pthread_mutex_unlock(&buffer_mutex);
            }
        } else {
            copy_file(&item);               /*Call a separate function to handle the copying of regular files*/
            /*Close file descriptors to avoid leaks*/
            close(item.src_fd);
            close(item.dst_fd);
        }
    }

    pthread_exit(NULL);                         /*Exit the worker thread*/
}

void copy_file(buffer_item *item) {                 /*Function to copy a file from source to destination*/
    char buffer[BUFSIZ];                            /*Buffer for reading and writing data*/
    ssize_t nread;
    long long file_bytes = 0;                       /*Track the number of bytes copied for this file*/

    while ((nread = read(item->src_fd, buffer, sizeof(buffer))) > 0) {  /*Read data from source file and write it to destination file*/
        file_bytes += nread;
        if (write(item->dst_fd, buffer, nread) != nread) {
            perror("Failed to write to destination file");
            return;
        }
    }

    if (nread < 0) {                                /*Check for read errors*/
        perror("Failed to read from source file");
    } else {
        total_bytes_copied += file_bytes;           /*Update global byte count*/
        pthread_mutex_lock(&buffer_mutex);
        regular_files++;                            /*Increment the count of regular files processed*/
        pthread_mutex_unlock(&buffer_mutex);
    }
}

void *manager_thread(void *arg) {                   /*Manager thread function to populate the buffer with file descriptors*/
    thread_args *args = (thread_args *)arg;
    char *src_dir = args->src_dir;
    char *dst_dir = args->dst_dir;
    process_directory(src_dir, dst_dir);            /*Process the entire directory recursively*/

    pthread_mutex_lock(&buffer_mutex);              /*Signal that processing is complete*/
    done = 1;
    pthread_cond_broadcast(&buffer_not_empty);
    pthread_mutex_unlock(&buffer_mutex);

    pthread_exit(NULL);                             /*Exit the manager thread*/
}

void process_directory(const char *src_dir, const char *dst_dir) {      /*Function to recursively process each file and directory within a given source directory*/
    DIR *dir;
    struct dirent *entry;

    if ((dir = opendir(src_dir)) == NULL) {                 /*Attempt to open the source directory*/
        perror("Failed to open source directory");
        return;
    }

    mkdir(dst_dir, 0755);                               /*Ensure the destination directory exists. If the directory already exists, this function call will do nothing*/

    while ((entry = readdir(dir)) != NULL) {            /*Read each entry in the directory*/
        char src_path[PATH_MAX];
        char dst_path[PATH_MAX];

        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;                   /*Skip the '.' and '..' directories*/
        }
        /*Construct full source and destination paths for the current entry*/
        snprintf(src_path, sizeof(src_path), "%s/%s", src_dir, entry->d_name);
        snprintf(dst_path, sizeof(dst_path), "%s/%s", dst_dir, entry->d_name);

        struct stat statbuf;                                /*Get file attributes to determine if it's a regular file, directory, or other type*/
        if (stat(src_path, &statbuf) == 0) {
            if (S_ISREG(statbuf.st_mode)) {                     /*Handling for regular files*/
                buffer_item item;                               /*Prepare a buffer item for a regular file*/
                strncpy(item.src_name, src_path, sizeof(item.src_name));
                strncpy(item.dst_name, dst_path, sizeof(item.dst_name));
                item.is_fifo = 0;

                item.src_fd = open(item.src_name, O_RDONLY);        /*Open the source file for reading*/
                if (item.src_fd < 0) {
                    perror_with_context("Failed to open source file", item.src_name);
                    continue;
                }

                item.dst_fd = open(item.dst_name, O_WRONLY | O_CREAT | O_TRUNC, 0644);      /*Open the destination file for writing, creating it if it does not exist, or truncating it if it does*/
                if (item.dst_fd < 0) {
                    perror_with_context("Failed to open destination file", item.dst_name);
                    close(item.src_fd);
                    continue;
                }

                pthread_mutex_lock(&buffer_mutex);                  /*Lock the buffer mutex before manipulating the buffer*/

                while (buffer_count == buffer_size && !done) {              /*Wait until there is space in the buffer*/
                    pthread_cond_wait(&buffer_not_full, &buffer_mutex);
                }

                if (done) {                                     /*Check if a termination signal was received while waiting*/
                    pthread_mutex_unlock(&buffer_mutex);
                    close(item.src_fd);
                    close(item.dst_fd);
                    break;
                }

                buffer[buffer_index] = item;                                        /*Add the file to the buffer and adjust the control variables*/
                buffer_index = (buffer_index + 1) % buffer_size;
                buffer_count++;

                pthread_cond_signal(&buffer_not_empty);             /*Signal any waiting worker threads that there is new data in the buffer*/
                pthread_mutex_unlock(&buffer_mutex);

            } else if (S_ISFIFO(statbuf.st_mode)) {                         /*Handling for FIFO special files*/
                buffer_item item;
                strncpy(item.src_name, src_path, sizeof(item.src_name));
                strncpy(item.dst_name, dst_path, sizeof(item.dst_name));
                item.is_fifo = 1;
                item.src_fd = -1;
                item.dst_fd = -1;

                pthread_mutex_lock(&buffer_mutex);

                while (buffer_count == buffer_size && !done) {                  /*Similar waiting mechanism as for regular files*/
                    pthread_cond_wait(&buffer_not_full, &buffer_mutex);
                }

                if (done) {
                    pthread_mutex_unlock(&buffer_mutex);
                    break;
                }

                buffer[buffer_index] = item;                        /*Add the FIFO to the buffer*/
                buffer_index = (buffer_index + 1) % buffer_size;
                buffer_count++;

                pthread_cond_signal(&buffer_not_empty);
                pthread_mutex_unlock(&buffer_mutex);

            } else if (S_ISDIR(statbuf.st_mode)) {              /*Handling for directories*/
                pthread_mutex_lock(&buffer_mutex);                
                directories++;                                  /*Increment directory count safely*/
                pthread_mutex_unlock(&buffer_mutex);

                process_directory(src_path, dst_path);          /*Recursively process the subdirectory*/
            }
        }
    }

    closedir(dir);                                      /*Clean up by closing the directory stream.*/
}

void perror_with_context(const char *message, const char *file) {       /*Error reporting function with additional context*/
    fprintf(stderr, "%s: %s - %s\n", message, file, strerror(errno));
}
