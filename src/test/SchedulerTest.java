package test;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import junit.framework.TestCase;
import main.FloorSubsystem;
import scheduler.ElevatorMessage;
import scheduler.ElevatorScheduler;
import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import scheduler.MainScheduler;
import util.Printer;

/**
 * Unit tests for the Scheduler classes
 * 
 * @author Kevin
 *
 */
public class SchedulerTest extends TestCase {

	/**
	 * Tests the main scheduler elevator put and floor get methods with an integer
	 * 
	 */
	public void testMainElevatorPutFloorRequest() {
		MainScheduler scheduler = new MainScheduler();
		FloorsScheduler floorScheduler = new FloorsScheduler(-1);
		ElevatorScheduler elevatorScheduler = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(elevatorScheduler);
		
		subsystem.powerOn();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Printer.print("[*Test] Floor 5 request to go UP");
				//floorScheduler.put(new Integer[] {5, 1});
				floorScheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return new Integer[] {5, 1};
					}
				});
			}
		}).start();
		

//		assertEquals(true, m.isFloorRequest);
//		
//		Printer.print("[*Test] Elevator workDoing = " + subsystem.workDoing.toString());
		
		while (subsystem.getElevator().isAwake()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}

	}

	/**
	 * Tests the main scheduler elevator put and floor get methods with a string
	 */
//	public void testMainElevatorPutAcknowledgement() {
//		String s = "Arrived!";
//		MainScheduler m = new MainScheduler();
//		ElevatorMessage em = new ElevatorMessage() {
//
//			// Some Messages
//			public String getAcknowledgement() {
//				return s;
//			}
//
//		};
//		m.elevatorPut(em);
//		//assertEquals(s, m.floorCommunication.aGet());
//		assertEquals(true, m.isElevatorAck);
//	}

	/**
	 * Tests the main scheduler elevator put and floor get methods with a string
	 */
//	public void testMainElevatorPutElevatorStatusUpdate() {
//		int floor = 5;
//		float velocity = 1;
//		int direction = 1;
//		MainScheduler m = new MainScheduler();
//
//		ElevatorStatusUpdate esu = new ElevatorStatusUpdate() {
//
//			@Override
//			public int direction() {
//				// TODO Auto-generated method stub
//				return direction;
//			}
//
//			@Override
//			public int currentFloor() {
//				// TODO Auto-generated method stub
//				return floor;
//			}
//
//			@Override
//			public float velocity() {
//				// TODO Auto-generated method stub
//				return velocity;
//			}
//
//			@Override
//			public boolean isSleeping() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		};
//
//		ElevatorMessage em = new ElevatorMessage() {
//
//			public ElevatorStatusUpdate getStatusUpdate() {
//				return esu;
//			}
//
//		};
//
//		m.elevatorPut(em);
//
//		assertEquals(true, m.isElevatorUpdate);
//		// assertEquals(s, m.floorGet());
//	}

	/**
	 * Tests the main scheduler floor put and elevator get methods with an integer
	 * array
	 */
//	public void testMainFloorPutElevatorGet() {
//		Integer[] i = { 5, 1 };
//		MainScheduler m = new MainScheduler();
//		m.floorPut(i);
//		// assertEquals(1, m.elevatorGet().toString());
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
	 * Tests the main scheduler elevator put and floor get methods with a Task
	 * object
	 */
//	public void testMainElevatorPutFloorGetTask() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		m.elevatorPut(t);
//		assertEquals(t, m.floorGet());
//	}

	/**
	 * Tests the main scheduler floor put and elevator get methods with a Task
	 */
//	public void testMainFloorPutElevatorGetTask() {
//		Task t = new Task("14:05:15.0", "2", "Up", "4");
//		MainScheduler m = new MainScheduler();
//		m.floorPut(t);
//		assertEquals(t, m.elevatorGet());
//	}

	/**
	 * Tests the proxy classes SchedulerElevator put and SchedulerFloor get with a
	 * string object
	 */
//	public void testProxyElevatorPutFloorGet() {
//		String s = "Hello";
//		MainScheduler m = new MainScheduler();
//		SchedulerType se = new ElevatorScheduler(m);
//		SchedulerType sf = new FloorsScheduler(m);
//		
//		ElevatorMessage em = new ElevatorMessage(){
//			
//			public String getAcknowledgement(){
//				return s;
//			}
//			
//		};
//		
//		se.put(em);
//		assertEquals(s, sf.get());
//	}

	/**
	 * Tests the proxy classes SchedulerFloor put and SchedulerElevator get with a
	 * string object
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
	 * Simulates two threads communicating with each other in a shared memory space.
	 * 
	 * One thread passes strings to the second thread and the second thread passes
	 * integers to the second thread
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
//					Printer.print("HERE1");
//					se.get();
//					Printer.print("HERE2");
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
