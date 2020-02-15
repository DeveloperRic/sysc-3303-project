package test;

import static org.junit.Assert.assertArrayEquals;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;
//import main.MainScheduler;
//import main.Scheduler;
//import main.SchedulerElevator;
//import main.SchedulerFloors;
import main.Task;

import scheduler.ElevatorScheduler.ElevatorMessage;
import scheduler.ElevatorScheduler.ElevatorStatusUpdate;
import scheduler.MainScheduler;
import scheduler.SchedulerType;
import scheduler.ElevatorScheduler;
import scheduler.ElevatorScheduler.ElevatorMessage;
import scheduler.FloorsScheduler;

/**
 * Unit tests for the Scheduler classes
 * 
 * @author Kevin
 *
 */
public class SchedulerTest extends TestCase {

	/**
	 * Tests the main scheduler
	 * elevator put and floor get methods
	 * with a string
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

//		assertEquals(1, m.currentElevatorDirection);
//		assertEquals(2, m.currentElevatorVelocity, 0);
//		assertEquals(1, m.currentElevatorFloor);
	}


	/**
	 * Tests the main scheduler
	 * floor put and elevator get methods
	 * with a string
	 */
//	public void testMainFloorPutElevatorGet() {
//		String s = "Hello";
//		MainScheduler m = new MainScheduler();
//		m.floorPut(s);
//		assertEquals(s, m.elevatorGet());
//	}
	
	/**
	 * Tests the main scheduler floor put and elevator get methods with a string
	 */
//	public void testMainFloorPutElevatorGetStuff() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		assertEquals(0, m.elevatorWorkDoing.size());
//		assertEquals(0, m.floorMessageQueue.size());
//		m.floorPut(t);
//		assertEquals(1, m.elevatorWorkDoing.size());
//		assertEquals(1, m.floorMessageQueue.size());
//	}

	/**
	 * Tests the main scheduler
	 * elevator put and floor get methods
	 * with a Task object
	 */
//	public void testMainElevatorPutFloorGetTask() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		m.elevatorPut(t);
//		assertEquals(t, m.floorGet());
//	}
	
	/**
	 * Tests the main scheduler
	 * floor put and elevator get methods
	 * with a Task
	 */
//	public void testMainFloorPutElevatorGetTask() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		m.floorPut(t);
//		assertEquals(t, m.elevatorGet());
//	}
	
	/**
	 * Tests the proxy classes 
	 * SchedulerElevator put and SchedulerFloor get
	 * with a string object 
	 */
//	public void testProxyElevatorPutFloorGet() {
//		String s = "Hello";
//		MainScheduler m = new MainScheduler();
//		SchedulerType se = new ElevatorScheduler(m);
//		SchedulerType sf = new FloorsScheduler(m);
//		se.put(s);
//		assertEquals(s, sf.get());
//	}
	
	/**
	 * Tests the proxy classes 
	 * SchedulerFloor put and SchedulerElevator get
	 * with a string object 
	 */
//	public void testProxyFloorPutGet() {
//		String s = "Hello";
//		MainScheduler m = new MainScheduler();
//		SchedulerType se = new ElevatorScheduler(m);
//		SchedulerType sf = new FloorsScheduler(m);
//		sf.put(s);
//		assertEquals(s, se.get());
//	}
	
	/**
	 * Simulates two threads communicating with each other
	 * in a shared memory space.
	 * 
	 *  One thread passes strings to the second thread
	 *  and the second thread passes integers to the second thread
	 */
//	public void test() {
//		
//		MainScheduler m = new MainScheduler();
//		
//		//messages to pass from thread one to thread two
//		String[] elevator = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
//		
//		//messages to pass from thread two to thread one
//		int[] floor = {1,2,3,4,5,6,7,8,9,10};
//		
//		AtomicBoolean b1 = new AtomicBoolean(false);
//		AtomicBoolean b2 = new AtomicBoolean(false);
//		
//		//thread one
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType se = new ElevatorScheduler(m);
//				for(int i = 0; i < elevator.length; i++) {
//					
//					//sends a string to the other thread
//					se.put(elevator[i]);
//					
//					//checks if what the other thread sent is what it received
//					assertEquals(floor[i], se.get());
//				}
//				b1.set(true);
//			}
//		}).start();
//
//		//thread two
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType sf = new FloorsScheduler(m);
//				for(int i = 0; i < elevator.length; i++) {
//					
//					//sends an integer to the other thread
//					sf.put(floor[i]);
//					
//					//checks if what the other thread sent is what it received
//					assertEquals(elevator[i], sf.get());
//				}
//				b2.set(true);
//			}
//		}).start();
//		
//		//Waits until both threads finished executing
//		while(!b1.get() || !b2.get()) {
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
//
//		AtomicBoolean b1 = new AtomicBoolean(false);
//		AtomicBoolean b2 = new AtomicBoolean(false);
//
//		//thread one
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType se = new ElevatorScheduler(m);
//				
//				for(int i = 0; i < 5; i++) {
//					System.out.println("HERE1");
//					se.get();
//					System.out.println("HERE2");
//				}
//
//				b1.set(true);
//			}
//		}).start();
//
//		//thread two
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				SchedulerType sf = new FloorsScheduler(m);
//
//					//sends an integer to the other thread
//					sf.put(t1);
//					sf.put(t2);
//					sf.put(t3);
//					sf.put(t4);
//					sf.put(t5);
//
//				b2.set(true);
//			}
//		}).start();
//
//		//Waits until both threads finished executing
//		while(!b1.get() || !b2.get()) {
//
//		}
//	}
}
