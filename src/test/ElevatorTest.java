package test;

import static org.junit.Assert.*;

import java.util.Date;

import main.Elevator;
import main.Elevator.ElevatorState;
import main.MainScheduler;
import main.Scheduler;
import main.SchedulerElevator;
import main.Task;

import org.junit.Test;

public class ElevatorTest {
	
	static MainScheduler m = new MainScheduler();
	static SchedulerElevator s = new SchedulerElevator(m);
	
	
	@Test
	public void test() {
		//fail("Not yet implemented");

		Elevator e = new Elevator(s);
		e.powerOn();
		
		

		ElevatorState state = e.getState();
		
		
		System.out.println("\nAssigning a task");
		state.assignTask(new Task("14:05:15.0", "2", "1", "4"));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task("14:13:56.04", "1", "-1", "3"));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task("14:13:56.06", "1", "-1", "3"));
		
		while(true){
			
			
		}
		
	}

}
