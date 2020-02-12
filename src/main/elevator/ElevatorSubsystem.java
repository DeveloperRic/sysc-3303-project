package main.elevator;

import java.util.Comparator;
import java.util.PriorityQueue;

import main.SchedulerElevator;

public class ElevatorSubsystem {

	boolean poweredOn;
	ElevatorState state;
	ElevatorDoors doors;
	ElevatorButton[] buttons;
	PriorityQueue<Integer> workDoing;
	PriorityQueue<Integer> workToDo;
	SchedulerElevator SchedulerElevator;

	public ElevatorSubsystem(SchedulerElevator scheduler) {
		this.SchedulerElevator = scheduler;
		state = new ElevatorState(this);
		doors = new ElevatorDoors();
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
		buttons = new ElevatorButton[21]; 
		for (int i = 1; i <= 21; ++i) {
			buttons[i - 1] = new ElevatorButton(i);
		}
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

	public ElevatorState getState() {
		return state;
	}

//	public static void main(String arg[]) {
//		System.out.println("=== Testing Elevator Class ===");
//		// Elevator elevator = new Elevator();
//		// elevator.powerOn();
//	}

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

}
