package test;

import main.Elevator;
import main.Elevator.ElevatorState;
import scheduler.MainScheduler;
import scheduler.ElevatorScheduler;
import main.Task;

import org.junit.Test;

public class ElevatorTest {
	
	static MainScheduler m = new MainScheduler();
	static ElevatorScheduler s = new ElevatorScheduler(m);
	
	
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
