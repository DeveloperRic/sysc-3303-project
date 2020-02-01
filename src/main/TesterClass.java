package main;
public class TesterClass {

	
	public static void main(String[] args) {
		
		MainScheduler scheduler = new MainScheduler();
		SchedulerFloors floorScheduler = new SchedulerFloors(scheduler);
		SchedulerElevator elevatorScheduler = new SchedulerElevator(scheduler);
		
		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
		Elevator elevator = new Elevator(elevatorScheduler);
		
		new Thread(floorSS,"FloorSS").start();
		elevator.powerOn();
	}
}
