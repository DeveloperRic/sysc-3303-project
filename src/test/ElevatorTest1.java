package test;

import static org.junit.Assert.*;
import main.Elevator;
import main.MainScheduler;
import main.Scheduler;
import main.SchedulerElevator;

import org.junit.Test;

public class ElevatorTest1 {

	@Test
	public void test() {
		//fail("Not yet implemented");
		MainScheduler m = new MainScheduler();
		SchedulerElevator s = new SchedulerElevator(m);
		Elevator e = new Elevator(s);
		
		
		
		
	}

}
