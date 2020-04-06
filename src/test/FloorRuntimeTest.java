package test;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

import main.Task;
import scheduler.FloorMessage;
import scheduler.FloorsScheduler;
import util.Printer;
import util.Transport;

public class FloorRuntimeTest {

	@Test
	public void test() throws UnknownHostException, SocketException {
		Transport.setVerbose(false);

		FloorsScheduler scheduler = new FloorsScheduler(-1); // just one floor for now, later there will be more

//		FloorSubsystem floorSS = new FloorSubsystem(scheduler);
//		
//		new Thread(floorSS,"FloorSS").start();

		Thread thread1 = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}

					Integer[] r1 = new Integer[] { 5, 1 };
					scheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return r1;
						}
						@Override
						public Task getTask() {
							return null;
						}
					});

					Printer.print(scheduler.get(null));

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					Integer[] r2 = new Integer[] { 20, -1 };
					scheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return r2;
						}
						@Override
						public Task getTask() {
							return null;
						}
					});

					Printer.print(scheduler.get(null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Thread thread2 = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}

					Integer[] r1 = new Integer[] { 3, 1 };
					scheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return r1;
						}
						@Override
						public Task getTask() {
							return null;
						}
					});

					Printer.print(scheduler.get(null));

					try {
						Thread.sleep(13000);
					} catch (InterruptedException e) {
					}

					Integer[] r2 = new Integer[] { 6, -1 };
					scheduler.put(new FloorMessage() {
						@Override
						public Integer[] getRequest() {
							return r2;
						}
						@Override
						public Task getTask() {
							return null;
						}
					});

					Printer.print(scheduler.get(null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread1.start();
		thread2.start();

		// wait for elevator to return to sleep mode
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

}