package test.unitTestScheduler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scheduler.FloorRequest;
import scheduler.MainScheduler;
import scheduler.SchedulerState;

public class SchedulerStateUnitTests {
	
	private static final int UP = 1;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void test_forwardRequestToElevator() {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getElevatorMessages().size() == 0);
		
		FloorRequest request = createFloorRequest(5,UP);
		int selectedElevator = 1;
		Object[] object = {request,selectedElevator};
		
		ms.switchState(SchedulerState.SEND_ACKNOWLEDGEMENT_TO_ELEVATOR,object);
		
		assertTrue(ms.getElevatorMessages().size() == 1);	
	}
	
	@Test
	void test_forwardAcknowledgementToFloor() {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getFloorMessages().size() == 0);
		
		FloorRequest request = createFloorRequest(5,UP);
		byte[] bytes = request.serialize();
		ms.switchState(SchedulerState.FORWARD_ACKNOWLEDGEMENT_TO_FLOOR,(Object)bytes);
		
		assertTrue(ms.getFloorMessages().size() == 1);
	}
	
	@Test
	void test_processEtaFromElevator() {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getElevatorMessages().size()==0);
		
		FloorRequest request = createFloorRequest(5,UP);
		ms.switchState(SchedulerState.PROCESS_ETA_FROM_ELEVATOR, (Object)request);
		
		assertTrue(ms.getElevatorMessages().size() == 1);
	}
	
	@Test
	void test_RequestToElevator() {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getElevatorMessages().size() == 0);
		
		FloorRequest request = createFloorRequest(5,UP);
		byte[] bytes = request.serialize();
		ms.switchState(SchedulerState.FORWARD_REQUEST_TO_ELEVATOR, (Object)bytes);
		
		assertTrue(ms.getElevatorMessages().size() == 1);
	}
	
	public FloorRequest createFloorRequest(int floorDest, int direction) {
		
		//Create the floor request
		Integer[] r = new Integer[] {floorDest,direction};	//Going to floor 5, Going up
		FloorRequest request = new FloorRequest() {
			@Override
			public Integer[] getRequest() {
				return r;
			}
			
			@Override
			public Integer getSourceElevator() {
				return 1;
			}
		};
		
		return request;
	}
}
