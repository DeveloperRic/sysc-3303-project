/**
 * 
 */
package main;
import java.util.ArrayList;

import schedulerStates.SchedulerState;
import schedulerStates.WaitForInput;

/**
 * Main Scheduler class that communicates from the floor
 * subsystem to the elevator subsystem.
 * 
 * Contains two queues that controls the flow of information
 * between the floor and elevator subsystems through put
 * and get methods
 * 
 * @author Kevin
 *
 */
public class MainScheduler {
	
	//The messages from floor to elevator
	private ArrayList<Object> elevatorMessageQueue;
	
	//The messages from elevator to floor
	private ArrayList<Object> floorMessageQueue;
	
	private SchedulerState currentState;
	
	private boolean isElevatorUpdate;
	
	private boolean isElevatorAck;
	
	private boolean isFloorRequest;

	/**
	 * Constructor class that instantiates the message lists
	 */
	public MainScheduler() {
		elevatorMessageQueue = new ArrayList<>();
		floorMessageQueue = new ArrayList<>();
		currentState = new WaitForInput();
		isElevatorAck = false;
		isElevatorUpdate = false;
		isFloorRequest = false;
	}
	
	public void setState(SchedulerState s) {
		currentState = s;
	}
	
	public boolean isElevatorUpdate() {
		return isElevatorUpdate;
	}
	
	public boolean isElevatorAck() {
		return isElevatorAck;
	}
	
	public boolean isFloorRequest() {
		return isFloorRequest;
	}
	
	/**
	 * Returns the first object in the floor queue.
	 * 
	 * If the floor queue is empty, wait.
	 * If floor queue is not empty, return the first element
	 * 
	 * @return the first object of the floor queue
	 */
	public synchronized Object floorGet() {
		
		//this won't be needed once switch to UPD
		
		while(floorMessageQueue.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler SENDING confirmation message to Floor\n Task Information : " + floorMessageQueue.get(0).toString() + "\n");
		notifyAll();
		return floorMessageQueue.remove(0);
	}

	/**
	 * Puts an object in the elevator queue.
	 * 
	 * Places an object in the elevator queue that it
	 * received from the floor.
	 * 
	 * @param o The object to be put in the elevator queue
	 * @return true if successful, false otherwise
	 */
	public synchronized boolean floorPut(Object o) {
		
		//parse request
		//set state to ReceivedRequestFromFloor
		currentState.doWork(this);
		
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED task from Floor\n Task Information : " + o.toString() + "\n");
		notifyAll();
		return elevatorMessageQueue.add(o);
	}

	/**
	 * Returns the first object in the elevator queue.
	 * 
	 * If the elevator queue is empty, wait.
	 * If elevator queue is not empty, return the first element
	 * 
	 * @return the first object of the elevator queue
	 */
	public synchronized Object elevatorGet() {
		
		//this won't be needed once switch to UPD
		
		while(elevatorMessageQueue.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler SENDING task to Elevator\n Task Information : " + elevatorMessageQueue.get(0).toString() + "\n");
		notifyAll();
		return elevatorMessageQueue.remove(0);
	}

	/**
	 * Puts an object in the floor queue.
	 * 
	 * Places an object in the floor queue that it
	 * received from the elevator.
	 * 
	 * @param o The object to be put in the floor queue
	 * @return true if successful, false otherwise
	 */
	public synchronized boolean elevatorPut(Object o) {
		//parse request
		//set state to ReceivedAcknowledgementFromElevator
		// or
		//set state to ReceivedUpdateFromElevator
		currentState.doWork(this);
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED confirmation message from Elevator\n Task Information : " + o.toString() + "\n");
		notifyAll();
		return floorMessageQueue.add(o);
	}

}
