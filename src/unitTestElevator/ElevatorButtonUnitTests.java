package unitTestElevator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevator.ElevatorButton;
import elevator.ElevatorSubsystem;
import scheduler.ElevatorScheduler;
import scheduler.MainScheduler;

class ElevatorButtonUnitTests {

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_pressButton() { 
		MainScheduler ms = new MainScheduler();
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		ElevatorButton elevatorButton = new ElevatorButton(subsystem, 0);
		ms.activate();
		elevatorButton.press();
		assertTrue(elevatorButton.isPressed());
		ms.closeComms();
		schedulerElevator.closeComms();
	}
	
	@Test
	void test_unpressButton() {
		MainScheduler ms = new MainScheduler();
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		ElevatorButton elevatorButton = new ElevatorButton(subsystem, 0);
		ms.activate();
		elevatorButton.unpress();
		assertFalse(elevatorButton.isPressed());
		ms.closeComms();
		schedulerElevator.closeComms();
	}

}
