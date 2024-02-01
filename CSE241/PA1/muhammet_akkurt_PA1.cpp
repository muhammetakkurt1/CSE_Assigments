#include <iostream>
#include <cstdlib>
#include <ctime>
#include <cstring>

using namespace std;

void undefinedParam(int argc){		/* this function checks if the input parameter count is correct */

		if (argc != 3) {			
        cout << "E0" << endl;
        exit(1);
    	}
}

bool isUnique(string s) {	/* this function checks if the characters in a string are unique */
				
    for(int i=0; i<s.length(); i++) {
        for(int j=i+1; j<s.length(); j++) {
            if(s[i] == s[j]) {				/* if a digit is similar, false is returned */

                return false;
            }
        }
    }
    if (s[0] == '0'){			/* return false if number starts with 0 or only 0 is written */
    	return false;
	}
    return true;				/*true is returned if the number consists of unique digits*/
}

int main(int argc, char* argv[]) {
   
    undefinedParam(argc);			/* the number of arguments is checked */
    
	if (string(argv[1]) != "-r" && string(argv[1]) != "-u") {	/* check if the first input parameter is valid */
    cout << "E0" << endl;
    exit(1);
	}
	
    int secretNumber[10];		/*define array to hold secret number*/
    int digitCount = 0;		/*define the data to hold the digits of the secret number*/

    
    if (string(argv[1]) == "-r") { /* generate a random number if the first input parameter is "-r" */
      	
        for (int i = 0; i <  strlen(argv[2]); i++) {		/* checking the correctness of the parameter entered for the random number */
	    	if (!isdigit(argv[2][i])){		
			cout << "E0" << endl;
    		exit(1);
			}
		}
		if(atoi(argv[2])<=0 || atoi(argv[2]) > 9){			/* check if the second input parameter is a valid positive integer between 1 and 9 */
          	cout << "E0" << endl;
            exit(1);
		}
    
        
        
        digitCount = atoi(argv[2]);				/*determining the number of digits of a random number*/
        
        srand(time(NULL));
        secretNumber[0] = rand() % 9 + 1; 	     	/*selecting a number other than 0 with the first digit*/	
        
        for (int i = 1; i < digitCount; i++) {		/* generating random numbers and assigning them to the secret number until the secret number consists of unique numbers */
            int newDigit = rand() % 10;
            
            for (int j = 0; j < i; j++) {
                if (secretNumber[j] == newDigit) {
                    newDigit = rand() % 10;
                    j = -1;
                }
            }
            secretNumber[i] = newDigit;			/*adding a random number as a digit to a number*/
        }
				
    } else if (string(argv[1]) == "-u") {			/* use the input parameter as the secret number if the first input parameter is "-u" */
        
        for (int i = 0; i <  strlen(argv[2]); i++) {		/* checking the correctness of the parameter entered for the random number */
	    	if (!isdigit(argv[2][i])){
			cout << "E0" << endl;
    		exit(1);
			}
		}
    	if (!isUnique(argv[2])) {					/* check if the second input parameter is a valid number with unique digits */
        	cout << "E0" << endl;
        	exit(1);
    	}
        
        string secretNumberString = string(argv[2]);	/*string definition and define number of digits*/
        digitCount = secretNumberString.length();
        
        for (int i = 0; i < digitCount; i++) {					/* Convert the string to an integer array */
            secretNumber[i] = secretNumberString[i] - '0';
        }
        
    } else {							/* Exit the program if the first input parameter is neither "-r" nor "-u" */
        cout << "E0" << endl;
        exit(1);
    }

	for (int turn = 1; turn <= 100; turn++) {			/* the game will run up to a maximum of 100 guesses */
    
    string guessString;			/* get user guess */
    cin >> guessString;
    
    	if (!isUnique(guessString)) {					/* check if the guess is a valid number with unique digits */
        	cout << "E0" << endl;
        	exit(1);
    	}
    	
    	for (int i = 0; i < guessString.length(); i++) {		/* check if the number entered by the user is a valid number */
	    	if (!isdigit(guessString[i])){
			cout << "E2" << endl;
    		exit(1);
			}
		}
			if(digitCount != guessString.length()){			/* Check the length of the number entered by the user. */
    		cout << "E1" << endl;
    		exit(1);
			}
    int guess[digitCount];			/*define array to save digits*/			
    	
		for (int i = 0; i < digitCount; i++) {		/* Parse the digits of the guessed number. */
        	guess[i] = guessString[i] - '0';
    	}
    
   
    int correctDigits = 0;				/* defining a variable to find the number of digits in the correct and incorrect positions */
    int misplacedDigits = 0;
    
    	for (int i = 0; i < digitCount; i++) {      /* the loop continues until the number of digits   */
        	if (guess[i] == secretNumber[i]) { 	/*the correct number is in the correct position*/
            	correctDigits++;
        	} else {									/* digit is correct but position is incorrect */
            	for (int j = 0; j < digitCount; j++) {		/* a loop to check all digits */
                	if (guess[i] == secretNumber[j]) {		/* the digit is not in the correct position, but the next digit is in the index */
                    	misplacedDigits++;
                    	break;
                	}
            	}
        	}
    	}
    	
		if (correctDigits == digitCount) {		 /* Check if the user has correctly guessed the number */
        	cout << "FOUND " << turn << endl;
        	break;
    	}
    	
    	if (turn == 100) {				/* Check if the user has used up all 100 turns */
        	cout << "FAILED" << endl;
        	break;
    	}
    
    cout << correctDigits << " " << misplacedDigits << endl;	/* Output the number of correct and misplaced digits */
    
    }
	return 0;
}

