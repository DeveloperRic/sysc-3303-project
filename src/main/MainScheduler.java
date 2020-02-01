/**
 * 
 */
package main;
import java.util.ArrayList;

/**
 * @author Kevin
 *
 */
public class MainScheduler {
	
	ArrayList<Object> elevatorMessageQueue;
	ArrayList<Object> floorMessageQueue;

	public MainScheduler() {
		elevatorMessageQueue = new ArrayList<>();
		floorMessageQueue = new ArrayList<>();
	}
	
	public synchronized Object floorGet() {
		while(floorMessageQueue.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler SENDING confirmation message to Floor\n Task Information : " + ((floorMessageQueue.get(0))).toString() + "\n");
		notifyAll();
		return floorMessageQueue.remove(0);
	}

	public synchronized boolean floorPut(Object o) {
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED task from Floor\n Task Information : " + ((Task)(o)).toString() + "\n");
		notifyAll();
		return elevatorMessageQueue.add(o);
	}

	public synchronized Object elevatorGet() {
		while(elevatorMessageQueue.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler SENDING task to Elevator\n Task Information : " + ((Task)(elevatorMessageQueue.get(0))).toString() + "\n");
		notifyAll();
		return elevatorMessageQueue.remove(0);
	}

	public synchronized boolean elevatorPut(Object o) {
		System.out.println("SCHEDULER SUBSYSTEM: Scheduler RECEIVED confirmation message from Elevator\n Task Information : " + o.toString() + "\n");
		notifyAll();
		return floorMessageQueue.add(o);
	}

}
