package elevatorSubsystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import scheduler.ElevatorScheduler;
import scheduler.ElevatorScheduler.ElevatorMessage;
import scheduler.ElevatorScheduler.ElevatorStatusUpdate;

public class ElevatorSubsystem {

	boolean poweredOn;
	Elevator elevator;
//	PriorityQueue<Integer> workDoing;
	public BlockingQueue<Integer> workDoing;
	ElevatorScheduler scheduler;
	ElevatorState currentState;

	public ElevatorSubsystem(ElevatorScheduler schedulerElevator) {
		this.scheduler = schedulerElevator;
		elevator = new Elevator(this);
		workDoing = new LinkedBlockingQueue<>();
//		workDoing = new PriorityQueue<Integer>(new Comparator<Integer>() {
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				return Math.abs(o1 - elevator.currentFloor) - Math.abs(o2 - elevator.currentFloor);
//			}
//		});
//		workToDo = new PriorityQueue<Integer>(new Comparator<Integer>() {
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				return Math.abs(o1 - elevator.currentFloor) - Math.abs(o2 - elevator.currentFloor);
//			}
//		});
		currentState = ElevatorState.IDLE;
	}

	public boolean isPoweredOn() {
		return poweredOn;
	}

	public void powerOn() {
		Thread taskGetter = new Thread(new TaskGetter(this));
		poweredOn = true;
		taskGetter.start();
	}

	public void powerOff() {
		poweredOn = false;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public void updateWorkDoing(Integer[] newWorkDoing) {
		synchronized (workDoing) {
			workDoing.clear();
			for (Integer floor : newWorkDoing) {
				workDoing.add(floor);
			}
			System.out.println("[Elevator] updated work doing (size= " + workDoing.size() + ")");
		}
		if (!elevator.isAwake()) {
			elevator.wakeup();
		} else {
			System.out.println("[Elevator] already awake");
		}
	}

	void notifyStatus() {
		// send message to Scheduler
		// saying "elevator's current status"
		System.out.println("notify status called");

		ElevatorMessage em = new ElevatorMessage() {
			@Override
			public ElevatorStatusUpdate getStatusUpdate() {
				ElevatorStatusUpdate update = new ElevatorStatusUpdate() {
					@Override
					public int direction() {
						System.out.println("elev message direction");
						return new Integer(elevator.direction);
					}

					@Override
					public int currentFloor() {
						return new Integer(elevator.currentFloor);
					}

					@Override
					public float velocity() {
						return new Float(elevator.velocity);
					}

					@Override
					public boolean isSleeping() {
						return new Boolean(!elevator.isAwake());
					}
				};
				System.out.println("elev updated created");
				return update;
			}

		};

		scheduler.put(em);
	}

	void notifyArrivedAtFloor(int floor) {
		// send message to Scheduler
		// saying "elevator arrived at floor ${floor}"
		System.out.println("\nArrived at floor " + floor);

		String s = "Arrived " + floor + "th floor!";

		scheduler.put(new ElevatorMessage() {
			public String getAcknowledgement() {
				return s;
			}
		}); // some Objects to notify
	}

	public void notifyFloorRequest(int floor) {
		System.out.println("Someone requested floor " + floor);
		scheduler.put(new ElevatorMessage() {
			@Override
			public Integer getFloorRequest() {
				return floor;
			}
		});
	}

	enum ElevatorState {
		IDLE, ACCELERATING, DECELERATING, MAX_SPEED, DOORS_OPENING, DOORS_OPEN, DOORS_CLOSING, DOORS_CLOSED
	}

}