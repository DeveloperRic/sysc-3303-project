package test.unitTestElevator;

import static org.junit.jupiter.api.Assertions.*;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import elevator.*;
import scheduler.ElevatorScheduler;

class ElevatorMotorUnitTests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_accelerate() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		ElevatorMotor em = new ElevatorMotor(elevator);
		float startVelocity = elevator.velocity;
		em.accelerate(elevator);
		float acceleratedVelocity = elevator.velocity;
		schedulerElevator.closeComms();
		assertTrue(acceleratedVelocity > startVelocity);

	}

	@Test
	void test_decelerate() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		ElevatorMotor em = new ElevatorMotor(elevator);
		float startVelocity = elevator.velocity;
		em.accelerate(elevator);
		float acceleratedVelocity = elevator.velocity;
		em.decelerate(elevator);
		float deceleratedVelocity = elevator.velocity;
		schedulerElevator.closeComms();
		assertTrue(startVelocity == deceleratedVelocity);
		assertTrue(acceleratedVelocity > deceleratedVelocity);

	}

	@Test
	void test_doMovement() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		ElevatorMotor em = new ElevatorMotor(elevator);
		float startTravel = elevator.getMetersTravelled();
		float velocity = 1;
		em.doMovement(elevator, velocity);
		float endTravel = elevator.getMetersTravelled();
		schedulerElevator.closeComms();
		assertTrue(endTravel > startTravel);
		assertEquals(endTravel, startTravel + velocity);

	}

	@Test
	void test_doMovementToFloor() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem subsystem = new ElevatorSubsystem(schedulerElevator);
		Elevator elevator = new Elevator(subsystem);
		ElevatorMotor em = new ElevatorMotor(elevator);
		float velocity = Elevator.FLOOR_HEIGHT;
		em.doMovement(elevator, velocity);
		float endTravel = elevator.getMetersTravelled();
		schedulerElevator.closeComms();
		assertEquals(0, endTravel);
	}

}
