import java.util.Map;
import java.util.Iterator;

public class decryptor {
	private Map<Character, Map<Character, Character>> map;
	private String key;
	private String keystream = "";
	private String plain_text = "";
	private String cipher_text;
	/*Constructor for the decryptor class. Initializes the map, key, and cipher text to be decrypted.*/
	public decryptor(Map<Character, Map<Character, Character>> _map, String _key, String text) {
		this.map = _map;
		this.key = _key;
		this.cipher_text = text;
	}

	public void decrypt() {
		// do not edit this method
		generate_keystream();
		generate_plain_text();
	}
	
	private void generate_keystream() {
		for (int i = 0, len = cipher_text.length(); i < len; i++) {	/* Loop over the length of the cipher text to construct the keystream*/
			keystream += key.charAt(i % key.length());	/*Append the character from key at position i modulo the key's length*/
		}
	}
	
	private void generate_plain_text() {
		for (int i = 0; i < cipher_text.length(); i++) {		/*Iterate through each character of the cipher text*/
			char column = keystream.charAt(i);					/* Determine the column from the keystream character*/
			char cipherChar = cipher_text.charAt(i);			/*Current character to decrypt from cipher text*/

			for (Map.Entry<Character, Map<Character, Character>> entry : map.entrySet()) {	/*Iterate through each row entry in the Vigenère table*/
				Character row = entry.getKey();											/*The row character (key in the outer map)*/
				Map<Character, Character> columnMap = entry.getValue();				/* The inner map for this row.*/

				for (Character currentColumn : map.get(row).keySet()) {				/*Check each column in the current row to find the matching cipher character*/
					if (currentColumn.equals(column) && columnMap.get(currentColumn).equals(cipherChar)) {	/*If the column matches and the mapped character equals the cipher character,the plaintext character is found*/
						this.plain_text += row;			/*Append the row character to the plaintext*/
						break; 							/* Break the inner loop once the matching character is found*/
					}
				}
			}
		}
	}

	public String get_keystream() {		/*Getter for the keystream*/
		return this.keystream;
	}
	
	public String get_plain_text() {	/*Getter for the decrypted plaintext*/
		return this.plain_text;
	}
}
