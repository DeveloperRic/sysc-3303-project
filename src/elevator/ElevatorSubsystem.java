package elevator;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import main.InputParser;
import main.Task;
import scheduler.ElevatorMessage;
import scheduler.ElevatorScheduler;
import scheduler.FloorMessage;
import util.Printer;
import util.Transport;

public class ElevatorSubsystem {

	public static boolean verbose = true;

	boolean poweredOn;
	int elevatorNumber;
	Elevator elevator;
	PriorityQueue<Integer> workDoing;
//	List<Integer> workDoing;
	ElevatorScheduler scheduler;
	ElevatorState currentState;

	private ArrayList<Task> tasks = new ArrayList<Task>();

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
			@Override
			public String getAcknowledgement() {
				return s;
			}

			@Override
			public Integer getFloorArrivedOn() {
				return floor;
			}
		};

		try {
			scheduler.put(em);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
			return;
		}

		elevator.buttons[floor - 1].unpress();
	}

	void notifyButtonPressed(int destFloor) {
		synchronized (elevator) {
			Integer[] r = new Integer[] { destFloor, 0 };
			FloorMessage request = new FloorMessage() {
				@Override
				public Integer[] getRequest() {
					return r;
				}

				@Override
				public Integer getSourceElevator() {
					return elevatorNumber;
				}
				
				@Override
				public Task getTask() {
					return null;
				}
			};
			request.sourceElevator = elevatorNumber;
			try {
				scheduler.put(new ElevatorMessage() {
					@Override
					public FloorMessage getFloorRequest() {
						return request;
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
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
		Transport.setVerbose(verbose);
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	enum ElevatorState {
		IDLE, ACCELERATING, DECELERATING, MAX_SPEED, DOORS_OPENING, DOORS_OPEN, DOORS_CLOSING, DOORS_CLOSED
	}

	public static void main(String[] args) {

//		System.out.print("Enter this elevator's number : ");
//		Scanner scanner = new Scanner(System.in);
//		int elevNum = Integer.parseInt(scanner.nextLine());
//
//		System.out.println("elevatorNumber set to " + elevNum + "\n");

		InputParser ip = new InputParser("\\src\\assets\\Inputs.txt");
		ArrayList<Task> tasks = new ArrayList<Task>();

		while (ip.requests.size() > 0) {
			String[] request = ip.requests.remove(0).split(" ");
			Task newTask = new Task(request[0], request[1], request[2], request[3]);
			tasks.add(newTask);
		}

		ElevatorSubsystem subsystem;
		try {
			subsystem = new ElevatorSubsystem(new ElevatorScheduler(1));
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			System.exit(0);
			return;
		}

		subsystem.setTasks(tasks);
		ElevatorSubsystem.setVerbose(false);
		subsystem.powerOn();

	}

}