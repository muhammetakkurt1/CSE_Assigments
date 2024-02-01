#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

class ImageEditor {      /* ImageEditor class*/
public:
    ImageEditor() {}		/* ImageEditor class constructor */

    void start() {				/* the method that starts the editor */
        while (true) {					/* the loop continues until the user logs out */
            int choice = main_menu();		/* show main menu*/
            switch (choice) {		/* methods are called based on choices */
                case 0:
                    return;
                case 1:
                    open_menu();
                    break;
                case 2:
                    save_menu();
                    break;
                case 3:
                    scripts_menu();
                    break;
                default:
                	break;
            }
        }
    }

    
    int get_width() const {  /* getter method that returns width */
        return width;
    }

    void set_width(int w) {	 /* setter method that sets the width value */
        width = w;
    }

    int get_height() const {	/* getter method returning height value */
        return height;
    }

    void set_height(int h) {   /* setter method that sets the height value */
        height = h;
    }
    
    int get_max_color() const {		/* getter method that returns the maximum color value */
        return max_color;
    }

    void set_max_color(int m) {   /* setter method that sets the maximum color value*/
        max_color = m;
    }

    vector<int> get_image_data() const {   /* getter method returning image data */
        return image_data;
    }

    void set_image_data(vector<int> data) {    /* setter method that sets image data */
        image_data = data;
    }

private:
    int main_menu() {						/* method to show main menu */
        int choice;
        cout << "MAIN MENU\n";
        cout << "0 - Exit\n";
        cout << "1 - Open An Image(D)\n";
        cout << "2 - Save Image Data(D)\n";
        cout << "3 - Scripts(D)\n";
        cin >> choice;

        return choice;
    }

    void open_menu() {			/*Method to open the image popup menu */
        int choice = -1;
        while (choice != 0) {   /* menu is shown in terminal unless user enters 0 */
            cout << "OPEN AN IMAGE MENU\n";
            cout << "0 - UP\n";
            cout << "1 - Enter The Name Of The Image File\n";
            cin >> choice;
            switch (choice) {  /* the action is taken according to the value entered by the user */
                case 0:
                    break;
                case 1:
                    open_image();
                    break;
                default:
                    break;
            }
        }
    }

    void open_image() {    
        string filename;								/* a variable holding the filename  */
        cout << "Enter the name of the image file: ";
        cin >> filename;									/* get filename from user */
        ifstream infile(filename);  					/*	creates an input file stream to read the file */
        if (!infile.is_open()) {							/*	checks if the file is opened, if the file cannot be opened, an error message is printed and the function exits */
            cerr << "Error: Unable to open file " << filename << endl;
            return;
        }
        string line;				/*	a string variable that holds the lines read from the file */
        infile >> line;				/* a line is read from the file and assigned to the line variable */
        if (line != "P3") {			/* checks if the file format is P3 */
            cerr << "Error: Unsupported file format" << endl;		
            return;
        }
        int w, h, m;				/* variables for the width (w), height (h) and maximum color value (m) of the image */
        infile >> w >> h >> m;		/* read and assign width , height and maximum color value from file */
        set_width(w);				/*sets the width of the image */
        set_height(h);				/*sets the  height  of the image */
        set_max_color(m);			/*sets the maximum color value of the image */
        vector<int> data(get_height() * get_width() * 3);			/*a new vector is created and size adjusted to hold the image data*/
        for (int i = 0; i < get_height() * get_width() * 3; ++i) {		/* it reads the components of each pixel from the file and places them in the data vector */
            infile >> data[i];	
        }
        set_image_data(data);		/*data is set to the image_data vector */
        infile.close();			/* the file stream is turned off*/
    }

    void save_menu() {		/*Method to open the save image data popup menu */
        int choice = -1;
        while (choice != 0) {		 /* menu is shown in terminal unless user enters 0 */
            cout << "SAVE IMAGE DATA MENU\n";
            cout << "0 - UP\n";
            cout << "1 - Enter A File Name\n";
            cin >> choice;
            switch (choice) {				/* the action is taken according to the value entered by the user */
            	case 0:
                	break;
            	case 1:
                	save_image_data();
                	break;
            	default:
                	break;
        	}
    	}
	}

	void save_image_data() {
    	string filename;					/* a variable holding the filename  */
    	cout << "Enter a file name: ";
    	cin >> filename;					/* get filename from user */
    	ofstream outfile(filename);			/*	the file is opened and created with the given filename */
    	if (!outfile.is_open()) {			/*  if the file cannot be opened, an error message is given and the function is exited */
        	cerr << "Error: Unable to create file " << filename << endl;
        	return;
    	}
    	outfile << "P3\n";													/*	the information that the file format is P3 is written to the file */
    	outfile << get_width() << " " << get_height() << "\n";		/*	the width and height of the image are written to the file */
    	outfile << get_max_color() << "\n";							/* the maximum color value of the picture is written to the file */
    	for (int i = 0; i < get_height() * get_width() * 3; ++i) {			/*loop prints the RGB values of all pixels of the image to the file*/
        	outfile << get_image_data()[i] << " ";		/* a space is left after each data */
        	if ((i + 1) % 3 == 0) {				/* a tab space is left after each pixel data*/	
            	outfile << "\t";
        	}
        	if ((i + 1) % (get_width() * 3) == 0) {		/* after the pixel data as much as the picture width, a bottom line is passed */
            	outfile << "\n";
        	}	
    	}
    	outfile.close();     	/* the file stream is turned off*/
	}

	void scripts_menu() {				/*Method to open the convert script popup menu */
    	int choice = -1;
    	while (choice != 0) {					/* menu is shown in terminal unless user enters 0 */
        	cout << "CONVERT TO GRAYSCALE MENU\n";
        	cout << "0 - Exit\n";
        	cout << "1 - Convert To Grayscale\n";
        	cin >> choice;
        	switch (choice) {					/* the action is taken according to the value entered by the user */
            	case 0:
                	break;
           	 	case 1:
                	convert_to_grayscale();
                	break;
            	default:
                	break;
        	}
    	}
	}

	void convert_to_grayscale() {
	    double c_r, c_g, c_b;					/*variables to hold coefficients for red, green and blue channels*/
	    bool valid = false;			/* a variable to validate to get valid coefficients */
	    while (!valid) {				/* loop until valid coefficients are received */
	        cout << "Enter coefficients for RED, GREEN, and BLUE channels: ";
	        cin >> c_r >> c_g >> c_b;
	
	        if (c_r < 0 || c_r >= 1 || c_g < 0 || c_g >= 1 || c_b < 0 || c_b >= 1) {		/*  check if coefficients are between 0 and 1 and print error message if not */
	            cout << "Invalid coefficients. Coefficients should be between 0 and 1. Try again.\n";
	        } else {								/*	if the coefficients are valid make the change that ends the loop */
	            valid = true;
	        }
	    }
	    
	    vector<int> data = get_image_data();				/* store current pixel data in a temporary vector*/
	    for (int i = 0; i < get_height() * get_width() * 3; i += 3) {
	        int gray = c_r * data[i] + c_g * data[i + 1] + c_b * data[i + 2];	/* calculate the gray scale of each pixel using the coefficients */
	        if (gray > 255) {		/* limit grayscale from 0 to 255 */
	            gray = 255;
	        }
	        data[i] = data[i + 1] = data[i + 2] = gray;		/* assigning a gray scale to the red, green, and blue channels of the pixel */
	    }
	    set_image_data(data);		/*  assign updated pixel data to class variable */
	}

	int width = 0;    /* class variable defining width*/
	int height = 0;   /* class variable defining height*/
	int max_color = 0;        /* class variable defining maximum color value*/ 
	vector<int> image_data;  /* class variable defining the pixel values of the image to vector */

};

int main() {
	ImageEditor editor;		/*	An object of the ImageEditor class is created */
	editor.start();    	/* the menu is called with the start function */
	return 0;
}

