package elevator;

import java.util.Comparator;
import java.util.PriorityQueue;

import scheduler.ElevatorMessage;
import scheduler.ElevatorScheduler;
import scheduler.FloorRequest;
import util.Printer;

public class ElevatorSubsystem {

	static boolean verbose = true;

	boolean poweredOn;
	int elevatorNumber;
	Elevator elevator;
	PriorityQueue<Integer> workDoing;
//	List<Integer> workDoing;
	ElevatorScheduler scheduler;
	ElevatorState currentState;

	public ElevatorSubsystem(ElevatorScheduler schedulerElevator) {
		this.scheduler = schedulerElevator;
		elevatorNumber = schedulerElevator.getElevatorNumber();
		elevator = new Elevator(this);
//		workDoing = new ArrayList<Integer>();
		workDoing = new PriorityQueue<Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return Math.abs(o1 - elevator.currentFloor) - Math.abs(o2 - elevator.currentFloor);
			}
		});
//		workToDo = new PriorityQueue<Integer>(new Comparator<Integer>() {
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				return Math.abs(o1 - elevator.currentFloor) - Math.abs(o2 - elevator.currentFloor);
//			}
//		});
		currentState = ElevatorState.IDLE;
	}

	public int getFirstWorkDoing() {
		return workDoing.peek();
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

	public void assignTask(int floor) {
		synchronized (workDoing) {
			if (!workDoing.contains(floor)) {
				workDoing.add(floor);

				Printer.print("ELEVATOR SUBSYSTEM: Assigned task (floor = " + floor + ")\n");

				if (!elevator.isAwake())
					elevator.wakeup();
			} else {
				Printer.print("ELEVATOR SUBSYSTEM: Skipping task (floor = " + floor + ") -- already in queue\n");
			}
		}
	}

	void notifyArrivedAtFloor(int floor) {
		// send message to Scheduler
		// saying "elevator arrived at floor ${floor}"

		String s = "Elevator " + elevatorNumber + " arrived at floor " + floor;
		Printer.print("\n[Elevator] " + s + "\n");

		ElevatorMessage em = new ElevatorMessage() {
			public String getAcknowledgement() {
				return s;
			}
		};

		scheduler.put(em);

		elevator.buttons[floor+1].unpress();
	}

	void notifyButtonPressed(int destFloor) {
		synchronized (elevator) {
			Integer[] r = new Integer[] { destFloor, 0 };
			FloorRequest request = new FloorRequest() {
				@Override
				public Integer[] getRequest() {
					return r;
				}

				@Override
				public Integer getSourceElevator() {
					return elevatorNumber;
				}
			};
			request.sourceElevator = elevatorNumber;
			scheduler.put(new ElevatorMessage() {
				@Override
				public FloorRequest getFloorRequest() {
					return request;
				}
			});
		}
	}

	public void pressButton(int floor) {
		elevator.buttons[floor - 1].press();
	}
	
	public boolean isButtonPressed(int floor) {
		return elevator.buttons[floor - 1].isPressed();
	}

	public static void setVerbose(boolean verbose) {
		ElevatorSubsystem.verbose = verbose;
	}

	enum ElevatorState {
		IDLE, ACCELERATING, DECELERATING, MAX_SPEED, DOORS_OPENING, DOORS_OPEN, DOORS_CLOSING, DOORS_CLOSED
	}

}