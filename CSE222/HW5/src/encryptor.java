import java.util.Map;

public class encryptor {
	private Map<Character, Map<Character, Character>> map;
	private String key;
	private String keystream = "";
	private String plain_text;
	private String cipher_text = "";
	
	public encryptor(Map<Character, Map<Character, Character>> _map, String _key, String text) {
		this.map = _map;			/*Set the table map*/
		this.key = _key;			/*Set the key for encryption*/
		this.plain_text = text;		/*Set the plaintext to be encrypted*/
	}
	
	public void encrypt() {
		// do not edit this method
		generate_keystream();
		generate_cipher_text();
	}
	
	private void generate_keystream() {
		for (int i = 0, len = plain_text.length(); i < len; i++) {	/*Loop over the plaintext length to construct the keystream*/
			keystream += key.charAt(i % key.length());			/*Append the character from the key, cycling through the key as needed*/
		}
	}
	
	private void generate_cipher_text() {
		for (int i = 0; i < plain_text.length(); i++) {		/*Loop through each character of the plaintext*/
			char row = plain_text.charAt(i);				/*Character from plaintext determines the row in the cipher table*/
			char column = keystream.charAt(i);					/*Character from keystream determines the column*/
			this.cipher_text += map.get(row).get(column);		/*Append the character from the intersection of row and column in the cipher table to the ciphertext*/
		}
	}

	public String get_keystream() {			/*Returns the generated keystream used for encryption*/
		return this.keystream;
	}
	
	public String get_cipher_text() {		/*Returns the ciphertext generated from the encryption process.*/
		return this.cipher_text;
	}
}
