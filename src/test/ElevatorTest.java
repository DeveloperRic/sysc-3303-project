package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import main.MainScheduler;
import main.SchedulerElevator;
import main.elevator.Elevator;
import main.elevator.ElevatorSubsystem;

public class ElevatorTest {

	MainScheduler mainScheduler = new MainScheduler();
	SchedulerElevator schedulerElevator = new SchedulerElevator(mainScheduler);
	
	int[][] elevatorsFloorRequestBoundary = new int[1][2];

	@Test
	public void test() {

		// FOLLOWING CODE IS BROKEN NOW

//		ElevatorSubsystem elevator = new ElevatorSubsystem(s);
//		elevator.powerOn();
//
//		Elevator state = elevator.getElevator();
//
//		System.out.println("\nAssigning a task");
//		state.assignTask(new Task("14:05:15.0", "2", "1", "4"));
//		System.out.println("\nAssigning a task");
//		state.assignTask(new Task("14:13:56.04", "1", "-1", "3"));
//		System.out.println("\nAssigning a task");
//		state.assignTask(new Task("14:13:56.06", "1", "-1", "3"));
//

		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		subsystem.powerOn();

		/*
		 * ************************************************* if there's a way to go up, go up first
		 * 1 | floor 5 ^ | elev 9 | floor 3 ^ | floor 10 v | elev 1 |
		 * 
		 * [ 9 ]
		 * 
		 * maxFloor = 9
		 */

//		List<Integer> workToDo = new ArrayList<>();

		subsystem.assignTask(5);
		subsystem.assignTask(9);
		subsystem.assignTask(3);
		subsystem.assignTask(10);
		subsystem.assignTask(1);
		subsystem.assignTask(7);
		subsystem.assignTask(9);
		subsystem.assignTask(12);
		subsystem.assignTask(6);
		subsystem.assignTask(5);
		subsystem.assignTask(1);
		
		System.out.println("\n*** Assigned all tasks ***");
		
		System.out.println(subsystem.getWorkDoing().toString() + "\n");
		
		// wait for elevator to return to sleep mode
		while (subsystem.getElevator().isAwake()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}

	}

	private boolean canStopAtFloor(Elevator elevator, int floor) {
		if (elevator.direction == 0)
			return true;
		if ((elevator.direction == -1 && floor > elevator.currentFloor)
				|| (elevator.direction == 1 && floor < elevator.currentFloor)) {
			return false;
		}
		if (floor < elevatorsFloorRequestBoundary[0][0] || floor > elevatorsFloorRequestBoundary[0][1]) {
			return false;
		}
		if (!elevator.isMoving() || elevator.currentFloor == floor)
			return true;

		float distanceToFloor = Math.abs(floor - elevator.currentFloor) * Elevator.FLOOR_HEIGHT;
		float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / elevator.velocity;
		return secondsToFloor >= elevator.secondsToStop();
	}

}
