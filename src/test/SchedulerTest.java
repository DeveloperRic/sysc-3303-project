package test;

import static org.junit.Assert.assertArrayEquals;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;
import main.Task;
import scheduler.ElevatorScheduler.ElevatorMessage;
import scheduler.ElevatorScheduler.ElevatorStatusUpdate;
import scheduler.MainScheduler;
import scheduler.SchedulerType;
import scheduler.ElevatorScheduler;
import scheduler.FloorsScheduler;

/**
 * Unit tests for the Scheduler classes
 * 
 * @author Kevin
 *
 */
public class SchedulerTest extends TestCase {

	/**
	 * Tests the main scheduler elevator put and floor get methods with a string
	 */
	public void testMainElevatorPutFloorGet() {
		String s = "Hello";
		MainScheduler m = new MainScheduler();
		ElevatorMessage em = new ElevatorMessage(){
			
			//Some Messages
			public String getAcknowledgement(){
				return s;
			}
			
		};
		m.elevatorPut(em);
		assertEquals(s, m.floorGet());
	}

	/**
	 * Tests the main scheduler elevator put and floor get methods with a float
	 * array
	 */
	public void testMainElevatorPutFloorGetInteger() {
//		Integer[] s = { 1, 2, 1 };
		int i = 5;
		MainScheduler m = new MainScheduler();
		assertEquals(0, m.elevatorStatus.getDirection());
		assertEquals(0, m.elevatorStatus.getVelocity(), 0);
		assertEquals(0, m.elevatorStatus.getCurrentFloor());

		
		ElevatorMessage em = new ElevatorMessage(){
			
			//Some Messages
			public Integer getFloorRequest(){
				return i;
			}
			
		};
		
		m.elevatorPut(em);
		
		assertEquals(1, m.elevatorStatus.workDoing.size());
		
		//Should we check the pending request queue? But queue does not public and ambiguous
		
//		assertEquals(1, m.elevatorStatus.getDirection());
//		assertEquals(2, m.elevatorStatus.getVelocity(), 0);
//		assertEquals(1, m.elevatorStatus.getCurrentFloor());
	}
	
//	
//	/**
//	 * Tests the main scheduler elevator put and floor get methods with a string
//	 */
//	public void testMainElevatorPutFloorGetStatusUpdate() {
//		String s = "Hello";
//		
//		ElevatorStatusUpdate esu = new ElevatorStatusUpdate(){
//			public int direction(){
//				return 1;
//			}
//
//			@Override
//			public int currentFloor() {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//
//			@Override
//			public float velocity() {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//
//			@Override
//			public boolean isSleeping() {
//				// TODO Auto-generated method stub
//				return false;
//			};
//		};
//		
//		MainScheduler m = new MainScheduler();
//		ElevatorMessage em = new ElevatorMessage(){
//			
//			//Some Messages
//			public ElevatorStatusUpdate getStatusUpdate(){
//				return esu;
//			}
//			
//		};
//		m.elevatorPut(em);
//		assertEquals(s, m.floorGet());
//	}
//
//	/**
//	 * Tests the main scheduler floor put and elevator get methods with a string
//	 */
//	public void testMainFloorPutElevatorGet() {
////		String s = "Hello";
//		Integer[] arr = {4 , 1};
//		MainScheduler m = new MainScheduler();
//		m.floorPut(arr);
//		
//		assertEquals(1, m.elevatorStatus.workDoing.size());
//		
//		//Check Pending Request Queue? But Pending Request Queue is not public and ambiguous
////		assertEquals(arr, m.elevatorGet());
//	}
//
//	/**
//	 * Tests the main scheduler floor put and elevator get methods with a string
//	 */
//	public void testMainFloorPutElevatorGetStuff() {
////		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		Integer[] arr = {2 , 1};
//		MainScheduler m = new MainScheduler();
//		
//		assertEquals(0, m.elevatorStatus.workDoing.size());
////		assertEquals(0, m.floorMessageQueue.size());
//		
//		m.floorPut(arr);
//		
//		assertEquals(1, m.elevatorStatus.workDoing.size());
////		assertEquals(1, m.floorMessageQueue.size());
//	}

	/**
	 * Tests the main scheduler elevator put and floor get methods with a Task
	 * object
	 */
//	public void testMainElevatorPutFloorGetTask() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		m.elevatorPut(t);
//		assertEquals(t, m.floorGet());
//	}
//
//	/**
//	 * Tests the main scheduler floor put and elevator get methods with a Task
//	 */
//	public void testMainFloorPutElevatorGetTask() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		m.floorPut(t);
//		assertEquals(t, m.elevatorGet());
//	}

//	/**
//	 * Tests the proxy classes SchedulerElevator put and SchedulerFloor get with a
//	 * string object
//	 */
//	public void testProxyElevatorPutFloorGet() {
//		String s = "Hello";
//		MainScheduler m = new MainScheduler();
//		SchedulerType se = new ElevatorScheduler(m);
//		SchedulerType sf = new FloorsScheduler(m);
//		
//		ElevatorMessage em = new ElevatorMessage(){
//			
//			//Some Messages
//			public String getAcknowledgement(){
//				return s;
//			}
//			
//		};
//		
//		se.put(em);
//		assertEquals(s, sf.get());
//	}

//	/**
//	 * Tests the proxy classes SchedulerFloor put and SchedulerElevator get with a
//	 * string object
//	 */
//	public void testProxyFloorPutGet() {
////		String s = "Hello";
//		Integer[] i = {7, 1};
//		MainScheduler m = new MainScheduler();
//		SchedulerType se = new ElevatorScheduler(m);
//		SchedulerType sf = new FloorsScheduler(m);
//		
//		sf.put(i);
//		
////		assertEquals(0, se.get());
//	}

//	/**
//	 * Simulates two threads communicating with each other in a shared memory space.
//	 * 
//	 * One thread passes strings to the second thread and the second thread passes
//	 * integers to the second thread
//	 */
//	public void test() {
//
//		MainScheduler m = new MainScheduler();
//
//		// messages to pass from thread one to thread two
//		String[] elevator = { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten" };
//
//		// messages to pass from thread two to thread one
//		Integer[] floor = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
//
//		AtomicBoolean b1 = new AtomicBoolean(false);
//		AtomicBoolean b2 = new AtomicBoolean(false);
//
//		// thread one
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType se = new ElevatorScheduler(m);
//				for (int i = 0; i < elevator.length; i++) {
//
//					// sends a string to the other thread
//					se.put(elevator[i]);
//
//					// checks if what the other thread sent is what it received
//					assertEquals(floor[i], se.get());
//				}
//				b1.set(true);
//			}
//		}).start();
//
//		// thread two
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType sf = new FloorsScheduler(m);
//				for (int i = 0; i < elevator.length; i++) {
//
//					// sends an integer to the other thread
//					sf.put(floor);
//
//					// checks if what the other thread sent is what it received
//					assertEquals(elevator[i], sf.get());
//				}
//				b2.set(true);
//			}
//		}).start();
//
//		// Waits until both threads finished executing
//		while (!b1.get() || !b2.get()) {
//
//		}
//	}

//	public void test2() {
//
//		MainScheduler m = new MainScheduler();
//
//		Task t1 = new Task("14:05:15.0", "2", "Up", "4");
//		Task t2 = new Task("14:05:15.0", "2", "Up", "4");
//		Task t3 = new Task("14:05:15.0", "2", "Up", "4");
//		Task t4 = new Task("14:05:15.0", "2", "Up", "4");
//		Task t5 = new Task("14:05:15.0", "2", "Up", "4");
//
//		AtomicBoolean b1 = new AtomicBoolean(false);
//		AtomicBoolean b2 = new AtomicBoolean(false);
//
//		// thread one
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType se = new ElevatorScheduler(m);
//
//				for (int i = 0; i < 5; i++) {
//					System.out.println("HERE1");
//					se.get();
//					System.out.println("HERE2");
//				}
//
//				b1.set(true);
//			}
//		}).start();
//
//		// thread two
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType sf = new FloorsScheduler(m);
//				Integer[] arr = new Integer[] {3,1};
//				Integer[] arr2 = new Integer[] {4,1};
//				// sends an integer to the other thread
//				sf.put(arr);
////				sf.put(arr2);
////				sf.put(arr);
////				sf.put(arr);
////				sf.put(arr);
//
//				b2.set(true);
//			}
//		}).start();
//
//		// Waits until both threads finished executing
//		while (!b1.get() || !b2.get()) {
//
//		}
//	}

}
