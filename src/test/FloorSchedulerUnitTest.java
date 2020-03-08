package test;


import org.junit.Test;

import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import util.Transport;
import static org.junit.Assert.assertTrue;
import util.Printer;

public class FloorSchedulerUnitTest {

	//have other schedulers running first
	@Test
	public void test() {
		Transport.setVerbose(true);
		
		FloorsScheduler scheduler = new FloorsScheduler();
		Transport t = scheduler.getTransport();
		
		
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
				
				//simulates a put from floor scheduler
				
				//serialize request
				byte[] data = new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return r;
					}
				}.serialize();
				
				assertTrue(scheduler.FLOOR_PORT == t.getSendPort() && scheduler.FLOOR_PORT == t.getReceivePort());
				
				//same as scheduler.put(request)
				Object[] send = t.send(data);
				
				//checking if packet is being sent to the right place
				assertTrue(((byte[])send[0]).length > 0);
				assertTrue((int)send[1] == t.getDestinationPort());
				
				// receive confirmation of message received
				Printer.print("--->[conf] Floor waiting to receive");
				
				//just check to see if floor sched can receive packets 
				Object[] receive = null;
				try {
					receive = t.receive();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				assertTrue(receive[1] != null);
				
				Printer.print(scheduler.get(null));
				
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
