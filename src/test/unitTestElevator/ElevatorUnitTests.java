package test.unitTestElevator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import scheduler.ElevatorScheduler;

class ElevatorUnitTests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_timeToStop() {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		int floor = 3;
		int direction = 1;
		elevator.direction = direction;
		float distance = Math.abs(elevator.currentFloor - floor) * Elevator.FLOOR_HEIGHT;
		schedulerElevator.closeComms();
		assertEquals(distance / elevator.velocity, elevator.timeToStopAtFloor(floor, direction));
	}

	@Test
	void test_secondsToStop() {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		elevator.velocity = 10;
		schedulerElevator.closeComms();
		assertEquals(elevator.velocity / Elevator.ACCELERATION, elevator.secondsToStop());
	}

}
