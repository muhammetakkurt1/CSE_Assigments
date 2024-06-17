import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;

public class alphabet {
	private Set<Character> english_alphabet = new LinkedHashSet<Character>();
	private Map<Character, Map<Character, Character>> map = new HashMap<Character,  Map<Character, Character>>();
	
	public alphabet() {
		// do not edit this method
		fill_english_alphabet();
		fill_map();
	}
	
	private void fill_english_alphabet() {
		// do not edit this method
		for(char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
		    english_alphabet.add(c);
		}
	}
	
	private void fill_map() {
		Iterator<Character> iterRow = english_alphabet.iterator();	/*Iterator to traverse through the characters in the english_alphabet set*/
		int charNum = english_alphabet.size();	/*Store the number of characters in the alphabet, which determines the number of rows and columns in the map*/
		int rowShifter = 0;		/*Initialize shift, which determines how many positions the row should be shifted*/

		while (iterRow.hasNext()) {					/*Iterate over each character in the alphabet, using each as a starting row header*/
			char rowStart = iterRow.next();			/*Current character to start the row*/
			Map<Character, Character> innerMap = new HashMap<>();	/*Create a map for the current row*/

			Iterator<Character> iterCol = english_alphabet.iterator();	/*Initialize another iterator to set up columns for the current row*/
			for (int i = 0; i < rowShifter; i++) {	/*Shift the column iterator to the correct start position for this row*/
				iterCol.next();
			}

			for (int j = 0; j < charNum; j++) {		/*Fill the inner map for the current row with shifted characters*/
				char colChar;
				if (iterCol.hasNext()) {			/*Get the next character for the column*/
					colChar = iterCol.next();
				} else {							/*Reset the column iterator to the beginning of the alphabet if the end is reached*/
					iterCol = english_alphabet.iterator();
					colChar = iterCol.next();
				}
				innerMap.put(english_alphabet.toArray(new Character[0])[j], colChar);	/*Map the column header  to the shifted character*/
			}

			map.put(rowStart, innerMap);		/*Put the completed row into the main map with the row header as the key*/
			rowShifter++;					/*Increase the shift for the next row*/
		}
	}

	public void print_map() {
		// do not edit this method
		System.out.println("*** Viegenere Cipher ***\n\n");
		System.out.println("    " + english_alphabet);
		System.out.print("    ------------------------------------------------------------------------------");
		for(Character k: map.keySet()) {
			System.out.print("\n" + k + " | ");
			System.out.print(map.get(k).values());
		}
		System.out.println("\n");
		
	}
	/*It causes a warning because it is not type-safe at compile time. This should be the return value for no warning: Map<Character,Map<Character,Character>> */
	public Map get_map() {		/*Return the complete Vigenère cipher table*/
		return this.map;
	}
}