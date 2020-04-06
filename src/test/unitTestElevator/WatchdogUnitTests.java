package test.unitTestElevator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runners.MethodSorters;

import elevator.ElevatorSubsystem;
import elevator.Watchdog;
import elevator.Watchdog.FaultConfig;
import main.Task;
import scheduler.ElevatorScheduler;
import scheduler.FloorMessage;
import scheduler.FloorsScheduler;
import scheduler.MainScheduler;
import util.Transport;

@TestInstance(Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WatchdogUnitTests {

	private Watchdog watchdog;
	private FaultConfig config;
	private FloorsScheduler floorsScheduler;

	@BeforeAll
	void setUp() throws Exception {
		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(true);
		mainScheduler.activate();

		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler(1));
		ElevatorSubsystem.setVerbose(true);
		subsystem.powerOn();

		floorsScheduler = new FloorsScheduler(-1);

		Transport.setVerbose(false);

		watchdog = subsystem.WATCHDOG;
	}

	@Test
	void a() {
		try {
			floorsScheduler.put(new FloorMessage() {
				@Override
				public Task getTask() {
					return null;
				}

				@Override
				public Integer[] getRequest() {
					return new Integer[] { 12, -1 };
				}
			});

			Thread.sleep(5000);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void b() {
		config = new FaultConfig(3000, FaultConfig.SOFT_FAULT, 4);

		assertTrue(config.isSoftFault());
		assertFalse(config.isHardFault());
		assertFalse(config.readyToFault(System.currentTimeMillis()));

		watchdog.saveConfig(config);

		assertEquals(config, watchdog.getConfigs().get(0));
	}

	@Test
	void c() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		assertTrue(config.readyToFault(System.currentTimeMillis()));

		assertTrue(watchdog.checkForFault());
		assertTrue(watchdog.faultOccured(4));
		assertFalse(watchdog.faultOccured(FaultConfig.DEFAULT_IDENTIFIER));
	}

	@Test
	void d() {
		try {
			floorsScheduler.put(new FloorMessage() {
				@Override
				public Task getTask() {
					return null;
				}

				@Override
				public Integer[] getRequest() {
					return new Integer[] { 9, -1 };
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void e() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
		}

		config = new FaultConfig(1000, FaultConfig.HARD_FAULT, FaultConfig.DEFAULT_IDENTIFIER);

		assertTrue(config.isHardFault());
		assertFalse(config.isSoftFault());
		assertFalse(config.readyToFault(System.currentTimeMillis()));

		watchdog.saveConfig(config);

		assertEquals(config, watchdog.getConfigs().get(0));

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
		}

		assertTrue(config.readyToFault(System.currentTimeMillis()));

		assertTrue(watchdog.checkForFault());
		assertTrue(watchdog.faultOccured(FaultConfig.DEFAULT_IDENTIFIER));

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

}
