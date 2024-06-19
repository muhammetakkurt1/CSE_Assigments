#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include <semaphore.h>
#include <unistd.h>

#define PERMA_AUTOMOBILE_SPACES 8
#define PERMA_PICKUP_SPACES 4
#define TEMP_AUTOMOBILE_SPACES 8
#define TEMP_PICKUP_SPACES 4

/*Semaphore declarations*/
sem_t newAutomobile, newPickup;
sem_t inChargeforAutomobile, inChargeforPickup;
sem_t exitSignal; 

/*Counter variables for free spots*/
int mFree_automobile = TEMP_AUTOMOBILE_SPACES;
int mFree_pickup = TEMP_PICKUP_SPACES;
int pFree_automobile = PERMA_AUTOMOBILE_SPACES;
int pFree_pickup = PERMA_PICKUP_SPACES;

/*Mutex for protecting the counters*/
pthread_mutex_t automobile_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t pickup_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t temp_automobile_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t temp_pickup_mutex = PTHREAD_MUTEX_INITIALIZER;

void logStatus() {          /*Function to log the current status of parking spaces*/
    char buffer[256];  
    int len;
    pthread_mutex_lock(&automobile_mutex);
    pthread_mutex_lock(&pickup_mutex);
    pthread_mutex_lock(&temp_automobile_mutex);
    pthread_mutex_lock(&temp_pickup_mutex);
    
    len = snprintf(buffer, sizeof(buffer), "Temporary Automobile Parking Spaces: %d/%d, Temporary Pickup Parking Spaces: %d/%d\n",
                   TEMP_AUTOMOBILE_SPACES - mFree_automobile, TEMP_AUTOMOBILE_SPACES,
                   TEMP_PICKUP_SPACES - mFree_pickup, TEMP_PICKUP_SPACES);
    write(STDOUT_FILENO, buffer, len);

    len = snprintf(buffer, sizeof(buffer), "Permanent Automobile Parking Spaces: %d/%d, Permanent Pickup Parking Spaces: %d/%d\n\n\n",
                   PERMA_AUTOMOBILE_SPACES - pFree_automobile, PERMA_AUTOMOBILE_SPACES,
                   PERMA_PICKUP_SPACES - pFree_pickup, PERMA_PICKUP_SPACES);
    write(STDOUT_FILENO, buffer, len);

    pthread_mutex_unlock(&temp_pickup_mutex);
    pthread_mutex_unlock(&temp_automobile_mutex);
    pthread_mutex_unlock(&pickup_mutex);
    pthread_mutex_unlock(&automobile_mutex);
}

void* carOwner(void* arg) {         /*Thread function for car owners*/
    char buffer[256];  
    int len;
    while (1) {
        int vehicle_type = rand() % 2;  /*0 for automobile, 1 for pickup*/
        
        if (vehicle_type == 0) {  /*If automobile*/
            pthread_mutex_lock(&temp_automobile_mutex);
            
            if (mFree_automobile > 0) {
                mFree_automobile--;
                len = snprintf(buffer, sizeof(buffer), "The automobile arrives at the parking lot entrance. Available temporary automobile parking spaces: %d\n", mFree_automobile+1);
                write(STDOUT_FILENO, buffer, len);
                pthread_mutex_unlock(&temp_automobile_mutex);
                sem_post(&newAutomobile);               /*Signal the arrival of a new automobile*/
                sem_wait(&inChargeforAutomobile);       /*Wait for the attendant to park the car*/
            } else {
                pthread_mutex_unlock(&temp_automobile_mutex);
                len = snprintf(buffer, sizeof(buffer), "The automobile arrives at the parking lot entrance but there is no temporary space for automobiles. The automobile owner is leaving.\n\n");
                write(STDOUT_FILENO, buffer, len);            
            }
        } else {                    /*If pickup*/
            pthread_mutex_lock(&temp_pickup_mutex);
            if (mFree_pickup > 0) {
                mFree_pickup--;
                len = snprintf(buffer, sizeof(buffer), "The pickup arrives at the parking lot entrance. Available temporary pickup parking spaces: %d\n", mFree_pickup+1);
                write(STDOUT_FILENO, buffer, len);
                pthread_mutex_unlock(&temp_pickup_mutex);
                sem_post(&newPickup);           /* Signal the arrival of a new pickup*/
                sem_wait(&inChargeforPickup);   /*Wait for the attendant to park the pickup*/
            } else {
                pthread_mutex_unlock(&temp_pickup_mutex);
                len = snprintf(buffer, sizeof(buffer), "The pickup arrives at the parking lot entrance but there is no temporary space for pickups. The pickup owner is leaving.\n\n\n");
                write(STDOUT_FILENO, buffer, len);
            }
        }
        sleep(rand() % 5);          /*Simulate the time before the next car arrives*/
    }
}

void* carAttendant(void* arg) {             /*Thread function for car attendants*/
    int vehicle_type = *(int*)arg;
    char buffer[256];  
    int len;
    while (1) {
        if (vehicle_type == 0) {                            /*Attendant for automobiles*/
            sem_wait(&newAutomobile);                           /*Wait for a new automobile to arrive*/
            sleep(1);                                       /*Simulate parking time in temporary space*/
            pthread_mutex_lock(&temp_automobile_mutex);
            len = snprintf(buffer, sizeof(buffer), "The automobile was parked in a temporary parking lot by the valet. Available temporary automobile parking spaces: %d\n", mFree_automobile);
            write(STDOUT_FILENO, buffer, len);
            sem_post(&inChargeforAutomobile);                   /*Signal that the car has been parked*/
            pthread_mutex_unlock(&temp_automobile_mutex);
            
            if (mFree_automobile == 0) {                    /*Temporary parking full, move to permanent*/
                pthread_mutex_lock(&automobile_mutex);
                pthread_mutex_lock(&pickup_mutex);
                pthread_mutex_lock(&temp_automobile_mutex);
                pthread_mutex_lock(&temp_pickup_mutex);
                len = snprintf(buffer, sizeof(buffer), "\nThe temporary automobile parking lot is full. If there is space in the permanent park, the valet will start moving the automobiles.\n");
                write(STDOUT_FILENO, buffer, len);
                while (pFree_automobile > 0 && mFree_automobile < TEMP_AUTOMOBILE_SPACES) {
                    mFree_automobile++;
                    pFree_automobile--;
                    len = snprintf(buffer, sizeof(buffer), "Automobile permanently parked. Available permanent automobile parking spaces: %d, Available temporary automobile parking spaces: %d\n",
                           pFree_automobile, mFree_automobile);
                    write(STDOUT_FILENO, buffer, len);
                    sleep(1);                               /*Simulate moving time*/
                }
                pthread_mutex_unlock(&temp_pickup_mutex);
                pthread_mutex_unlock(&temp_automobile_mutex);
                pthread_mutex_unlock(&pickup_mutex);
                pthread_mutex_unlock(&automobile_mutex);
            }
        } else {                                /*Attendant for pickups*/
            sem_wait(&newPickup);               /*Wait for a new pickup to arrive*/
            sleep(1);                           /*Simulate parking time in temporary space*/
            pthread_mutex_lock(&temp_pickup_mutex);
            len = snprintf(buffer, sizeof(buffer), "The pickup was parked in a temporary parking lot by the valet. Available temporary pickup parking spaces: %d\n", mFree_pickup);
            write(STDOUT_FILENO, buffer, len);
            sem_post(&inChargeforPickup);                   /*Signal that the pickup has been parked*/
            pthread_mutex_unlock(&temp_pickup_mutex);
            
            if (mFree_pickup == 0) {                        /*Temporary parking full, move to permanent*/
                pthread_mutex_lock(&automobile_mutex);
                pthread_mutex_lock(&pickup_mutex);
                pthread_mutex_lock(&temp_automobile_mutex);
                pthread_mutex_lock(&temp_pickup_mutex);
                len = snprintf(buffer, sizeof(buffer), "\nThe temporary pickup parking lot is full. If there is space in the permanent park, the valet will start moving the pickups.\n");
                write(STDOUT_FILENO, buffer, len);
                while (pFree_pickup > 0 && mFree_pickup < TEMP_PICKUP_SPACES) {
                    mFree_pickup++;
                    pFree_pickup--;
                    len = snprintf(buffer, sizeof(buffer), "Pickup permanently parked. Available permanent pickup parking spaces: %d, Available temporary pickup parking spaces: %d\n",
                           pFree_pickup, mFree_pickup);
                    write(STDOUT_FILENO, buffer, len);
                    sleep(1);                                  /*Simulate moving time*/
                }
                pthread_mutex_unlock(&temp_pickup_mutex);
                pthread_mutex_unlock(&temp_automobile_mutex);
                pthread_mutex_unlock(&pickup_mutex);
                pthread_mutex_unlock(&automobile_mutex);
            }
        }

        logStatus();                            /*Log the status after each parking operation*/
        
        pthread_mutex_lock(&automobile_mutex);
        pthread_mutex_lock(&pickup_mutex);
        pthread_mutex_lock(&temp_automobile_mutex);
        pthread_mutex_lock(&temp_pickup_mutex);
        
        if (mFree_automobile == 0 && pFree_automobile == 0 && mFree_pickup == 0 && pFree_pickup == 0) {     /*Check if all parking spots are full*/
            sem_post(&exitSignal);  /*Signal main thread to exit*/
        }
        
        pthread_mutex_unlock(&temp_pickup_mutex);
        pthread_mutex_unlock(&temp_automobile_mutex);
        pthread_mutex_unlock(&pickup_mutex);
        pthread_mutex_unlock(&automobile_mutex);
    }
}

int main() {
    srand(time(NULL));
    char buffer[256];  
    int len;
    pthread_t owner_thread, automobile_attendant_thread, pickup_attendant_thread;
    int automobile = 0, pickup = 1;

    /*Initialize semaphores*/
    sem_init(&newAutomobile, 0, 0);
    sem_init(&newPickup, 0, 0);
    sem_init(&inChargeforAutomobile, 0, 0);
    sem_init(&inChargeforPickup, 0, 0);
    sem_init(&exitSignal, 0, 0); 

    /*Create threads*/
    pthread_create(&owner_thread, NULL, carOwner, NULL);
    pthread_create(&automobile_attendant_thread, NULL, carAttendant, &automobile);
    pthread_create(&pickup_attendant_thread, NULL, carAttendant, &pickup);

    /*Wait for the exit signal*/
    sem_wait(&exitSignal);

    /*Cancel and join threads before exiting*/
    pthread_cancel(owner_thread);
    pthread_join(owner_thread, NULL);
    pthread_cancel(automobile_attendant_thread);
    pthread_join(automobile_attendant_thread, NULL);
    pthread_cancel(pickup_attendant_thread);
    pthread_join(pickup_attendant_thread, NULL);

    /* Destroy semaphores*/
    sem_destroy(&newAutomobile);
    sem_destroy(&newPickup);
    sem_destroy(&inChargeforAutomobile);
    sem_destroy(&inChargeforPickup);
    sem_destroy(&exitSignal);

    /*Log that all parking spaces are full and the program is exiting*/
    len = snprintf(buffer, sizeof(buffer), "All parking spaces are full. Exiting the program.\n");
    write(STDOUT_FILENO, buffer, len);

    return 0;
}
