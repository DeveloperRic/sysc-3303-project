package scheduler;

import java.util.ArrayList;
import java.util.List;

import scheduler.ElevatorScheduler.ElevatorMessage;
import scheduler.ElevatorScheduler.ElevatorStatusUpdate;
import util.DblEndedPQ;

/**
 * Main Scheduler class that communicates from the floor subsystem to the
 * elevator subsystem.
 * 
 * Contains two queues that controls the flow of information between the floor
 * and elevator subsystems through put and get methods
 * 
 * @author Kevin
 *
 */
public class MainScheduler {

	// The value for an elevator going up
	private static final int UP = 1;

	// The value for an elevator going down
	private static final int DOWN = -1;

	Communication<Integer[], String> floorCommunication;
	Communication<Integer[], ElevatorMessage> elevatorCommunication;
	List<Integer[]> pendingRequests;
	ElevatorStatusUpdate elevatorStatusUpdate;
	String elevatorAcknowledgement;

	// Holds the current state of the elevator
	public SchedulerState currentState;

	public ElevatorStatus elevatorStatus;

	// Holds whether or not the message received is an update message
	public boolean isElevatorUpdate;

	// Holds whether or not the message received was an elevator acknowledge message
	public boolean isElevatorAck;

	// Holds whether or not the message received was a floor request
	public boolean isFloorRequest;

	/**
	 * Constructor class that instantiates the message lists
	 */
	public MainScheduler() {
		floorCommunication = new Communication<>("Floor", "Scheduler");
		elevatorCommunication = new Communication<>("Scheduler", "Elevator");
		pendingRequests = new ArrayList<>();
		currentState = SchedulerState.WAIT_FOR_INPUT;
		elevatorStatus = new ElevatorStatus();
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
	 * Returns the first object in the floor queue.
	 * 
	 * If the floor queue is empty, wait. If floor queue is not empty, return the
	 * first element
	 * 
	 * @return the first object of the floor queue
	 */
	public synchronized String floorGet() {
		return floorCommunication.aGet();
	}

	/**
	 * Puts an object in the elevator queue.
	 * 
	 * Places an object in the elevator queue that it received from the floor.
	 * 
	 * @param o The object to be put in the elevator queue
	 * @return true if successful, false otherwise
	 */
	public synchronized boolean floorPut(Integer[] o) {

		// parse request
		// set state to ReceivedRequestFromFloor

		// currently, these booleans are set to the message
		// being a floor request message
		isElevatorAck = false;
		isElevatorUpdate = false;
		isFloorRequest = true;

		System.out.println(
				"SCHEDULER SUBSYSTEM: Scheduler RECEIVED task from Floor\n Task Information : " + o.toString() + "\n");

		pendingRequests.add(o);
		// Current state should be waiting
		currentState.doWork(this);

		notifyAll();
		return true;
	}

	/**
	 * Returns the first object in the elevator queue.
	 * 
	 * If the elevator queue is empty, wait. If elevator queue is not empty, return
	 * the first element
	 * 
	 * @return the first object of the elevator queue
	 */
	public synchronized Integer elevatorGet() {
//		return elevatorCommunication.bGet();
		while (elevatorStatus.workDoing.size() == 0) {
			System.out.println("Waiting for task");
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		System.out.println("Notified and got a task");
		try {
			return elevatorStatus.workDoing.getMin();
		} catch (Exception e) {
			return null;
		}
	}
	
	public synchronized void elevatorRemove() {
		try {
			elevatorStatus.workDoing.deleteMin();
		} catch (Exception e) {}
	}

	/**
	 * Puts an object in the floor queue.
	 * 
	 * Places an object in the floor queue that it received from the elevator.
	 * 
	 * @param o The object to be put in the floor queue
	 * @return true if successful, false otherwise
	 */
	public boolean elevatorPut(ElevatorMessage o) {
		// parse request
		// set state to ReceivedAcknowledgementFromElevator
		// or
		// set state to ReceivedUpdateFromElevator

		// currently, these booleans are set to the message
		// being an elevator update message
		
		System.out.println("Reach to ElevatorPut");

		if (o.getFloorRequest() != null) {
			isElevatorAck = false;
			isElevatorUpdate = false;
			isFloorRequest = true;
			pendingRequests.add(new Integer[] {o.getFloorRequest(), elevatorStatus.direction});
		} else if (o.getStatusUpdate() != null) {
			isElevatorAck = false;
			isElevatorUpdate = true;
			isFloorRequest = false;
			elevatorStatusUpdate = o.getStatusUpdate();
		} else if (o.getAcknowledgement() != null) {
			isElevatorAck = true;
			isElevatorUpdate = false;
			isFloorRequest = false;
			elevatorAcknowledgement = o.getAcknowledgement();
		}

		// Current state should be waiting
		currentState.doWork(this);
		System.out.println(
				"SCHEDULER SUBSYSTEM: Scheduler RECEIVED message from Elevator\n Task Information : "
						+ o.toString() + "\n");
		//notifyAll();
		return true;
	}

	public class ElevatorStatus {

		private static final float ACCELERATION = 0.68f;
		private static final float FLOOR_HEIGHT = 3.23f;

		// Keeps track of the elevator's work to do (not assigned)
		public ArrayList<Integer[]> workToDo;
		// Keeps track of the elevator's work doing (assigned)
		// in a Double-ended Priority Queue
		public DblEndedPQ<Integer> workDoing;
		// Holds the current floors the elevator is taking
		private ArrayList<Integer> path;
		// Holds the current elevator direction
		private int direction;
		// Holds the current Elevator floor
		int currentFloor;
		Integer previousFloor;
		// Holds the current elevator velocity
		private float velocity;

		// Holds the max & min allowable floors e.g.)
		// elev >>> 9 (max floor)
		// on fl 3 someone says "i want to go up"
		// on fl 10 someone says "i want to go down"
		// scheduler should queue the 2nd request
		// elev <<< 3
		// elev >=< ? go to some requested floor
		// elev >>> 10
		// elev >=< ? go to some requested floor
		private int[] floorRequestBoundary = new int[2];

		private ElevatorStatus() {
			direction = 0;
			previousFloor = 0;
			currentFloor = 0;
			velocity = 0;
			path = new ArrayList<>();
			workToDo = new ArrayList<>();
			workDoing = new DblEndedPQ<Integer>();
		}

		void update(ElevatorStatusUpdate update) {
			direction = update.direction();
			currentFloor = update.currentFloor();
			velocity = update.velocity();
		}

		/**
		 * This function should be the brains behind what floors it wants the elevator
		 * to go to
		 * 
		 * @param mainScheduler TODO
		 * @return true if adding work to elevator
		 */
		synchronized boolean addToQueue(Integer[] movementRequest) {

			// given the elevator direction, elevator floors to visit, requested floor,
			// requested direction
			// find out whether the current floor to be visited fits in the elevator's path
			// this step involves using the elevator velocity, elevator
			// deceleration/acceleration,
			// elevator direction, requested direction, requested floor
			// if it's not on the way, keep it in a work to be done queue
			// if it is on the way, send the command for the elevator to go to the floor and
			// add that to the elevator work list
			// update appropriate things

			// The last received message should be the task
//			Task t = (Task) elevatorMessageQueue.remove(elevatorMessageQueue.size() - 1);

			int sourceOrTargetFloor = movementRequest[0];
			int targetDirection = movementRequest[1];

			// whether or not to add to the work doing queue (assign to elevator)
			boolean addToWorkDoing = canStopAtFloor(sourceOrTargetFloor, targetDirection);

			// If adding it to the work doing queue
			if (addToWorkDoing) {
				workDoing.insert(sourceOrTargetFloor);
				direction = targetDirection;
			} else {
				workToDo.add(movementRequest);
			}

			notifyAll();
			return addToWorkDoing;
		}

		private boolean canStopAtFloor(int floor, int direction) {
			// TODO: if floor already exists, addToWorkDoing = false;


			// If elevator has not been assigned anything yet
			if (velocity == 0 || currentFloor == floor || direction == 0) {
				return true;
			}
						
			// If directions are different
			if (direction != this.direction) {
				return false;
			}

			// If start floor lower than elevator while elevator going up
			if (floor < currentFloor && direction == UP) {
				return false;
			}

			// If start floor higher than elevator while elevator going down
			if (floor > currentFloor && direction == DOWN) {
				return false;
			}

			if (floor < floorRequestBoundary[0] || floor > floorRequestBoundary[1]) {
				return false;
			}

			// If elevator has not been assigned anything yet
			if (velocity == 0 || currentFloor == floor || direction == 0) {
				return true;
			}

			// if velocity is too fast to slow down, return false;
			float distanceToFloor = Math.abs(floor - currentFloor) * FLOOR_HEIGHT;
			float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / velocity;
			return secondsToFloor >= secondsToStop();
		}

		private float secondsToStop() {
			return velocity <= ACCELERATION ? 0 : velocity / ACCELERATION;
		}

		public void onElevatorIdle() {
			floorRequestBoundary[0] = -1; // min floor
			floorRequestBoundary[1] = -1; // max floor
		}

		public void onElevatorAssignedFirstTask(int floor, int direction) {
			if (direction == UP) {
				floorRequestBoundary[0] = floor; // min floor
				floorRequestBoundary[1] = -1; // max floor
			} else if (direction == DOWN) {
				floorRequestBoundary[0] = -1; // min floor
				floorRequestBoundary[1] = floor; // max floor
			}
		}

		public void onOutOfBoundsTaskQueued() {
			if (direction == UP) {
				floorRequestBoundary[1] = workDoing.getMax(); // max floor
			} else if (direction == DOWN) {
				floorRequestBoundary[0] = workDoing.getMin(); // min floor
			}
		}

		public int getDirection(){
			return direction;
		}

		public float getVelocity(){
			return velocity;
		}

		public int getCurrentFloor(){
			return currentFloor;
		}

	}
}