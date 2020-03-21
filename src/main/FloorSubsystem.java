package main;
import java.io.*;
import java.util.*;

import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import scheduler.SchedulerType;
import util.Printer;

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
	
	//Arraylist to hold the tasks
	public static ArrayList<Task> tasks = new ArrayList<Task>();
	
	public SchedulerType scheduler = null;
	
	public FloorSubsystem(SchedulerType scheduler) {
		this.scheduler = scheduler;
	}
	
	@Override
	public void run(){
		int taskNum = 0;
		
		Thread input = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					Integer[] arr = new Integer[2];
					Task t = getNextTask();
					Printer.print("FLOOR SUBSYSTEM: Task " + taskNum + " being sent to Scheduler : \n Task Information : " + t + "\n");
					arr[0] = t.getStartFloor();
					arr[1] = t.getDirection();
					
					scheduler.put(new FloorRequest() {
						@Override
						public Integer[] getRequest() {
							return arr;
						}
					});
					//Printer.print("FLOOR SUBSYSTEM: Floor RECEIVING confirmation message from Scheduler : \n " + (String)scheduler.get(null) + "\n");
				}
				
			}
		});
		
		Thread output = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					Printer.print("FLOOR SUBSYSTEM: Floor RECEIVING confirmation message from Scheduler : \n " + (String)scheduler.get(null) + "\n");
				}
			}
		});
		
		input.start();
		output.start();
		/*try {
			getInputs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Printer.print(tasks.toString() + "\n\n");
		
		//puts each task into the taskQueue in the scheduler
		while(true) {
			Printer.print("FLOOR SUBSYSTEM: Task " + taskNum + " being sent to Scheduler : \n Task Information : " + tasks.get(taskNum) + "\n");
			Integer[] arr = new Integer[2];
			Task t = getNextTask();
			arr[0] = t.getStartFloor();
			arr[1] = t.getDirection();
			scheduler.put(new FloorRequest() {
				@Override
				public Integer[] getRequest() {
					return arr;
				}
			});
			//Printer.print("FLOOR SUBSYSTEM: Floor RECEIVING confirmation message from Scheduler : \n " + (String)scheduler.get(null) + "\n");
		}*/
		
			
	}

	//reads from the input file and calls parseAdd on each line 
	public void getInputs() throws IOException {
		String localDir = System.getProperty("user.dir");
		BufferedReader in = new BufferedReader(new FileReader(localDir + "\\src\\assets\\Inputs.txt"));
		String ln;
		
		while ((ln = in.readLine()) != null)
			parseAdd(ln);
		in.close();
	}
	
	public synchronized Task getNextTask() {
		while(tasks.size() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tasks.remove(0);
	}
	
	//splits each string by whitespace and creates a Task object and puts it into the matrix
	public synchronized void parseAdd(String ln) {
		notifyAll();
		
		String[] inputs = ln.split(" ");
	
		try {
			Task task = new Task(inputs[0], inputs[1], inputs[2]);
			tasks.add(task);
//			tasks.add(new Task(inputs[0], inputs[3], "", inputs[1]));
			//taskMatrix.get(task.getStartFloor()-1).get(task.getDirection()).add(task);
		} catch (Exception e) {
			Printer.print("Invalid Input: " + e);
		}
	}
	
	public static void main(String args[]){
		
		FloorsScheduler scheduler = new FloorsScheduler(-1);

		FloorSubsystem floorSS = new FloorSubsystem(scheduler);
		
		new Thread(floorSS,"FloorSS").start();
		
	}
	
}
