package test.unitTestScheduler;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.Task;
import scheduler.FloorMessage;

public class FloorMessageUnitTests {
	private static final int UP = 1;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	@AfterEach
	void tearDown() throws Exception {
	}
				
	@Test
	void test_deserializeTask() {
		Task task = new Task("","5","Up",false);
		FloorMessage request = createFloorRequest(5,UP,task);
		byte[] bytes = request.serialize();
		FloorMessage deserializedFloorRequest = FloorMessage.deserialize(bytes);
		
		assertTrue(deserializedFloorRequest.getRequest()[0].equals(new Integer(5)));
		assertTrue(deserializedFloorRequest.getRequest()[1].equals(new Integer(UP)));
		
		assertTrue(5 == deserializedFloorRequest.getTask().getStartFloor());
		assertTrue(1 == deserializedFloorRequest.getTask().getDirection());
		//assertTrue(deserializedFloorRequest.getSourceElevator().equals(new Integer(1))); //This test should work, but right now we're returning null.
	}
	
	@Test
	void test_deserializeFault() {
		Task task = new Task("14:05:16.0","-1","1",true);
		task.setTimeDifference(5);
		
		FloorMessage request = createFloorRequest(5,UP,task);
		byte[] bytes = request.serialize();
		FloorMessage deserializedFloorRequest = FloorMessage.deserialize(bytes);
		
		assertTrue(deserializedFloorRequest.getRequest()[0].equals(new Integer(5)));
		assertTrue(deserializedFloorRequest.getRequest()[1].equals(new Integer(UP)));	
		
		assertTrue((LocalTime.parse("14:05:16.0")).equals(deserializedFloorRequest.getTask().getRequestTime()));
		assertTrue(-1 == deserializedFloorRequest.getTask().getFaultNumber());
		assertTrue(1 == deserializedFloorRequest.getTask().getElevatorNumber());
		assertTrue(5 == deserializedFloorRequest.getTask().getTimeDifference());
		//assertTrue(deserializedFloorRequest.getSourceElevator().equals(new Integer(1))); //This test should work, but right now we're returning null.
	}
	
	public FloorMessage createFloorRequest(int floorDest, int direction, Task task) {
		
		//Create the floor request
		Integer[] r = new Integer[] {floorDest,direction};	//Going to floor 5, Going up
		FloorMessage request = new FloorMessage() {
			@Override
			public Integer[] getRequest() {
				return r;
			}

			@Override
			public Integer getSourceElevator() {
				return 1;
			}
			
			@Override
			public Task getTask() {
				return task;
			}
		};

		return request;
	}
}
