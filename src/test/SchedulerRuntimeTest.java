package test;

import scheduler.MainScheduler;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

public class SchedulerRuntimeTest {

	@Test
	public void test() throws SocketException, UnknownHostException {

		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(false);
		mainScheduler.activate();

		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
