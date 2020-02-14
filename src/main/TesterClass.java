package main;

import scheduler.MainScheduler;
import scheduler.ElevatorScheduler;
import scheduler.FloorsScheduler;

public class TesterClass {

	
	public static void main(String[] args) {
		
		MainScheduler scheduler = new MainScheduler();
		FloorsScheduler floorScheduler = new FloorsScheduler(scheduler);
		ElevatorScheduler elevatorScheduler = new ElevatorScheduler(scheduler);
		
		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
		Elevator elevator = new Elevator(elevatorScheduler);
		
		new Thread(floorSS,"FloorSS").start();
		elevator.powerOn();
	}
}
