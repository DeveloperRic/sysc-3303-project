package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import util.Transport;

class FloorSchedulerUnitTest {

	//have other schedulers running first
	@Test
	void test() {
		Transport.setVerbose(true);
		
		FloorsScheduler scheduler = new FloorsScheduler();
		
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				Integer[] r1 = new Integer[] {5, 1};
				send(r1);				

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				Integer[] r2 = new Integer[] {3, 1};
				
				send(r2);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				Integer[] r3 = new Integer[] {10, -1};
				send(r3);
			}
			
			private void send(Integer[] r) {
				
				//serialize request
				byte[] data = new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return r;
					}
				}.serialize();
				
				assertTrue(scheduler.FLOOR_PORT == scheduler.t.getSendPort() && scheduler.FLOOR_PORT == scheduler.t.getReceivePort());
				
				//same as scheduler.put(request)
				Object[] send = scheduler.t.send(data);
				
				//checking if packet is being sent to the right place
				assertTrue(((byte[])send[0]).length > 0);
				assertTrue((int)send[1] == scheduler.t.getDestinationPort());
				
				// receive confirmation of message received
				System.out.println("--->[conf] Floor waiting to receive");
				
				Object[] receive = scheduler.t.receive();
				
				assertTrue(receive[1].toString() != null);
				
				System.out.println(scheduler.get(null));
				
			}
		}).start();

		// wait for elevator to return to sleep mode
		boolean wait = true;
		while (wait) {
			try {
				Thread.sleep(5000);
				wait = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
