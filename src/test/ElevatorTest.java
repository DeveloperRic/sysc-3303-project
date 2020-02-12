package test;

import org.junit.Test;

import main.MainScheduler;
import main.SchedulerElevator;
import main.Task;
import main.elevator.ElevatorState;
import main.elevator.ElevatorSubsystem;

public class ElevatorTest {
	
	static MainScheduler m = new MainScheduler();
	static SchedulerElevator s = new SchedulerElevator(m);
	
	
	@Test
	public void test() {

		ElevatorSubsystem elevator = new ElevatorSubsystem(s);
		elevator.powerOn();


		ElevatorState state = elevator.getState();
		
		
		System.out.println("\nAssigning a task");
		state.assignTask(new Task("14:05:15.0", "2", "1", "4"));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task("14:13:56.04", "1", "-1", "3"));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task("14:13:56.06", "1", "-1", "3"));

		
		// wait for elevator to return to sleep mode
		while(state.isAwake()){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
		}
		
	}

}
