package main;
import java.util.Comparator;
import java.util.PriorityQueue;

import scheduler.SchedulerType;
import scheduler.ElevatorScheduler;

public class Elevator {

	// ======== Victor's work ===================

	private boolean poweredOn;
	private ElevatorState state;
	private Doors doors;
	private PriorityQueue<Integer> workDoing;
	private PriorityQueue<Integer> workToDo;
	private SchedulerType scheduler = null;
	private ElevatorScheduler SchedulerElevator;
	
	public ElevatorState getState(){
		return state;
	}

	public Elevator(ElevatorScheduler scheduler) {
		this.SchedulerElevator = scheduler;
		state = new ElevatorState();
		doors = new Doors();
		workDoing = new PriorityQueue<Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return Math.abs(o1 - state.currentFloor) - Math.abs(o2 - state.currentFloor);
			}
		});
		workToDo = new PriorityQueue<Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return Math.abs(o1 - state.currentFloor) - Math.abs(o2 - state.currentFloor);
			}
		});
	}

	public boolean isPoweredOn() {
		return poweredOn;
	}

	public void powerOn() {
		Thread taskGetter = new Thread(new TaskGetter());
		poweredOn = true;
		taskGetter.start();
	}

	public void powerOff() {
		poweredOn = false;
	}

	// ========= Zeen's work ================

//	public synchronized void travel(int[][] tq) {
//
//		System.out.println("I'm Moving from " + tq[currentTask][0] + " floor!");
//
//		try {
//			Thread.sleep(tq[currentTask][2]);
//		} catch (InterruptedException e) {
//		}
//
//		System.out.println(tq[currentTask][3] + " floor Reached!");
//
//		// Send message to scheduler saying the elevator arrived destination?
//
//		// Open door and close door
//		toggleDoor(tq);
//
//	}
//
//	public synchronized void toggleDoor(int[][] tq) {
//
//		System.out.println("Door Opening");
//
//		try {
//			Thread.sleep(doorOpenTime);
//		} catch (InterruptedException e) {
//		}
//
//		System.out.println("Door Closing");
//
//		try {
//			Thread.sleep(doorCloseTime);
//		} catch (InterruptedException e) {
//		}
//
//		// Check queue. Another task? Next object in queue?
//		if (tq[++currentTask] == null) {
//			System.out.println("Elevator Wait for work");
//			thereIsWork = false;
//			getWork();
//		}
//
//		else {
//			travel(tq);
//		}
//
//	}
//
	// ======== Victor's work ===================

	public static void main(String arg[]) {
		System.out.println("=== Testing Elevator Class ===");
		//Elevator elevator = new Elevator();
		//elevator.powerOn();
	}

	private class TaskGetter implements Runnable {

		@Override
		public void run() {
			int i = 0;
			while(true) {
			
				Task o = (Task)SchedulerElevator.get(); 
				System.out.println("ELEVATOR SUBSYSYTEM: Elevator received task " + i + " from Scheduler\n Task Information : " + o.toString() + "\n");
				state.assignTask(o);
				System.out.println("ELEVATOR SUBSYSTEM: Elevator sending confirmation message to Scheduler: Task " + i + " received. Moving...\n");
				SchedulerElevator.put("Task " + i + " received. Moving...");
				i++;
			}
		}

	}

	// only need to give Scheduler the ElevatorState to check if the elevator
	// can stop at a floor. This protects data in Elevator
	public class ElevatorState {
		private static final float ACCELERATION = 0.68f;
		private static final float TERMINAL_VELOCITY = 4.31f;
		private static final float FLOOR_HEIGHT = 3.23f; // This should be a function call to Floor but whatever for now

		private ElevatorMotion motion;
		private int currentFloor;
		private float velocity;
		private int direction; // 1 is up, -1 is down
		private float metresTravelled; // per floor, NOT total

		private ElevatorState() {
			motion = new ElevatorMotion();
			currentFloor = 1;
			velocity = ACCELERATION; // i can't prove it but velocity must be >= ACCELERATION
			metresTravelled = 0;
		}

		public boolean isMoving() {
			return velocity > 0;
		}

		private void accelerate() {
			if (!isMoving())
				return;
			doMovement(velocity = Math.min(velocity + ACCELERATION, TERMINAL_VELOCITY));
		}

		private void decelerate() {
			if (!isMoving())
				return;
			doMovement(velocity = Math.max(velocity - ACCELERATION, 0));
		}

		private void doMovement(float velocity) {
			metresTravelled += velocity;

			if (metresTravelled >= FLOOR_HEIGHT) {
				currentFloor += direction;
				metresTravelled = 0;
				System.out.print("|");
			}
		}

		private float secondsToStop() {
			return velocity <= ACCELERATION ? 0 : velocity / ACCELERATION;
		}

		public boolean canStopAtFloor(int floor) {
			if (direction == 0)
				return true;
			if ((state.direction == -1 && floor > state.currentFloor)
					|| (state.direction == 1 && floor < state.currentFloor)) {
				return false;
			}
			if (!state.isMoving() || state.currentFloor == floor)
				return true;

			float distanceToFloor = Math.abs(floor - state.currentFloor) * FLOOR_HEIGHT;
			float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / state.velocity;
			return secondsToFloor >= state.secondsToStop();
		}

		public synchronized void assignTask(Task task) {
			//System.out.println("Elevator Received Task");
			if (canStopAtFloor(task.getStartFloor())) {

				if (currentFloor != task.getStartFloor() && !workDoing.contains(task.getStartFloor())) {
					workDoing.add(task.getStartFloor());
				}
				if (currentFloor != task.getDestinationFloor() && !workDoing.contains(task.getDestinationFloor())) {
					workDoing.add(task.getDestinationFloor());
				}

				//System.out.println("Added task to workDoing (" + task.getStartFloor() + " -> " + task.getDestinationFloor() + ")");

				//System.out.println("\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " + workToDo);

				if (!isAlive())
					wakeup();
			} else {
				if (currentFloor != task.getStartFloor() && !workToDo.contains(task.getStartFloor())) {
					workToDo.add(task.getStartFloor());
				}
				if (currentFloor != task.getDestinationFloor() && !workToDo.contains(task.getDestinationFloor())) {
					workToDo.add(task.getDestinationFloor());
				}

				//System.out.println("Added task to workToDo (" + task.getStartFloor() + " -> " + task.getDestinationFloor() + ")");

				//System.out.println("\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " + workToDo);
			}
		}

		private boolean isAlive() {
			return motion.running;
		}

		private synchronized void wakeup() {
			if (isAlive())
				return;

			Thread motionThread = new Thread(motion, "ElevatorMotion");
			//System.out.println("Waking up elevator");
			motion.running = true;
			motionThread.start();
		}

		private final class ElevatorMotion implements Runnable {
			private boolean running;
			private boolean taskAssigned;

			@Override
			public void run() {
				velocity = ACCELERATION;
				metresTravelled = 0;
				while (poweredOn && (!workDoing.isEmpty() || !workToDo.isEmpty())) {

					Integer targetFloor;
					boolean isWorkToDo = false;
					if ((targetFloor = workDoing.peek()) == null) {
						targetFloor = workToDo.peek();
						isWorkToDo = true;
					}
					direction = targetFloor > currentFloor ? 1 : -1;

					if (!taskAssigned) {
						//System.out.println("Starting a task (-> " + targetFloor + ")");
					}
					taskAssigned = true;

					float distanceToFloor = Math.abs(targetFloor - state.currentFloor) * FLOOR_HEIGHT;
					float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / state.velocity;

//					System.out.print(nextFloor + ", " + distanceToFloor + ", " + secondsToFloor + " {} ");

					if (secondsToFloor - 1 < state.secondsToStop()) {
						if (state.currentFloor == targetFloor) {
							//System.out.println("\nArrived at floor " + targetFloor);

							velocity = ACCELERATION;
							metresTravelled = 0;

							doors.openDoors();
							// TODO allow people to press button just in time and open doors again
							doors.closeDoors();

							if (isWorkToDo) {
								workToDo.poll();
							} else {
								workDoing.poll();
							}

							//System.out.println(
									//"\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " + workToDo);

							if ((targetFloor = workDoing.peek()) == null) {
								targetFloor = workToDo.peek();
							}

							//System.out.println("Moving towards floor " + targetFloor);
						} else {
							if (velocity == TERMINAL_VELOCITY) {
								System.out.print(".");// + currentFloor
							} else {
								System.out.print("-");
							}

							decelerate();
						}
					} else {
						if (velocity == TERMINAL_VELOCITY) {
							System.out.print(".");
						} else {
							System.out.print("+");
						}

						accelerate();
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				running = false;
				System.out.println("\nElevator is sleeping");
			}
		}
	}

	private class Doors {
		private final float DOOR_MOVE_TIME = 6.74f;

		private boolean doorsOpen;
		//private int movingDirection; // 1 for opening, -1 for closing

		private void openDoors() {
			if (doorsOpen)
				return;
			System.out.println("Opening doors");
			
			//movingDirection = 1;
			try {
				Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
			} catch (InterruptedException e) {
			}
			doorsOpen = true;
		}

		private void closeDoors() {
			if (!doorsOpen)
				return;
			System.out.println("Closing doors");
			//movingDirection = -1;
			try {
				Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
			} catch (InterruptedException e) {
			}
			doorsOpen = false;
		}
	}

	// This is a model of the Scheduler class, not the real thing
//	public static class Scheduler {
//		private final Task test1 = ;
//		private final Task test2 = ;
//
//		private ElevatorState[] elevatorStates = new ElevatorState[1];
//
//		public void onReadTaskFromFile() {
//			Task task = new Random().nextBoolean() ? test1 : test2;
//			if (elevatorStates[0].canStopAtFloor(task.startFloor)) {
//				elevatorStates[0].assignTask(task);
//			} else {
//				// check other elevators. if none can do it, wait
//			}
//		}
//	}

	// This is a model of the Task class, not the real thing
	
}
