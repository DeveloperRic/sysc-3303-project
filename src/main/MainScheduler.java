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
	
	private final int UP = 1;
	
	private final int DOWN = -1;
	
	public ArrayList<Task>elevatorWorkToDo;
	
	public ArrayList<Task>elevatorWorkDoing;
	
	public ArrayList<Integer>floorsToSendToElevator;
	
	public ArrayList<Integer>elevatorPath;
	
	public int currentElevatorDirection;
	
	public int currentElevatorFloor;
	
	public float currentElevatorVelocity;
	
	//The messages from floor to elevator
	public ArrayList<Object> elevatorMessageQueue;
	
	//The messages from elevator to floor
	public ArrayList<Object> floorMessageQueue;
	
	public SchedulerState currentState;
	
	private boolean isElevatorUpdate;
	
	private boolean isElevatorAck;
	
	private boolean isFloorRequest;

	/**
	 * Constructor class that instantiates the message lists
	 */
	public MainScheduler() {
		elevatorWorkToDo = new ArrayList<>();
		elevatorWorkDoing = new ArrayList<>();
		floorsToSendToElevator = new ArrayList<>();
		elevatorMessageQueue = new ArrayList<>();
		floorMessageQueue = new ArrayList<>();
		elevatorPath = new ArrayList<>();
		currentState = new WaitForInput();
		isElevatorAck = false;
		isElevatorUpdate = false;
		isFloorRequest = false;
		currentElevatorDirection = 0;
		currentElevatorFloor = 0;
		currentElevatorVelocity = 0;
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
	
	public boolean doMath() {
		
		Task t = (Task)elevatorMessageQueue.remove(elevatorMessageQueue.size()-1);
		
		boolean addToWorkDoing = false;
		
		//TODO: if floor already exists, addToWorkDoing = false;
		
		if(t.getDirection() != currentElevatorDirection) {
			addToWorkDoing = false;
		}
		
		if(t.getStartFloor() < currentElevatorFloor && currentElevatorDirection == UP) {
			addToWorkDoing = false;
		}
		
		if(t.getStartFloor() > currentElevatorFloor && currentElevatorDirection == DOWN) {
			addToWorkDoing = false;
		}
		
		//TODO: if velocity is too fast to slow down, return false;
		//(currentfloor - startfloor) * heightOfFloor...
		
		if(currentElevatorDirection == 0) {
			addToWorkDoing = true;
		}
		
		if(addToWorkDoing) {
			floorsToSendToElevator.add(t.getStartFloor());
			elevatorWorkDoing.add(t);
		}
		else {
			elevatorWorkToDo.add(t);
		}
		
		//	given the elevator direction, elevator floors to visit, requested floor, requested direction
		//	find out whether the current floor to be visited fits in the elevator's path
		//		this step involves using the elevator velocity, elevator deceleration/acceleration,
		//		elevator direction, requested direction, requested floor
		//	if it's not on the way, keep it in a work to be done queue
		//	if it is on the way, send the command for the elevator to go to the floor and add that to the elevator work list
		//	update appropriate things
		return true;
	}
	
	public void updateElevatorVals() {
		float[] stuff = (float[])floorMessageQueue.remove(0);
		currentElevatorFloor = (int)stuff[0];
		currentElevatorVelocity = stuff[1];
		currentElevatorDirection = (int)stuff[2];
		
		for(int i = 0; i < elevatorWorkDoing.size(); i++) {
			if(currentElevatorFloor == elevatorWorkDoing.get(i).getStartFloor()) {
				floorsToSendToElevator.add(elevatorWorkDoing.get(i).getDestinationFloor());
				elevatorWorkDoing.remove(i);
			}
		}
		
		for(int i = 0; i < elevatorPath.size(); i++) {
			if(currentElevatorFloor == elevatorPath.get(i)) {
				elevatorPath.remove(i);
			}
		}
		
		//TODO: if no more work to be done, send work to the elevator
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
		isElevatorAck = false;
		isElevatorUpdate = false;
		isFloorRequest = true;
		elevatorMessageQueue.add(o);
		currentState.doWork(this);
		
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED task from Floor\n Task Information : " + o.toString() + "\n");
		notifyAll();
		return true;
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
		isElevatorAck = false;
		isElevatorUpdate = true;
		isFloorRequest = false;
		floorMessageQueue.add(o);
		currentState.doWork(this);
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED confirmation message from Elevator\n Task Information : " + o.toString() + "\n");
		notifyAll();
		return true;
	}

}
