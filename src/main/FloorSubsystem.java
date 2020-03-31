package main;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.io.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import scheduler.ElevatorMessage;
import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import scheduler.SchedulerType;
import util.Printer;
import util.Transport;

public class FloorSubsystem implements Runnable{
	
	private static final int MAX_FLOORS = 22;

	//Arraylist to hold the tasks
	public static ArrayList<Task> tasks = new ArrayList<Task>();
	
	private ArrayList<Floor> floors = null;
	public FloorsScheduler scheduler = null;
	
	public FloorSubsystem(FloorsScheduler scheduler) {
		this.scheduler = scheduler;
		this.floors = new ArrayList<Floor>();
		
		//Create the floors
		for(int i=0; i<MAX_FLOORS; i++) {
			floors.add(new Floor(i+1,MAX_FLOORS));
		}
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
					
					//Store the request in the floor
					floors.get(t.getStartFloor() - 1).storeRequest(t);
					
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
					
					//What type of messages do we receive from the elevator?
					
					//Receive the message
					ElevatorMessage elevatorMessage = scheduler.get(null);
					//Check the floor it has arrived on and clear and tasks stored in this floor
					serviceFloorRequests(elevatorMessage.getFloorArrivedOn());
					
					Printer.print("FLOOR SUBSYSTEM: Floor RECEIVING confirmation message from Scheduler : \n " + elevatorMessage.getAcknowledgement() + "\n");
				}
			}
		});
		
		input.start();
		output.start();			
	}
	
	//Once we get an acknowledgement that an elevator has arrived at a certain floor.
	//We can clear all Requests stored in this floor.
	public void serviceFloorRequests(Integer floor) {
		floors.get(floor - 1).serviceRequest();
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
		
		//Start the FloorScheduler and store it in the FloorSubsystem
		Transport.setVerbose(true);
		FloorsScheduler scheduler = new FloorsScheduler(-1);
		FloorSubsystem floorSS = new FloorSubsystem(scheduler);
		
		new Thread(floorSS,"FloorSS").start();
		
		String inputFileDestination = "\\src\\assets\\Inputs.txt";
		InputParser ip = new InputParser(inputFileDestination);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		long strt = System.currentTimeMillis();
		LocalTime l = null;
		
		while(ip.requests.size() > 0) {
			String[] request = ip.requests.remove(0).split(" ");
			if(l == null) {
				l = LocalTime.parse(request[0]);
			}
			else {
				try {
					Thread.sleep(MILLIS.between(l, LocalTime.parse(request[0])));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			String time = LocalTime.now(ZoneId.systemDefault()).format(formatter);
			floorSS.parseAdd(time +" "+ request[1] +" "+ request[2]);
			System.out.println(time +" "+ request[1] +" "+ request[2]);
			System.out.println((System.currentTimeMillis() - strt)+": "+floorSS.tasks.size());
		}
	}
	
}
