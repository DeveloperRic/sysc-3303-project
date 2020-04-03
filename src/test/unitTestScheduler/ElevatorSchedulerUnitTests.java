package test.unitTestScheduler;

import static org.junit.Assert.assertTrue;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

import scheduler.ElevatorMessage;
import scheduler.ElevatorScheduler;
import scheduler.FloorRequest;
import scheduler.MainScheduler;
import util.Transport;

public class ElevatorSchedulerUnitTests {

	@Test
	public void test() throws UnknownHostException, SocketException {
		ElevatorScheduler scheduler = new ElevatorScheduler(1);
		Transport t = scheduler.getTransport();
		int startFloor = 5;

		Integer[] r = new Integer[] { startFloor, 0 };

		FloorRequest request = new FloorRequest() {
			@Override
			public Integer[] getRequest() {
				return r;
			};

		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					scheduler.put(new ElevatorMessage() {
						@Override
						public FloorRequest getFloorRequest() {
							return request;
						}
					});

					assertTrue(scheduler.get(null).getRequest()[0] == startFloor);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		assertTrue(MainScheduler.PORT_FOR_ELEVATOR == t.getDestinationPort());
//	assertTrue(scheduler.ELEVATOR_PORT == t.getReceivePort());
	}
}