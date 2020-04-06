package test.unitTestElevator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import elevator.Watchdog;
import elevator.Watchdog.WatchdogConfig;

@TestInstance(Lifecycle.PER_CLASS)
public class WatchdogUnitTests {
	
	private Watchdog watchdog;

	@BeforeAll
	void setUp() throws Exception {
		watchdog = new Watchdog(100);
	}

	@Test
	void testSaveConfig() {
		WatchdogConfig config = new WatchdogConfig(3000, WatchdogConfig.SOFT_FAULT, 4);
		
		assertTrue(config.isSoftFault());
		assertFalse(config.isHardFault());
		assertFalse(config.readyToFault(System.currentTimeMillis()));
		
		watchdog.saveConfig(config);
		
		assertEquals(config, watchdog.getConfigs().get(0));
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {}
		
		assertTrue(config.readyToFault(System.currentTimeMillis()));
		
		List<Integer> o = new ArrayList<>();
		
		watchdog.watch(occurences -> o.addAll(occurences));
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		
		assertEquals(4, o.get(0));
		
		assertTrue(watchdog.faultOccured(4));
		assertFalse(watchdog.faultOccured(0));
	}

}
