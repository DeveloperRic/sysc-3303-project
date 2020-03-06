package test;

import org.junit.jupiter.api.Test;

import elevator.ElevatorSubsystem;
import scheduler.ElevatorScheduler;

class ElevatorRuntimeTest {

	@Test
	void test() {
		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler());
//		ElevatorSubsystem.setVerbose(false);
		subsystem.powerOn();

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				subsystem.pressButton(9);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				subsystem.pressButton(1);
			}
		});

		// wait for elevator to return to sleep mode
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
