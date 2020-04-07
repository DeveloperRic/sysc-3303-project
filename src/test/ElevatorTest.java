package test;

import util.Printer;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

import elevator.ElevatorSubsystem;
import main.Task;
import scheduler.ElevatorScheduler;
import scheduler.FloorMessage;
import scheduler.FloorsScheduler;
import scheduler.MainScheduler;

public class ElevatorTest {

	private static final int UP = 1;
	private static final int DOWN = -1;

	private boolean testing;

	@Test
	public void test() throws SocketException, UnknownHostException {

		boolean verbose = false;

		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(verbose); // code works now, no need for spam
		mainScheduler.activate();

		FloorsScheduler floorsScheduler = new FloorsScheduler(-1); // just one floor for now, later there will be more

		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler(1));
		ElevatorSubsystem.setVerbose(verbose);
		subsystem.powerOn();

		testing = true;

		/*
		 * ************************************************* if there's a way to go up,
		 * go up first| 1 | floor 5 ^ | elev 9 | floor 3 ^ | elev 1 | floor 10 v |
		 * 
		 * [ 9 ]
		 * 
		 * assignment: expected [5 9 3 1 10] actual [5 9 3 1 10]
		 * 
		 * arrival: expected [3 5 9 10 1] actual [3 5 9 10 1]
		 * 
		 * The above results may look weird (you'd expect the elevator to go to 1, then
		 * 10 because (1) asked first), however, the distance between 9<->1 is greater
		 * than 9<->10, so in order to increase throughput, the elevator goes up first,
		 * then down (to maximize number of passengers in the elevator at a time)
		 * 
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					floorsScheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return new Integer[] { 5, UP };
						}
						
						@Override
						public Task getTask() {
							return null;
						}
					});

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					subsystem.pressButton(9);

					floorsScheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return new Integer[] { 3, UP };
						}
						
						@Override
						public Task getTask() {
							return null;
						}
					});

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					subsystem.pressButton(3);

					floorsScheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return new Integer[] { 10, DOWN };
						}
						
						@Override
						public Task getTask() {
							return null;
						}
					});

					if (verbose) {
						while (testing) {
							Printer.print("[Floor] " + floorsScheduler.get(null) + "\n");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		// wait for elevator to return to sleep mode
		do {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		} while (subsystem.getElevator().isAwake());

		testing = false;

	}
}
