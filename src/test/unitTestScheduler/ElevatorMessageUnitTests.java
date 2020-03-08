package test.unitTestScheduler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scheduler.ElevatorMessage;
import scheduler.FloorRequest;

public class ElevatorMessageUnitTests {
	
	private static final int UP = 1;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void test_getFloorRequest() {
			
		FloorRequest request = createFloorRequest(5,UP);
		ElevatorMessage elevMessage = createElevatorMessage(request,"");
				
		//Assert the elevator message
		assertTrue( (elevMessage.getFloorRequest().getRequest()[0]).equals(new Integer(5)));
		assertTrue( (elevMessage.getFloorRequest().getRequest()[1]).equals(new Integer(UP)));
		assertTrue( (elevMessage.getFloorRequest().getSourceElevator() == 1));
	}
	
	@Test
	void test_getAcknowledgement() {
		
		//Create the acknowledgment message (String)
		String s = "Elevator " + 1 + " arrived at floor " + 5;
		ElevatorMessage elevMessage = createElevatorMessage(null,s);
				
		//Assert the acknowledgement message in the elevator message
		assertTrue(elevMessage.getAcknowledgement().equals(s));
	}
	
	@Test
	void test_toString() {
		
		FloorRequest request = createFloorRequest(5,UP);
		
		//Create the acknowledgement message
		String s = "Elevator " + 1 + " arrived at floor " + 5;
		ElevatorMessage elevMessage = createElevatorMessage(request,s);
		
		//Assert the elevator message toString()
		String expectedResult = "<ElevMsg: (<FlrReq: (fl 5 going up)>)(<Ack: " + s + ")>";
		String actualResult = elevMessage.toString();
		assertTrue(expectedResult.equals(actualResult));
	}
	
	@Test
	void test_serialize() {
		
		FloorRequest request = createFloorRequest(5,UP);
		
		//Create the acknowledgement message
		String s = "Elevator " + 1 + " arrived at floor " + 5;
		ElevatorMessage elevMessage = createElevatorMessage(request,s);
		
		byte[] bytes = elevMessage.serialize();
		byte[] expectedBytes = {10, 29, 
									2, 4, 5, 1, -1, -128, 0, 0, 0, -1, 
									69, 108, 101, 118, 97, 116, 111, 114, 32, 49, 32, 97, 114, 114, 105, 118, 101, 100, 32, 97, 116, 32, 102, 108, 111, 111, 114, 32, 53 };
		
		//Goes through each byte and validates them
		for(int i=0; i < bytes.length; i++) {
			assertTrue(bytes[i] == expectedBytes[i]);
		}
		
	}
	
	@Test
	void test_deserialize() {
		
		FloorRequest request = createFloorRequest(5,UP);
		
		//Create the acknowledgement message
		String s = "Elevator " + 1 + " arrived at floor " + 5;
		
		//Create the elevator message
		ElevatorMessage elevMessage = createElevatorMessage(request,s);
		
		//Serialize it first
		byte[] bytes = elevMessage.serialize();
		
		ElevatorMessage deserializedElevMessage = ElevatorMessage.deserialize(bytes);
		
		//Assert the FloorRequest and Acknowledgement messages
		assertTrue( (deserializedElevMessage.getFloorRequest().getRequest()[0]).equals(new Integer(5)));
		assertTrue( (deserializedElevMessage.getFloorRequest().getRequest()[1]).equals(new Integer(1)));
		assertTrue( (deserializedElevMessage.getAcknowledgement().equals(s)));
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
	
	public ElevatorMessage createElevatorMessage(FloorRequest request, String acknowledgement) {
		
		//Create the elevator message request
		ElevatorMessage elevMessage = new ElevatorMessage() {
			@Override
			public FloorRequest getFloorRequest() {
				return request;
			}
			
			@Override
			public String getAcknowledgement() {
				return acknowledgement;
			}
		};
		
		return elevMessage;
	}
}
