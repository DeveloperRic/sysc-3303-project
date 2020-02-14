package main.elevator;

import java.util.Comparator;
import java.util.PriorityQueue;

import main.SchedulerElevator;

public class ElevatorSubsystem {

	boolean poweredOn;
	Elevator elevator;
	PriorityQueue<Integer> workDoing;
	SchedulerElevator SchedulerElevator;

	public ElevatorSubsystem(SchedulerElevator scheduler) {
		this.SchedulerElevator = scheduler;
		elevator = new Elevator(this);
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
	
	public PriorityQueue<Integer> getWorkDoing() {
		return workDoing;
	}
	
	public void assignTask(int floor) {
		workDoing.add(floor);
	}

}
