package test;

import org.junit.jupiter.api.Test;

import scheduler.MainScheduler;

class SchedulerRuntimeTest {

	@Test
	void test() {

		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(true); // code works now, no need for spam
		mainScheduler.activate();
		
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
