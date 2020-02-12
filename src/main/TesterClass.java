package main;

import main.elevator.ElevatorSubsystem;

public class TesterClass {

	
	public static void main(String[] args) {
		
		MainScheduler scheduler = new MainScheduler();
		SchedulerFloors floorScheduler = new SchedulerFloors(scheduler);
		SchedulerElevator elevatorScheduler = new SchedulerElevator(scheduler);
		
		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
		ElevatorSubsystem elevator = new ElevatorSubsystem(elevatorScheduler);
		
		new Thread(floorSS,"FloorSS").start();
		elevator.powerOn();
	}
}
