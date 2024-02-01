#include <iostream>
#include <vector>
#include <fstream>
#include <string>
#include <algorithm>

using namespace std;

struct Pixel {								/*pixel structure to store red, green, and blue values of a single pixel*/
    unsigned char red, green, blue;
};

class ppmImage {							/*ppmImage class for representing and manipulating PPM images*/
public:

    ppmImage(const string &filename) {		/* constructor that reads an image from a file */
        readFromFile(filename);
    }

    ppmImage(int width, int height) : width(width), height(height), maxColorValue(255) {	/*constructor that creates a new blank image of the specified width and height*/
        imageData.resize(width * height, { 255, 255, 255 });
    }

    ppmImage() : width(0), height(0), maxColorValue(255) {}			/*default constructor that creates an empty image with a width and height of 0*/

    int getWidth() const { 				/*getter function that returns the width of the image*/
		return width; 
	}
    int getHeight() const { 			/*getter function that returns the height of the image*/
		return height; 
	}
    int getMaxColorValue() const { 		/*getter function that returns the maximum color value of the image*/
		return maxColorValue; 
	}
    void setWidth(int width) { 			/*setter function that sets the width of the image*/
		this->width = width; 
	}
    void setHeight(int height) { 		/*setter function that sets the height of the image*/
		this->height = height; 
	}
    void setMaxColorValue(int maxColorValue) { 		/*setter function that sets the maximum color value of the image*/
		this->maxColorValue = maxColorValue; 
	}
	
	unsigned char getPixelComponent(int index, int color) const {					 /* retrieves a specific color component (red, green, or blue) of the pixel at the given index*/
        if (index < 0 || index >= width * height || color < 1 || color > 3) {			/*check if the index and color parameters are valid*/
            return 0;
        }

        const Pixel &pixel = imageData[index];										/*retrieve the pixel at the given index*/
        return color == 1 ? pixel.red : (color == 2 ? pixel.green : pixel.blue);	 /*return the specified color component*/
    }

    void setPixelComponent(int index, int color, unsigned char value) {			/*sets a specific color component (red, green, or blue) of the pixel at the given index to the given value */
        if (index < 0 || index >= width * height || color < 1 || color > 3) {		/*check if the index and color parameters are valid */
            return;
        }

        Pixel &pixel = imageData[index];		/*retrieve the pixel at the given index*/
        if (color == 1) {				/*set the specified color component to the new value*/
            pixel.red = value;
        } else if (color == 2) {
        	pixel.green = value;
		} else {
			pixel.blue = value;
		}
	}
	
   bool saveToFile(const string &filename) const {		/*this function saves the image data to a file in PPM format */
    ofstream outFile(filename);				/*open a new file stream for writing*/
    if (!outFile) {						/*check if the file stream is open*/		
        return false;
    }

    outFile << "P3\n" << width << " " << height << "\n" << maxColorValue << "\n";		/*	write the PPM header information to the file*/
    int size = imageData.size();
    int counter = 0;					 
    for (int i = 0; i < size; i++) {			/*iterate through the image data and write each pixel to the file*/
        Pixel pixel = imageData[i];
        outFile << static_cast<int>(pixel.red) << " ";
        outFile << static_cast<int>(pixel.green) << " ";
        outFile << static_cast<int>(pixel.blue)  << " ";
         
        counter++;
        if (counter % width == 0) {			/*add a newline character after every row of pixels*/
            outFile << "\n";
        } else {
            outFile << "\t";
        }
    }

    outFile.close();		/*close the file stream*/
    return true;
	}


	bool readFromFile(const string &filename) {		/*reads image data from a file in PPM format*/
	    ifstream inFile(filename);					/*open a new file stream for reading*/
	    if (!inFile) {							/*check if the file stream is open*/
	        return false;
	    }
	
	    string header;			/*read the PPM header information from the file*/
	    inFile >> header;
	    if (header != "P3") {		/*If the format is not appropriate, the file is closed and an error is returned*/
	        inFile.close();
	        return false;
	    }
	
	    inFile >> width >> height >> maxColorValue;		/*read the image dimensions and maximum color value*/
	
	    imageData.resize(width * height);					/*Resize the image data vector*/
	  	for (int i = 0; i < width * height; i++) {			/*iterate through the file and read each pixel's color values*/
	        int red, green, blue;
	        inFile >> red >> green >> blue;
	
	        Pixel &pixel = imageData[i];					/* store the color values in the corresponding pixel*/
	        pixel.red = static_cast<unsigned char>(red);
	        pixel.green = static_cast<unsigned char>(green);
	        pixel.blue = static_cast<unsigned char>(blue);
	    }
	
	    inFile.close();							/*close the file stream*/
	    return true;
	}

	ppmImage operator+(const ppmImage &other) const {				 /*overload the + operator to add two ppmImage objects pixel-wise*/
    	if (width != other.width || height != other.height) {		/*checking if two files are of similar size*/
        	return ppmImage(); 										/*return empty image*/
    	}

    	ppmImage result(width, height);																		/*create a new ppmImage object with the same dimensions as the input images*/
    	for (int i = 0; i < width * height; i++) {																/*iterate through each pixel in the imageData vector*/
	        
			result.imageData[i].red = min(maxColorValue, imageData[i].red + other.imageData[i].red);			/*Add the color components of the two images pixel-wise*/
	        result.imageData[i].green = min(maxColorValue, imageData[i].green + other.imageData[i].green);		/*Comparing and assigning the smallest so that the result is no greater than the maximum value (255) */
	        result.imageData[i].blue = min(maxColorValue, imageData[i].blue + other.imageData[i].blue);
	    }

    return result;							/*return of the object*/
	}

	ppmImage operator-(const ppmImage &other) const {					/*overload the - operator to subtract one ppmImage object from another pixel-wise*/
	    if (width != other.width || height != other.height) {			/*checking if two files are of similar size*/
	        return ppmImage(); 												/*return empty image*/			
	    }

	    ppmImage result(width, height);									/*create a new ppmImage object with the same dimensions as the input images*/
	    for (int i = 0; i < width * height; i++) {						/*iterate through each pixel in the imageData vector*/
	        result.imageData[i].red = max(0, static_cast<int>(imageData[i].red) - static_cast<int>(other.imageData[i].red));				/*subtract the color components of the second image from the first image pixel-wise*/
	        result.imageData[i].green = max(0, static_cast<int>(imageData[i].green) - static_cast<int>(other.imageData[i].green));			/*determining and assigning the largest so that the result is not less than 0*/
	        result.imageData[i].blue = max(0, static_cast<int>(imageData[i].blue) - static_cast<int>(other.imageData[i].blue));
	    }
	
	    return result;                         /*return of the object*/
	}
	friend ostream &operator<<(ostream &os, const ppmImage &image) {								/*overload the << operator to output a ppmImage object to an ostream in P3 PPM format*/
	    os << "P3\n" << image.width << " " << image.height << "\n" << image.maxColorValue << "\n";		/*output the P3 PPM header*/
	    for (const Pixel &pixel : image.imageData) {										/*output the pixel data*/
	        os << pixel.red << " " << pixel.green << " " << pixel.blue << "\n";
	    }
	
	    return os;
	}
	unsigned char &operator()(int row, int col, int color) {								/* overload the () operator to access a specific color component of a pixel in the image (non-const version)*/
		static unsigned char temp = 0;												/*	Create a static temporary variable to return a reference to when the given row, col, or color are out of bounds*/
	    if (row < 0 || row >= height || col < 0 || col >= width || color < 1 || color > 3) {	/*	Check if the given row, col, or color are out of bounds. If so, return a reference to the temporary variable*/
	        return temp;
	    }
	
	    Pixel &pixel = imageData[row * width + col];								/* access the corresponding pixel in the imageData vector*/
	    return color == 1 ? pixel.red : (color == 2 ? pixel.green : pixel.blue);	/* return a reference to the requested color component */
	}
	unsigned char operator()(int row, int col, int color) const {					/*overload the () operator to access a specific color component of a pixel in the image (const version)*/
	    if (row < 0 || row >= height || col < 0 || col >= width || color < 1 || color > 3) {		/*if row, col or color values are out of bounds then only 0 is returned*/
	        return 0;
	    }
	
	    const Pixel &pixel = imageData[row * width + col];							/* access the corresponding pixel in the imageData vector*/
	    return color == 1 ? pixel.red : (color == 2 ? pixel.green : pixel.blue);	/* return a reference to the requested color component */	
	}
	
	
	private:
		int width;                 /* the width of the image (in pixels)*/
		int height;                /* image height (in pixels)*/
		int maxColorValue;         /* maximum color value of pixels in the image (255)*/	
		vector<Pixel> imageData;	/*A vector containing the RGB values of the pixels in the image*/
};
	
	
	int read_ppm(const string &source_ppm_file_name, ppmImage &destination_object) {			/*helper function to read PPM image from file*/
		if (destination_object.readFromFile(source_ppm_file_name)) {    					/*	returns 1 (successful) or 0 (failed) based on success */
        	return 1;
    	} 
		else{
        	return 0;
    	}				
	}
	
	int write_ppm(const string &destination_ppm_file_name, const ppmImage &source_object) {			/*helper function to write PPM image to file*/	
		if (source_object.saveToFile(destination_ppm_file_name)) {								/*	returns 1 (successful) or 0 (failed) based on success */
	        return 1;
	    } 
		else {
	        return 0;
	    }				
	}
	int test_addition(const string &filename_image1, const string &filename_image2, const string &filename_image3) {		/*collects the pixels of two PPM images, creates a new image and writes it to the file*/	
		ppmImage image1(filename_image1);
		ppmImage image2(filename_image2);
		ppmImage result = image1 + image2;										/*	two file pixels are adding and assigned to the new object	*/
		return write_ppm(filename_image3, result);								/*	the function is called to print the result object to the newly created file */
	}
	int test_subtraction(const string &filename_image1, const string &filename_image2, const string &filename_image3) {			/*	creates a new image by subtracting the pixels of the two PPM images and writes it to the file */
		ppmImage image1(filename_image1);		
		ppmImage image2(filename_image2);
		ppmImage result = image1 - image2;				/*	two file pixels are substract and assigned to the new object	*/
		return write_ppm(filename_image3, result);     /*	the function is called to print the result object to the newly created file */
	}
	int test_print(const string &filename_image1) {						/* PPM prints the image to the screen */
	    ppmImage image(filename_image1);
	    
		if (image.getWidth() == 0 || image.getHeight() == 0) {			/*	return 0 if image read failed	*/
	        return 0; 
	    }
	
	    cout << "P3\n" << image.getWidth() << " " << image.getHeight() << "\n" << image.getMaxColorValue() << "\n";		/*Print header information of PPM image file*/
	
	    int width = image.getWidth();
	    int height = image.getHeight();
	    for (int row = 0; row < height; ++row) {			/*	Loop the pixels of the image as rows and columns*/
	        for (int col = 0; col < width; ++col) {
	            cout << static_cast<int>(image(row, col, 1)) << " ";		/*Print the red components of the pixel*/
	            cout << static_cast<int>(image(row, col, 2)) << " ";		/*Print the green components of the pixel*/	
	            cout << static_cast<int>(image(row, col, 3)) << " ";		/*Print the blue components of the pixel*/
	        }
	        cout << "\n";
	    }
	
	    return 1;
	}
	int swap_channels(ppmImage &image_object_to_be_modified, int swap_choice) {		/*PPM changes the color channels of the image object */
		int height = image_object_to_be_modified.getHeight();						/* Get the height of the image to be modified */
		int width = image_object_to_be_modified.getWidth();						/* Get the width of the image to be modified */
		for (int row = 0; row < height; row++) {		 						/*loop the pixels of the image as rows and columns*/
		    for (int col = 0; col < width; col++) {								/*change color channels based on swap_choice value */
		        if (swap_choice == 1) {
		            swap(image_object_to_be_modified(row, col, 1), image_object_to_be_modified(row, col, 2));		/*swap red and green channels for the current pixel*/
		        } else if (swap_choice == 2) {
		            swap(image_object_to_be_modified(row, col, 1), image_object_to_be_modified(row, col, 3));		/*swap red and blue channels for the current pixel*/
		        } else if (swap_choice == 3) {
		            swap(image_object_to_be_modified(row, col, 2), image_object_to_be_modified(row, col, 3));		/*swap green and blue channels for the current pixel*/
		        }
		    }
		}
	
	return 1;
	}
	
	ppmImage single_color(const ppmImage& source, int color_choice) {			/*creates a monochrome image from a PPM image object */
	    int width = source.getWidth();							/* Get the width of the source image */
	    int height = source.getHeight();						/* Get the height of the source image */
	    ppmImage result(width, height);						/* Create a new ppmImage object with the same dimensions as the source image */
	
	    for (int row = 0; row < height; ++row) {					/*loop the pixels of the image as rows and columns*/
	        for (int col = 0; col < width; ++col) {				/*adjust color components based on color_choice value */
	            if (color_choice == 1) {		
	                result(row, col, 1) = source(row, col, 1);		/*keep red component and reset other components*/
	                result(row, col, 2) = 0;
	                result(row, col, 3) = 0;
	            } else if (color_choice == 2) {
	                result(row, col, 1) = 0;
	                result(row, col, 2) = source(row, col, 2);		/*keep green component and reset other components*/
	                result(row, col, 3) = 0;
	            } else if (color_choice == 3) {
	                result(row, col, 1) = 0;
	                result(row, col, 2) = 0;
	                result(row, col, 3) = source(row, col, 3);		/*keep blue component and reset other components*/
	            }
	        }
	    }
	
	    return result;
	}
	
	
int main(int argc, char **argv) {
		
	if (argc < 3) {										/*printing error on insufficient argument input*/
		cerr << "Not enough arguments provided" << endl;
		return 1;
	}
	
	int choice = stoi(argv[1]);							/*get user's choice for operation to perform*/
	string ppm_file_name1 = argv[2];					/*get the first PPM file name*/
	string ppm_file_name2 = (argc > 3) ? argv[3] : "";		 /*get the second PPM file name if available*/
	string ppm_file_name3 = (argc > 4) ? argv[4] : "";		/*get the third PPM file name if available*/
	
	switch (choice) {								/*perform the requested operation based on the user's choice*/
	    case 1:																				/*	addition*/
	        if (argc == 5) {															/*calling the required functions by entering the desired number of arguments*/
	        	test_addition(ppm_file_name1, ppm_file_name2, ppm_file_name3);
	           
	        }
	       	else{																			/*error if incorrect number of arguments is entered*/
	       		 cerr << "Invalid arguments provided for addition" << endl;	
	            return 1;
			   }
	        break;
	    case 2:																			/*subtraction*/
	        if (argc == 5) {																/*calling the required functions by entering the desired number of arguments*/
	        	test_subtraction(ppm_file_name1, ppm_file_name2, ppm_file_name3);
	        }
	        else{																	/*error if incorrect number of arguments is entered*/
	       		cerr << "Invalid arguments provided for subtraction" << endl;
	            return 1;
			}
	        break;
	    case 3:																/*swap red and blue channels*/
	        if (argc == 4) {													/*calling the required functions by entering the desired number of arguments*/
	        	ppmImage image(ppm_file_name1);
				swap_channels(image, 2);
				write_ppm(ppm_file_name2, image);
	            
	        }	
	        else{															/*error if incorrect number of arguments is entered*/
	        	cerr << "Invalid arguments provided for swapping red and blue channels" << endl;
	        	return 1;
			}
			break;
		case 4:															/*swap green and blue channels*/	
			if (argc == 4) {												/*calling the required functions by entering the desired number of arguments*/
				ppmImage image(ppm_file_name1);
				swap_channels(image, 3);
				write_ppm(ppm_file_name2, image);
			}
			else{														/*error if incorrect number of arguments is entered*/
				cerr << "Invalid arguments provided for swapping green and blue channels" << endl;
				return 1;
			}
			break;
		case 5:														/*preserve red channel*/	
			if (argc == 4) {														/*calling the required functions by entering the desired number of arguments*/
				ppmImage image(ppm_file_name1);
				ppmImage result = single_color(image, 1);
				write_ppm(ppm_file_name2, result);	
			}
			else{																	/*error if incorrect number of arguments is entered*/
				cerr << "Invalid arguments provided for preserving the red channel" << endl;
				return 1;
			}
			break;
		case 6:														/*preserve green channel*/
			if (argc == 4) {										/*calling the required functions by entering the desired number of arguments*/
				ppmImage image(ppm_file_name1);
				ppmImage result = single_color(image, 2);
				write_ppm(ppm_file_name2, result);
			}
			else{													/*error if incorrect number of arguments is entered*/
				cerr << "Invalid arguments provided for preserving the green channel" << endl;
				return 1;
			}
			break;
		case 7:													/*preserve blue channel*/
			if (argc == 4) {								/*calling the required functions by entering the desired number of arguments*/
				ppmImage image(ppm_file_name1);
				ppmImage result = single_color(image, 3);
				write_ppm(ppm_file_name2, result);
			
			}
			else{												/*error if incorrect number of arguments is entered*/
				cerr << "Invalid arguments provided for preserving the blue channel" << endl;
				return 1;
			}
			break;
		case 8:														/*print PPM image to console*/
			if (argc == 3){										/*calling the required functions by entering the desired number of arguments*/
				test_print(ppm_file_name1);
			    }
			else{													/*error if incorrect number of arguments is entered*/
				cerr << "Invalid arguments to print to console" << endl;
				return 1;
			}
			break;
		default:
				cerr << "Invalid choice number" << endl;
				return 1;
	}
	return 0;
}
