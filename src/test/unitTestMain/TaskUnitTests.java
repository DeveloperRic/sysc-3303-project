package test.unitTestMain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.Task;

public class TaskUnitTests {
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void test_deserializeTask() {
		Task task = new Task("","3","Up",false);
		LocalTime time = null;
		int startFloor = 3;
		int direction = 1;
		
		assertTrue(time == task.getRequestTime());
		assertTrue(startFloor == task.getStartFloor());
		assertTrue(direction == task.getDirection());
		
		byte[] taskBytes = task.serialize();
		task = Task.deserialize(taskBytes);

		assertTrue(startFloor == task.getStartFloor());
		assertTrue(direction == task.getDirection());
	}
	
	@Test
	void test_deserializeFault() {
		
		Task task = new Task("14:05:16.0","-1","1",true);
		task.setTimeDifference(5);
		
		LocalTime time = LocalTime.parse("14:05:16.0");
		int faultNumber = -1;
		int elevatorNumber = 1;
		int timeDifference = 5;
		
		assertTrue(time.equals(task.getRequestTime()));
		assertTrue(faultNumber == task.getFaultNumber());
		assertTrue(elevatorNumber == task.getElevatorNumber());
		assertTrue(timeDifference == task.getTimeDifference());
		
		byte[] taskBytes = task.serialize();
		task = Task.deserialize(taskBytes);
			
		assertTrue(time.equals(task.getRequestTime()));
		assertTrue(faultNumber == task.getFaultNumber());
		assertTrue(elevatorNumber == task.getElevatorNumber());
		assertTrue(timeDifference == task.getTimeDifference());
	}
}
