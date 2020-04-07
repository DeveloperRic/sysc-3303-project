package test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import elevator.ElevatorSubsystem;
import junit.framework.TestCase;
import main.Task;
import scheduler.ElevatorScheduler;
import scheduler.FloorMessage;
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
	 * @throws SocketException
	 * @throws UnknownHostException
	 * 
	 */
	public void testMainElevatorPutFloorRequest() throws UnknownHostException, SocketException {
		MainScheduler scheduler = new MainScheduler();

		scheduler.activate();

		FloorsScheduler floorScheduler = new FloorsScheduler(-1);
		ElevatorScheduler elevatorScheduler = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(elevatorScheduler);

		subsystem.powerOn();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Printer.print("[*Test] Floor 5 request to go UP");
				try {
					floorScheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return new Integer[] { 5, 1 };
						}
						
						@Override
						public Task getTask() {
							return null;
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		while (subsystem.getElevator().isAwake()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

	}
}
