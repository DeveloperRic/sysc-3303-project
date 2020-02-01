package main;
import java.io.*;
import java.util.*;

public class FloorSubsystem implements Runnable{

	/*static LinkedList<Task> floor1Up = new LinkedList<Task>();
	static LinkedList<Task> floor1Down = new LinkedList<Task>();
	static ArrayList<LinkedList<Task>> floor1 = new ArrayList<LinkedList<Task>>(
			Arrays.asList(floor1Up, floor1Down)); 
	
	static LinkedList<Task> floor2Up = new LinkedList<Task>();
	static LinkedList<Task> floor2Down = new LinkedList<Task>();
	static ArrayList<LinkedList<Task>> floor2 = new ArrayList<LinkedList<Task>>(
			Arrays.asList(floor2Up, floor2Down)); 
	
	static LinkedList<Task> floor3Up = new LinkedList<Task>();
	static LinkedList<Task> floor3Down = new LinkedList<Task>();
	static ArrayList<LinkedList<Task>> floor3 = new ArrayList<LinkedList<Task>>(
			Arrays.asList(floor3Up, floor3Down));  
	
	static LinkedList<Task> floor4Up = new LinkedList<Task>();
	static LinkedList<Task> floor4Down = new LinkedList<Task>();
	static ArrayList<LinkedList<Task>> floor4 = new ArrayList<LinkedList<Task>>(
			Arrays.asList(floor4Up, floor4Down));  
	
	static ArrayList<ArrayList<LinkedList<Task>>> taskMatrix = new ArrayList<ArrayList<LinkedList<Task>>>(
			Arrays.asList(floor1, floor2, floor3, floor4));*/
	
	static ArrayList<Task> tasks = new ArrayList<Task>();
	
	private Scheduler scheduler = null;
	
	public FloorSubsystem(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public static void main(String[] args) throws IOException {
		getInputs();
	}
	
	@Override
	public void run(){
		try {
			getInputs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i=0; i<tasks.size(); i++) {
			System.out.println("FLOOR SUBSYSTEM: Task " + i + " being sent to Scheduler : \n Task Information : " + tasks.get(i) + "\n");
			scheduler.put(tasks.get(i));
		}
		
		while(true) {
			System.out.println("FLOOR SUBSYSTEM: Floor RECEIVING confirmation message from Scheduler : \n " + (String)scheduler.get() + "\n");
		}
	}

	//reads from the input file and calls parseAdd on each line 
	private static void getInputs() throws IOException {
		//is this how u do relative paths in java
		String localDir = System.getProperty("user.dir");
		BufferedReader in = new BufferedReader(new FileReader(localDir + "\\src\\assets\\Inputs.txt"));
		String ln;
		while ((ln = in.readLine()) != null)
			parseAdd(ln);
		in.close();
	}
	
	//splits each string by whitespace and creates a Task object and puts it into the matrix
	private static void parseAdd(String ln) {      
		String[] inputs = ln.split(" ");
	
		try {
			Task task = new Task(inputs[0], inputs[1], inputs[2], inputs[3]);
			tasks.add(task);
			//taskMatrix.get(task.getStartFloor()-1).get(task.getDirection()).add(task);
		} catch (Exception e) {
			System.out.println("Invalid Input: " + e);
		}
	}
}
