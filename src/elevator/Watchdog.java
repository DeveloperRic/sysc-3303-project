package elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Watchdog {
	private long delayTillFault;
	private List<WatchdogConfig> configs;
	private List<Integer> occurredFaults;

	public Watchdog(long delayTillFault) {
		this.delayTillFault = delayTillFault;
		configs = new ArrayList<>();
		occurredFaults = new ArrayList<>();
	}

	public void saveConfig(WatchdogConfig config) {
		this.configs.add(config);
	}

	public List<WatchdogConfig> getConfigs() {
		return configs;
	}

	public void watch(Consumer<List<Integer>> faultHandler) {
		long curTime = System.currentTimeMillis();
		boolean doHardFault = false;
		boolean doSoftFault = false;

		List<Integer> occurredFaults = new ArrayList<>();

		for (int i = 0; i < configs.size(); ++i) {
			WatchdogConfig config = configs.get(i);

			if (doHardFault && doSoftFault)
				break;

			if (config.readyToFault(curTime)) {

				if (config.isHardFault()) {
					doHardFault = true;
				} else {
					doSoftFault = true;
				}

				occurredFaults.add(configs.remove(i).identifier);
				--i;
			}
		}

		if (!(doHardFault || doSoftFault))
			return;

		try {
			Thread.sleep(delayTillFault);
		} catch (InterruptedException e) {
		}

		this.occurredFaults.addAll(occurredFaults);
		faultHandler.accept(occurredFaults);

	}

	public boolean faultOccured(int identifier) {
		return occurredFaults.contains(identifier);
	}

	public static class WatchdogConfig {
		public static final int HARD_FAULT = 1;
		public static final int SOFT_FAULT = 0;

		private final long faultAt;
		private final int faultType;
		private final int identifier;

		public WatchdogConfig(long millisecondsTillFault, int faultType, int identifier) {
			this.faultAt = System.currentTimeMillis() + millisecondsTillFault;
			this.faultType = faultType;
			this.identifier = identifier;
		}

		public boolean isHardFault() {
			return faultType == HARD_FAULT;
		}

		public boolean isSoftFault() {
			return faultType == SOFT_FAULT;
		}

		public boolean readyToFault(long curTime) {
			return curTime >= faultAt;
		}
	}

}
