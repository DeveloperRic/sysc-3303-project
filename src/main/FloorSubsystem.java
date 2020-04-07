package main;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import scheduler.ElevatorMessage;
import scheduler.FloorMessage;
import scheduler.FloorsScheduler;
import util.Printer;
import util.Transport;


/**
 * This class represents the floor subsystem for all the floors. It sends and
 * messages to request an elevator to a certain floor. It receives messages and notifies
 * a the floors if an elevator has arrived on a certain floor.
 * 
 */
public class FloorSubsystem implements Runnable {

	private static final int MAX_FLOORS = 22;

	public static ArrayList<Task> tasks = new ArrayList<Task>();

	private ArrayList<Floor> floors = null;
	public FloorsScheduler scheduler = null;
	
	/**
	 * Initializes the floor subsystem by initializing the floors and the
	 * floor scheduler.
	 * 
	 * @param scheduler	The floorScheduler used as an in between communication between the
	 * 					floor subsystem and the main scheduler.
	 * 
	 */
	public FloorSubsystem(FloorsScheduler scheduler) {
		this.scheduler = scheduler;
		this.floors = new ArrayList<Floor>();

		// Create the floors
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors.add(new Floor(i + 1, MAX_FLOORS));
		}
	}
	
	/**
	 * Creates two threads called input and output. Input sends messages to the scheduler and
	 * output receives messages from the scheduler.
	 * 
	 */
	@Override
	public void run() {	
		/**
		 * A new thread to get a task and store it as a Floor Message to be used by
		 * the Floor Scheduler.
		 * 
		 */
		Thread input = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					Task t = getNextTask();

					// Store the request in the floor if it is not a fault
					if(!t.isFault())
						floors.get(t.getStartFloor() - 1).storeRequest(t);
					
					try {
						
						//Convert the task into a floor message
						FloorMessage floorMessage = new FloorMessage() {
							@Override
							public Integer[] getRequest() {
								return t.getRequest();
							}
							
							@Override
							public Task getTask() {
								return t;
							}
						};
						floorMessage.selectedElevator = floorMessage.sourceElevator = t.getElevatorNumber();
						
						if(t.isFault()) {
							System.out.println("Floor Subsytem : I'M SENDING A FAULT");
							System.out.println(t);
						}
						
						scheduler.put(floorMessage);
						
						if(t.isFault()) {
						 System.out.println("Floor Subsystem : I'M DONE SENDING A FAULT");
						}
						
						
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(0);
						return;
					}
				}

			}
		});

		/**
		 * A new thread to listen for an elevator message of an elevator 
		 * arriving on a certain floor and service the floor which requested 
		 * this elevator.
		 * 
		 */
		Thread output = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					// Receive the message
					ElevatorMessage elevatorMessage;

					try {
						elevatorMessage = scheduler.get(null);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(0);
						return;
					}

					// Check the floor it has arrived on and clear and tasks stored in this floor
					serviceFloorRequests(elevatorMessage.getFloorArrivedOn());

					Printer.print("FLOOR SUBSYSTEM: Floor RECEIVING confirmation message from Scheduler : \n "
							+ elevatorMessage.getAcknowledgement() + "\n");
				}
			}
		});

		input.start();
		output.start();
	}

	/**
	 * Services the requests stored within a floor.
	 * 
	 * @param floor	Indicates the floor in which the elevator has
	 * 				arrived on.
	 */
	public void serviceFloorRequests(Integer floor) {
		floors.get(floor - 1).serviceRequest();
	}

	/**
	 * Reads the "Inputs.txt" file and converts each line to be stored as a task.
	 * 
	 */
	public void getInputs() throws IOException {
		String localDir = System.getProperty("user.dir");
		BufferedReader in = new BufferedReader(new FileReader(localDir + "\\src\\assets\\Inputs.txt"));
		String ln;
		while ((ln = in.readLine()) != null)
			parseAdd(ln);
		in.close();
	}
	
	/**
	 * Retrieves the next task from the tasks list and removes it from the list.
	 * 
	 */
	public synchronized Task getNextTask() {
		while (tasks.size() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		return tasks.remove(0);
	}

	/**
	 * Splits the input string by whitespace and creates a Task object which is then added
	 * to the tasks array list.
	 * 
	 * @param ln	A string representing a line from the input file.
	 * 
	 * @return Task	An Object that represents a line from the input text file.
	 * 
	 */
	public synchronized void parseAdd(String ln) {
		notifyAll();

		String[] inputs = ln.split(" ");

		try {
			
			Task task = null;
			boolean isFault = false;
			
			//Check if the input is an injected fault by checking if the start floor is a negative number
			if( Integer.parseInt(inputs[1]) < 0 )
				isFault = true;
			
			task = new Task(inputs[0],inputs[1],inputs[2],isFault);
			
			//Set the time difference
			if(isFault) {
				System.out.println(Arrays.toString(inputs));
				task.setTimeDifference(Integer.parseInt(inputs[3]));
			}
			
			tasks.add(task);
			
		} catch (Exception e) {
			Printer.print("Invalid Input: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Part of the integrated test responsible for starting up the floor subsystem,
	 * read the input file, convert each line into a task and send messages to the 
	 * scheduler with a time simulated delay.
	 * 
	 */
	public static void main(String args[]) {

		// Start the FloorScheduler and store it in the FloorSubsystem
		Transport.setVerbose(true);			//Set this to true if you want more information and false if otherwise.
		FloorsScheduler scheduler;

		try {
			scheduler = new FloorsScheduler(-1);
		} catch (UnknownHostException | SocketException e1) {
			e1.printStackTrace();
			return;
		}

		FloorSubsystem floorSS = new FloorSubsystem(scheduler);

		new Thread(floorSS, "FloorSS").start();
		
		//Read the lines from the "Inputs.txt" file.
		String inputFileDestination = "\\src\\assets\\Inputs.txt";
		InputParser ip = new InputParser(inputFileDestination);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		long strt = System.currentTimeMillis();
		LocalTime l = null;
		
		
		//Creates a time delay based on the time stamp difference between each line
		while (ip.requests.size() > 0) {
			String[] request = ip.requests.remove(0).split(" ");
			System.out.println(request.length);
			if (l == null) {
				l = LocalTime.parse(request[0]);
			} else {
				try {
					Thread.sleep(MILLIS.between(l, LocalTime.parse(request[0])));
				} catch (InterruptedException e) {
				}
				l = LocalTime.parse(request[0]);
			}

			String time = LocalTime.now(ZoneId.systemDefault()).format(formatter);	
			
			//Send the input line to the floor subsystem once it's done.
			floorSS.parseAdd(time + " " + request[1] + " " + request[2] + " " +  request[3]);

			System.out.println(time + " " + request[1] + " " + request[2]);
			System.out.println((System.currentTimeMillis() - strt) + ": " + FloorSubsystem.tasks.size());
		}
	}

}
