package test.unitTestScheduler;

import static org.junit.jupiter.api.Assertions.*;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.Task;
import scheduler.ElevatorMessage;
import scheduler.FloorMessage;
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
	void test_forwardRequestToElevator() throws SocketException, UnknownHostException {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getElevatorMessages().size() == 0);

		FloorMessage request = createFloorRequest(5, UP);
		int selectedElevator = 1;
		Object[] object = { request, selectedElevator };

		ms.switchState(SchedulerState.SEND_ACKNOWLEDGEMENT_TO_ELEVATOR, object);

		assertTrue(ms.getElevatorMessages().size() == 1);
	}

	@Test
	void test_forwardAcknowledgementToFloor() throws SocketException, UnknownHostException {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getFloorMessages().size() == 0);

		ElevatorMessage message = new ElevatorMessage() {
			@Override
			public String getAcknowledgement() {
				return "Test elevator arrives @ 5";
			}

			@Override
			public Integer getFloorArrivedOn() {
				return 5;
			}
		};

		ms.switchState(SchedulerState.FORWARD_ACKNOWLEDGEMENT_TO_FLOOR, message.serialize());

		assertTrue(ms.getFloorMessages().size() == 1);
	}

	@Test
	void test_processEtaFromElevator() throws SocketException, UnknownHostException {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getElevatorMessages().size() == 0);

		FloorMessage request = createFloorRequest(5, UP);
		ms.switchState(SchedulerState.PROCESS_ETA_FROM_ELEVATOR, (Object) request);

		assertTrue(ms.getElevatorMessages().size() == 1);
	}

	@Test
	void test_RequestToElevator() throws SocketException, UnknownHostException {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getElevatorMessages().size() == 0);

		FloorMessage request = createFloorRequest(5, UP);
		byte[] bytes = request.serialize();
		ms.switchState(SchedulerState.FORWARD_REQUEST_TO_ELEVATOR, (Object) bytes);

		assertTrue(ms.getElevatorMessages().size() == 1);
	}

	@Test
	void test_CommissioningElevator() throws SocketException, UnknownHostException {
		MainScheduler ms = new MainScheduler();
		ms.closeComms();
		assertTrue(ms.getDecommissionedElevators().size() == 0);

		ElevatorMessage decomMessage = new ElevatorMessage() {
			@Override
			public Integer[] getFaultNotice() {
				return new Integer[] { 0, 7 };
			}
		};

		ms.switchState(SchedulerState.RECEIVE_MESSAGE_FROM_ELEVATOR, decomMessage.serialize());

		assertTrue(ms.getDecommissionedElevators().size() == 1);
		assertEquals(7,(int) ms.getDecommissionedElevators().get(0));
		
		decomMessage = new ElevatorMessage() {
			@Override
			public Integer[] getFaultNotice() {
				return new Integer[] { 1, 7 };
			}
		};

		ms.switchState(SchedulerState.RECEIVE_MESSAGE_FROM_ELEVATOR, decomMessage.serialize());

		assertTrue(ms.getDecommissionedElevators().isEmpty());
	}

	public FloorMessage createFloorRequest(int floorDest, int direction) {

		// Create the floor request
		Integer[] r = new Integer[] { floorDest, direction }; // Going to floor 5, Going up

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
				return null;
			}
		};

		return request;
	}
}
