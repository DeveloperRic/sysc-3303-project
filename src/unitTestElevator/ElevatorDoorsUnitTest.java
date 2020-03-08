package unitTestElevator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import elevator.*;
import scheduler.ElevatorScheduler;

class ElevatorDoorsUnitTest {
	
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_doorsOpen() {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		ElevatorDoors ed = new ElevatorDoors(elevator);
		ed.openDoors();
		assertTrue(ed.isOpen());
		schedulerElevator.closeComms();
	}
	
	@Test
	void test_doorsClosed() {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		ElevatorDoors ed = new ElevatorDoors(elevator);
		ed.closeDoors();
		assertFalse(ed.isOpen());
		schedulerElevator.closeComms();
	}

}
