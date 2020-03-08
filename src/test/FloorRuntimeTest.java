package test;

import scheduler.FloorRequest;
import scheduler.FloorsScheduler;
import util.Transport;
import main.FloorSubsystem;

import org.junit.Test;

public class FloorRuntimeTest {

	@Test
	public void test() {
		Transport.setVerbose(true);
		
		FloorsScheduler scheduler = new FloorsScheduler();
		
		FloorSubsystem floorSS = new FloorSubsystem(scheduler);
		
		new Thread(floorSS,"FloorSS").start();
		
		

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//				}
//
//				Integer[] r1 = new Integer[] {5, 1};
//				scheduler.put(new FloorRequest() {
//					@Override
//					public Integer[] getRequest() {
//						return r1;
//					}
//				});
//				
//				System.out.println(scheduler.get(null));
//
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//
//				Integer[] r2 = new Integer[] {3, 1};
//				scheduler.put(new FloorRequest() {
//					@Override
//					public Integer[] getRequest() {
//						return r2;
//					}
//				});
//				
//				System.out.println(scheduler.get(null));
//
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//
//				Integer[] r3 = new Integer[] {10, -1};
//				scheduler.put(new FloorRequest() {
//					@Override
//					public Integer[] getRequest() {
//						return r3;
//					}
//				});
//				
//				System.out.println(scheduler.get(null));
//			}
//		}).start();

		// wait for elevator to return to sleep mode
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

}