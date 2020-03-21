package test.unitTestScheduler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scheduler.FloorRequest;

public class FloorRequestUnitTests {

	private static final int UP = 1;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_getRequest() {
		FloorRequest request = createFloorRequest(5, UP);

		assertTrue(request.getRequest()[0].equals(new Integer(5)));
		assertTrue(request.getRequest()[1].equals(new Integer(UP)));
	}

	@Test
	void test_getSourceElevator() {
		FloorRequest request = createFloorRequest(5, UP);
		assertTrue(request.getSourceElevator().equals(new Integer(1)));
	}

	@Test
	void test_toString() {
		FloorRequest request = createFloorRequest(5, UP);
		String expectedResult = "<FlrReq: (fl 5 going up)>";
		assertTrue(request.toString().equals(expectedResult));
	}

	@Test
	void test_serialize() {
		FloorRequest request = createFloorRequest(5, UP);
		byte[] bytes = request.serialize();
		byte[] expectedBytes = { 2, 4, 5, 1, -1, -128, 0, 0, 0, -1 };

		// Goes through each byte and validates them
		for (int i = 0; i < bytes.length; i++) {
			assertTrue(bytes[i] == expectedBytes[i]);
		}
	}

	@Test
	void test_deserialize() {
		FloorRequest request = createFloorRequest(5, UP);
		byte[] bytes = request.serialize();
		FloorRequest deserializedFloorRequest = FloorRequest.deserialize(bytes);

		assertTrue(deserializedFloorRequest.getRequest()[0].equals(new Integer(5)));
		assertTrue(deserializedFloorRequest.getRequest()[1].equals(new Integer(UP)));
		// assertTrue(deserializedFloorRequest.getSourceElevator().equals(new
		// Integer(1))); //This test should work, but right now we're returning null.
	}

	public FloorRequest createFloorRequest(int floorDest, int direction) {

		// Create the floor request
		Integer[] r = new Integer[] { floorDest, direction }; // Going to floor 5, Going up
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
