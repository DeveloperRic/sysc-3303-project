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

	//The value for an elevator going up
	private final int UP = 1;

	//The value for an elevator going down
	private final int DOWN = -1;

	//Keeps track of the elevator's work to do (not assigned)
	public ArrayList<Task>elevatorWorkToDo;

	//Keeps track of the elevator's work doing (assigned)
	public ArrayList<Task>elevatorWorkDoing;

	//Holds the floor numbers the scheduler wants to send to the elevator
	public ArrayList<Integer>floorsToSendToElevator;

	//Holds the current floors the elevator is taking
	public ArrayList<Integer>elevatorPath;

	//Holds the current elevator direction
	public int currentElevatorDirection;

	//Holds the current Elevatoor floor
	public int currentElevatorFloor;

	//Holds the current elevator velocity
	public float currentElevatorVelocity;
	
	//The messages from floor to elevator
	public ArrayList<Object> elevatorMessageQueue;
	
	//The messages from elevator to floor
	public ArrayList<Object> floorMessageQueue;
	
	//Holds the current state of the elevator
		public SchedulerState currentState;

		//Holds whether or not the message received is an update message
		private boolean isElevatorUpdate;

		//Holds whether or not the message received was an elevator acknowledge message 
		private boolean isElevatorAck;

		//Holds whether or not the message received was a floor request
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
	
	/**
	 * Sets the current state of the scheduler
	 * 
	 * @param s The state to transition into
	 */
	public void setState(SchedulerState s) {
		currentState = s;
	}

	/**
	 * Returns whether or not the message was
	 * an elevator update message.
	 * 
	 * @return true if message was update message
	 */
	public boolean isElevatorUpdate() {
		return isElevatorUpdate;
	}

	/**
	 * Returns whether or not the message was
	 * an elevator acknowledge message.
	 * 
	 * @return true if message was acknowledge message
	 */
	public boolean isElevatorAck() {
		return isElevatorAck;
	}

	/**
	 * Returns whether or not the message was
	 * a floor request message
	 * 
	 * @return true if message was request message
	 */
	public boolean isFloorRequest() {
		return isFloorRequest;
	}

	/**
	 * This function should be the brains behind what floors
	 * it wants the elevator to go to
	 * 
	 * @return true if adding work to elevator
	 */
	public boolean doMath() {

		//	given the elevator direction, elevator floors to visit, requested floor, requested direction
		//	find out whether the current floor to be visited fits in the elevator's path
		//		this step involves using the elevator velocity, elevator deceleration/acceleration,
		//		elevator direction, requested direction, requested floor
		//	if it's not on the way, keep it in a work to be done queue
		//	if it is on the way, send the command for the elevator to go to the floor and add that to the elevator work list
		//	update appropriate things

		//The last received message should be the task
		Task t = (Task)elevatorMessageQueue.remove(elevatorMessageQueue.size()-1);

		//whether or not to add to the work doing queue (assign to elevator)
		boolean addToWorkDoing = false;

		//TODO: if floor already exists, addToWorkDoing = false;

		//If directions are different
		if(t.getDirection() != currentElevatorDirection) {
			addToWorkDoing = false;
		}

		//If start floor lower than elevator while elevator going up
		if(t.getStartFloor() < currentElevatorFloor && currentElevatorDirection == UP) {
			addToWorkDoing = false;
		}

		//If start floor higher than elevator while elevator going down
		if(t.getStartFloor() > currentElevatorFloor && currentElevatorDirection == DOWN) {
			addToWorkDoing = false;
		}

		//TODO: if velocity is too fast to slow down, return false;
		//(currentfloor - startfloor) * heightOfFloor...

		//If elevator has not been assigned anything yet
		if(currentElevatorDirection == 0) {
			addToWorkDoing = true;
		}

		//If adding it to the work doing queue
		if(addToWorkDoing) {
			floorsToSendToElevator.add(t.getStartFloor());
			elevatorWorkDoing.add(t);
		}
		else {
			elevatorWorkToDo.add(t);
		}

		return addToWorkDoing;
	}

	/**
	 * Updates the elevator values
	 */
	public void updateElevatorVals() {
		float[] stuff = (float[])floorMessageQueue.remove(floorMessageQueue.size()-1);
		currentElevatorFloor = (int)stuff[0];
		currentElevatorVelocity = stuff[1];
		currentElevatorDirection = (int)stuff[2];

		//if the elevator has reached a start floor, remove task, add destination floor
		for(int i = 0; i < elevatorWorkDoing.size(); i++) {
			if(currentElevatorFloor == elevatorWorkDoing.get(i).getStartFloor()) {
				floorsToSendToElevator.add(elevatorWorkDoing.get(i).getDestinationFloor());
				elevatorWorkDoing.remove(i);
			}
		}

		//If current floor is on elevator path, remove it
		for(int i = 0; i < elevatorPath.size(); i++) {
			if(currentElevatorFloor == elevatorPath.get(i)) {
				elevatorPath.remove(i);
			}
		}
	}

		//TODO: if no more work to be done, send work to the elevator
	/**
	 * Returns the first object in the floor queue.
	 * 
	 * If the floor queue is empty, wait.
	 * If floor queue is not empty, return the first element
	 * 
	 * @return the first object of the floor queue
	 */
	public synchronized Object floorGet() {
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

		//currently, these booleans are set to the message 
		//being a floor request message
		isElevatorAck = false;
		isElevatorUpdate = false;
		isFloorRequest = true;

		elevatorMessageQueue.add(o);

		//Current state should be waiting
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

		//currently, these booleans are set to the message 
		//being an elevator update message
		isElevatorAck = false;
		isElevatorUpdate = true;
		isFloorRequest = false;

		floorMessageQueue.add(o);

		//Current state should be waiting
		currentState.doWork(this);
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED confirmation message from Elevator\n Task Information : " + o.toString() + "\n");
		notifyAll();
		return true;
	}

}
