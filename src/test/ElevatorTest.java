package test;

import util.Printer;

import org.junit.Test;

import elevator.ElevatorSubsystem;
import scheduler.ElevatorScheduler;
import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import scheduler.MainScheduler;

public class ElevatorTest {

	private static final int UP = 1;
	private static final int DOWN = -1;

	private boolean testing;

	@Test
	public void test() {

		boolean verbose = false;

		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(verbose); // code works now, no need for spam
		mainScheduler.activate();

		FloorsScheduler floorsScheduler = new FloorsScheduler();

		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler());
		subsystem.setVerbose(verbose);
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

//		List<Integer> workToDo = new ArrayList<>();

		new Thread(new Runnable() {

			@Override
			public void run() {
				floorsScheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return new Integer[] { 5, UP };
					}
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				subsystem.pressButton(9);

				floorsScheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return new Integer[] { 3, UP };
					}
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				subsystem.pressButton(1);

				floorsScheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return new Integer[] { 10, DOWN };
					}
				});

				if (verbose) {
					while (testing) {
						Printer.print("[Floor] " + floorsScheduler.get(null) + "\n");
					}
				}
			}
		}).start();

//		subsystem.assignTask(5);
//		subsystem.assignTask(9);
//		subsystem.assignTask(3);
//		subsystem.assignTask(10);
//		subsystem.assignTask(1);
//		subsystem.assignTask(7);
//		subsystem.assignTask(9);
//		subsystem.assignTask(12);
//		subsystem.assignTask(6);
//		subsystem.assignTask(5);
//		subsystem.assignTask(1);
//		
//		Printer.print("\n*** Assigned all tasks ***");
//		
//		Printer.print(subsystem.getWorkDoing().toString() + "\n");
//		

		// wait for elevator to return to sleep mode
		do {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		} while (subsystem.getElevator().isAwake());

		testing = false;

	}

//	private boolean canStopAtFloor(Elevator elevator, int floor) {
//		if (elevator.direction == 0)
//			return true;
//		if ((elevator.direction == -1 && floor > elevator.currentFloor)
//				|| (elevator.direction == 1 && floor < elevator.currentFloor)) {
//			return false;
//		}
//		if (floor < elevatorsFloorRequestBoundary[0][0] || floor > elevatorsFloorRequestBoundary[0][1]) {
//			return false;
//		}
//		if (!elevator.isMoving() || elevator.currentFloor == floor)
//			return true;
//
//		float distanceToFloor = Math.abs(floor - elevator.currentFloor) * Elevator.FLOOR_HEIGHT;
//		float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / elevator.velocity;
//		return secondsToFloor >= elevator.secondsToStop();
//	}

}
