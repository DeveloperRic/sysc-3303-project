package test;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import main.FloorSubsystem;
import scheduler.MainScheduler;
import scheduler.ElevatorScheduler;
import scheduler.FloorsScheduler;

public class TesterClass {

	
	public static void main(String[] args) {
		
		//System.out.println("Testing");
		
		//InputParser inputParser = new InputParser("\\src\\assets\\Inputs.txt");
		//inputParser.printTasks();
		//System.out.println("");
		//inputParser.printTimeRequests();
		
		MainScheduler scheduler = new MainScheduler();
		FloorsScheduler floorScheduler = new FloorsScheduler(scheduler);
		ElevatorScheduler elevatorScheduler = new ElevatorScheduler(scheduler);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorScheduler);
		
		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
//		Elevator elevator = new Elevator(elevatorSubsystem);
		
		new Thread(floorSS,"FloorSS").start();
		elevatorSubsystem.powerOn();
	}
}
