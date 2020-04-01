package test.unitTestElevator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevator.ElevatorLamp;

class ElevatorLampUnitTests {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_lampOn() {
		ElevatorLamp el = new ElevatorLamp();
		el.turnOn();
		assertTrue(el.isIlluminated());
	}

	@Test
	void test_lampOff() {
		ElevatorLamp el = new ElevatorLamp();
		el.turnOn();
		el.turnOff();
		assertFalse(el.isIlluminated());
	}

}
