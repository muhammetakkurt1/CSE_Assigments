public class preprocessor {
	private String initial_string;
	private String preprocessed_string;
		
	public preprocessor(String str) {
		this.initial_string = str;		/*Assign the original string to initial_string.*/
		this.preprocessed_string = "";	/*Initialize preprocessed_string as empty*/
	}

	public void preprocess() {
		// do not edit this method
		capitalize();
		clean();
	}
	
	private void capitalize() {
		String result = "";		/*Initialize an empty string to hold the result*/

		for (int i = 0; i < initial_string.length(); i++) {			/*Iterate through each character of the initial_string*/
			char currentChar = initial_string.charAt(i);  			/*Extract the current character*/
			if (currentChar == 'ı') {						/*Check if the current character is the lowercase 'ı'*/
				result += 'İ';  					/*If it's 'ı', manually convert it to uppercase 'İ'*/
			} else {
				result += Character.toUpperCase(currentChar);		/*For all other characters, convert them to uppercase using the standard method*/
			}
		}
		this.preprocessed_string = result;					/*Update the preprocessed_string with the newly formed uppercase string*/
	}

	private void clean() {
		this.preprocessed_string = preprocessed_string.replaceAll("[^A-Z]", "");		/*Replace all characters in the preprocessed_string that are not between A and Z*/
	}
	
	public String get_preprocessed_string() {	/*Return the final processed string*/
		return this.preprocessed_string;
	}
}