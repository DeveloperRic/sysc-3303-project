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
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Map.Entry;
import util.Printer;

public class InputParser {
	
	private String fileDirectory = System.getProperty("user.dir");
	private HashMap<String,Task> tasks = new HashMap<String,Task>();
	private ArrayList<LocalTime> timeRequests = null;
	
	public InputParser(String inputFileDestination) {
		fileDirectory += inputFileDestination;
		
		try {
			getInputs();
			storeTimeRequests();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//reads from the input file and calls parseAdd on each line 
	public void getInputs() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileDirectory));
		String ln;
		int lineNumber = 0;
		
		//Validate the input types
		while ((ln = in.readLine()) != null) {
			
			lineNumber++;
			if(validateInputFormat(ln,lineNumber) && validateInputLogic(ln,lineNumber)) {
				parseAdd(ln);
			}
			else {
				continue;
			}
		}
		in.close();
	}
	
	public boolean validateInputFormat(String input, int lineNumber) {
		String[] inputs = input.split(" ");
		
		//Validate Input Size
		if(inputs.length < 4) {
			Printer.print(inputs.length);
			Printer.print("Input Line " + lineNumber + " has less than 4 arguments. Skipping Input...");
			return false;
		}
		
		//Validate Date Type
		try {
			LocalTime.parse(inputs[0]);
		} catch (DateTimeParseException e) {
			Printer.print("Input Line " + lineNumber + " has an invalid Date time format. Skipping Input...");
			return false;
		}
		
		//Validate Start Floor Type
		try {
			Integer.parseInt(inputs[1]);
		} catch (NumberFormatException e) {
			Printer.print("Input Line " + lineNumber + " has an invalid Start floor format. Skipping Input...");
			return false;
		}
		
		//Validate Direction Type
		if( !(inputs[2].toLowerCase().contains("up")) && !(inputs[2].toLowerCase().contains("down")) ) {
			Printer.print("Input Line " + lineNumber + " has an invalid Direction format. Skipping Input...");
			return false;
		}

		//Validate Destination Floor Type
		try {
			Integer.parseInt(inputs[3]);
		} catch (NumberFormatException e) {
			Printer.print("Input Line " + lineNumber + " has an invalid Destination floor format. Skipping Input...");
			return false;
		}
		
		return true;
	}
	
	public boolean validateInputLogic(String input, int lineNumber) {
		String[] inputs = input.split(" ");
		int startFloor = Integer.parseInt(inputs[1]);
		int destFloor = Integer.parseInt(inputs[3]);
		boolean directionUp = (inputs[2].toLowerCase().contains("up")) ? true : false ;
		
		//Compare if valid start floor and end floor if going up
		if(directionUp) {
			if(startFloor - destFloor >= 0) {
				Printer.print("Input Line " + lineNumber + " is attempting to go UP from floor " + startFloor + " to floor " + destFloor
										+ ". Invalid Logic. Skipping Input...");
				return false;
			} 
		}
		else {
			if(startFloor - destFloor <= 0) {
				Printer.print("Input Line " + lineNumber + " is attempting to go DOWN from floor " + startFloor + " to floor " + destFloor
						+ ". Invalid Logic. Skipping Input...");
				return false;				
			}
		}
		
		return true;
	}
	
	public void parseAdd(String input) {
		String[] inputs = input.split(" ");
		
		try {
			Task task = new Task(inputs[0], inputs[1], inputs[2]);
			tasks.put(LocalTime.parse(inputs[0]).toString(),task);
		} catch (Exception e) {
			Printer.print("Invalid Input: " + e);
		}	
	}
	
	public HashMap<String,Task> getTasks() {
		return tasks;
	}
	
	public void printTasks() {
		Set<Entry<String,Task>> set = tasks.entrySet();
		Iterator<Entry<String, Task>> i = set.iterator();
		int index = 1;
		
		while(i.hasNext()) {
			Entry<String,Task> me = (Entry<String, Task>)i.next();
			System.out.print("Task " + index + ". KEY : " + me.getKey() + " -> ");
			Printer.print("VALUE : " + me.getValue());
			index++;
		}
		
		Printer.print();
	}
	
	public void storeTimeRequests() {
		Set<Entry<String,Task>> set = tasks.entrySet();
		Iterator<Entry<String, Task>> i = set.iterator();
		timeRequests = new ArrayList<LocalTime>();
		
		while(i.hasNext()) {
			Entry<String,Task> me = (Entry<String, Task>)i.next();
			timeRequests.add(me.getValue().getRequestTime());
		}
	}
	
	public void printTimeRequests() {
		for(LocalTime time : timeRequests) {
			Printer.print(time.toString());
		}
	}
	
	public ArrayList<LocalTime> getTimeRequests(){
		return timeRequests;
	}
	
}
