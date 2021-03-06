package test.unitTestElevator;

import static org.junit.jupiter.api.Assertions.*;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevator.ElevatorSubsystem;
import scheduler.ElevatorScheduler;
import scheduler.MainScheduler;

class ElevatorSubsystemUnitTests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_assignTask() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem es = new ElevatorSubsystem(schedulerElevator);
		int floor = 1;
		es.assignTask(floor);
		schedulerElevator.closeComms();
		assertEquals(floor, es.getFirstWorkDoing());
	}

	@Test
	void test_powerOn() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem es = new ElevatorSubsystem(schedulerElevator);
		es.powerOn();
		schedulerElevator.closeComms();
		assertTrue(es.isPoweredOn());
	}

	@Test
	void test_powerOff() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		ElevatorSubsystem es = new ElevatorSubsystem(schedulerElevator);
		es.powerOff();
		schedulerElevator.closeComms();
		assertFalse(es.isPoweredOn());
	}

	@Test
	void test_pressButton() throws UnknownHostException, SocketException {
		ElevatorScheduler schedulerElevator = new ElevatorScheduler(1);
		MainScheduler ms = new MainScheduler();
		ms.activate();
		ElevatorSubsystem es = new ElevatorSubsystem(schedulerElevator);
		es.pressButton(1);
		schedulerElevator.closeComms();
		assertTrue(es.isButtonPressed(1));
	}

}
