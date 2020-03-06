package test;

import org.junit.jupiter.api.Test;

import scheduler.FloorRequest;
import scheduler.FloorsScheduler;

class FloorRuntimeTest {

	@Test
	void test() {
		FloorsScheduler scheduler = new FloorsScheduler();

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				Integer[] r1 = new Integer[] {5, 1};
				scheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return r1;
					}
				});
				
				System.out.println(scheduler.get(null));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				Integer[] r2 = new Integer[] {3, 1};
				scheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return r2;
					}
				});
				
				System.out.println(scheduler.get(null));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				Integer[] r3 = new Integer[] {10, -1};
				scheduler.put(new FloorRequest() {
					@Override
					public Integer[] getRequest() {
						return r3;
					}
				});
				
				System.out.println(scheduler.get(null));
			}
		}).start();

		// wait for elevator to return to sleep mode
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

}
