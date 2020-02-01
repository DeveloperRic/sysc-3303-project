package test;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;
import main.MainScheduler;
import main.Scheduler;
import main.SchedulerElevator;
import main.SchedulerFloors;
import main.Task;

public class SchedulerTest extends TestCase {

	public void testMainElevatorPutFloorGet() {
		String s = "Hello";
		MainScheduler m = new MainScheduler();
		m.elevatorPut(s);
		assertEquals(s, m.floorGet());
	}
	
	public void testMainFloorPutElevatorGet() {
		String s = "Hello";
		MainScheduler m = new MainScheduler();
		m.floorPut(s);
		assertEquals(s, m.elevatorGet());
	}
	
	public void testMainElevatorPutFloorGetTask() {
		Task t = new Task("14:05:15.0", "2", "Up", "4");
		MainScheduler m = new MainScheduler();
		m.elevatorPut(t);
		assertEquals(t, m.floorGet());
	}
	
	public void testMainFloorPutElevatorGetTask() {
		Task t = new Task("14:05:15.0", "2", "Up", "4");
		MainScheduler m = new MainScheduler();
		m.floorPut(t);
		assertEquals(t, m.elevatorGet());
	}
	
	public void testProxyElevatorPutFloorGet() {
		String s = "Hello";
		MainScheduler m = new MainScheduler();
		Scheduler se = new SchedulerElevator(m);
		Scheduler sf = new SchedulerFloors(m);
		se.put(s);
		assertEquals(s, sf.get());
	}
	
	public void testProxyFloorPutGet() {
		String s = "Hello";
		MainScheduler m = new MainScheduler();
		Scheduler se = new SchedulerElevator(m);
		Scheduler sf = new SchedulerFloors(m);
		sf.put(s);
		assertEquals(s, se.get());
	}
	
	public void test() {
		
		MainScheduler m = new MainScheduler();
		
		String[] elevator = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
		int[] floor = {1,2,3,4,5,6,7,8,9,10};
		
		AtomicBoolean b1 = new AtomicBoolean(false);
		AtomicBoolean b2 = new AtomicBoolean(false);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Scheduler se = new SchedulerElevator(m);
				for(int i = 0; i < elevator.length; i++) {
					se.put(elevator[i]);
					assertEquals(floor[i], se.get());
					b1.set(true);
				}
			}
		}).start();

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Scheduler sf = new SchedulerFloors(m);
				for(int i = 0; i < elevator.length; i++) {
					sf.put(floor[i]);
					assertEquals(elevator[i], sf.get());
					b2.set(true);
				}
			}
		}).start();
		
		while(!b1.get() || !b2.get()) {
			
		}
	}
	
	

}
