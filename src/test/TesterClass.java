package test;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import main.FloorSubsystem;
import scheduler.FloorRequest;
import scheduler.MainScheduler;
import scheduler.ElevatorScheduler;
import scheduler.FloorsScheduler;
import util.Transport;

public class TesterClass {

	
	public static void main(String[] args) {
		
		//System.out.println("Testing");
		
		//InputParser inputParser = new InputParser("\\src\\assets\\Inputs.txt");
		//inputParser.printTasks();
		//System.out.println("");
		//inputParser.printTimeRequests();
		
//		MainScheduler scheduler = new MainScheduler();
//		FloorsScheduler floorScheduler = new FloorsScheduler();
//		ElevatorScheduler elevatorScheduler = new ElevatorScheduler();
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorScheduler);
//		
//		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
//		
//		new Thread(floorSS,"FloorSS").start();
//		elevatorSubsystem.powerOn();
		
		
		//Active Main Scheduler
		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(true); // code works now, no need for spam
		mainScheduler.activate();
		
		
		//Power on elevator subsystem
		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler(1));
		ElevatorSubsystem.setVerbose(true);
		subsystem.powerOn();
		
		
		//Active floor scheduler
		Transport.setVerbose(true);
		FloorsScheduler floorScheduler = new FloorsScheduler();

		
		//Delay
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
		
		new Thread(floorSS,"FloorSS").start();
		
		
	}
}
