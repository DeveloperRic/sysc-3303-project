package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scheduler.ElevatorMessage;
import scheduler.ElevatorScheduler;
import scheduler.FloorRequest;
import scheduler.MainScheduler;
import util.Transport;

public class ElevatorSchedulerUnitTest {

	@Test
	public void test() {
		ElevatorScheduler scheduler = new ElevatorScheduler();
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
			scheduler.put(new ElevatorMessage() {
				@Override
				public FloorRequest getFloorRequest() {
					return request;
				}
			});
			
			assertTrue(scheduler.get(null).getRequest()[0] == startFloor);
		
			}
		}).start();
	
	assertTrue(MainScheduler.PORT_FOR_ELEVATOR == t.getDestinationPort());
	assertTrue(scheduler.ELEVATOR_PORT == t.getReceivePort());
	}
}