package main.elevator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import main.SchedulerElevator;

public class ElevatorSubsystem {

	boolean poweredOn;
	Elevator elevator;
//	PriorityQueue<Integer> workDoing;
	List<Integer> workDoing;
	SchedulerElevator scheduler;
	ElevatorState currentState;

	public ElevatorSubsystem(SchedulerElevator schedulerElevator) {
		this.scheduler = schedulerElevator;
		elevator = new Elevator(this);
		workDoing = new ArrayList<Integer>();
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

	public List<Integer> getWorkDoing() {
		return workDoing;
	}

	public void assignTask(int floor) {
		workDoing.add(floor);
		if (!elevator.isAwake())
			elevator.wakeup();
	}
	
	void notifyStatus(int floor, float velocity, int direction) {
		// send message to Scheduler
		// saying "elevator's current status"
		scheduler.put(new float[] {floor, velocity, direction});
	}

	void notifyArrivedAtFloor(int floor) {
		System.out.println("\nArrived at floor " + floor);
		// send message to Scheduler
		// saying "elevator arrived at floor ${floor}"
		scheduler.put(floor); // some Objects to notify
	}

	enum ElevatorState {
		IDLE, ACCELERATING, DECELERATING, MAX_SPEED, DOORS_OPENING, DOORS_OPEN, DOORS_CLOSING, DOORS_CLOSED
	}

}