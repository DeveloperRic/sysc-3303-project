/** InputParser.java
 * The InputParser class reads an Input text file, validates the
 * format and logic and stores the tasks to be extracted and simulated.
 * @author Ralton Vaz
 *
 */

package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class takes the input file and takes each line and saves it
 * as an element in the request array list so the floor subsystem can
 * use it.
 * 
 */
public class InputParser {
	
	private String fileDirectory = System.getProperty("user.dir");	//Start the address from the project
	public ArrayList<String> requests = new ArrayList<>();
	
	public InputParser(String inputFileDestination) {
		fileDirectory += inputFileDestination;
		
		try {
			getInputs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Goes through each line in the input file until it reaches the end.
	 * 
	 */
	public void getInputs() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileDirectory));
		String ln;	
		//Validate the input types
		while ((ln = in.readLine()) != null) {
			parseAdd(ln);
		}
		in.close();
	}
	
	/**
	 * Saves each line in the requests array list.
	 * 
	 * @param input	The line from the input file to be saved as an element.
	 * 
	 */
	public void parseAdd(String input) {
		requests.add(input);
	}
}
