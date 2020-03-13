package test;

import scheduler.MainScheduler;

import org.junit.Test;

public class SchedulerRuntimeTest {

	@Test
	public void test() {

		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(false); // code works now, no need for spam
		mainScheduler.activate();
		
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
