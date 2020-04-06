package elevator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import main.Task;
import scheduler.ElevatorMessage;
import scheduler.FloorMessage;
import util.Printer;

public class Watchdog {
	private static final long DELAY_TILL_FAULT = 50;

	private ElevatorSubsystem subsystem;
	private List<FaultConfig> configs;
	private List<Integer> occurredFaults;
	private PriorityQueue<Integer> workDoing;
	public boolean recovering;

	public Watchdog(ElevatorSubsystem subsystem) {
		this.subsystem = subsystem;
		configs = new ArrayList<>();
		occurredFaults = new ArrayList<>();
		workDoing = new PriorityQueue<Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return Math.abs(o1 - subsystem.elevator.currentFloor) - Math.abs(o2 - subsystem.elevator.currentFloor);
			}
		});
	}

	public void saveConfig(FaultConfig config) {
		this.configs.add(config);
	}

	public void addToWorkDoing(Integer job) {
		if (job != null && !workDoing.contains(job)) {
			workDoing.add(job);
			Printer.print(
					"WATCHDOG: Task floor " + job + " added to watch list for elevator " + subsystem.elevatorNumber);
		}
	}

	public void removeFromWorkDoing(Integer job) {
		workDoing.remove(job);
		Printer.print(
				"WATCHDOG: Task floor " + job + " removed from watch list for elevator " + subsystem.elevatorNumber);
	}

	public void clearWorkDoing() {
		workDoing.clear();
	}

	public List<FaultConfig> getConfigs() {
		return configs;
	}

	public boolean checkForFault() {
		if (recovering)
			return true;

		long curTime = System.currentTimeMillis();
		FaultConfig hardFault = null;
		FaultConfig softFault = null;

		List<Integer> occurredFaults = new ArrayList<>();

		for (int i = 0; i < configs.size(); ++i) {
			FaultConfig config = configs.get(i);

			if (hardFault != null)
				break;

			if (config.readyToFault(curTime)) {

				if (config.isHardFault()) {
					hardFault = config;
				} else {
					softFault = config;
				}

				occurredFaults.add(configs.remove(i).identifier);
				--i;
			}
		}

		if (hardFault == null && softFault == null)
			return false;

		try {
			Thread.sleep(DELAY_TILL_FAULT);
		} catch (InterruptedException e) {
		}

		this.occurredFaults.addAll(occurredFaults);

		final FaultConfig finalHardFault = hardFault;
		final FaultConfig finalSoftFault = softFault;

		hardFault = null;
		softFault = null;

		new Thread(new Runnable() {
			@Override
			public void run() {
				recover(finalHardFault != null ? finalHardFault : finalSoftFault);
			}
		}).start();

		return true;
	}

	public boolean faultOccured(int identifier) {
		return occurredFaults.contains(identifier);
	}

	private void recover(FaultConfig fault) {
		recovering = true;

		Printer.print("WATCHDOG: Detected fault in elevator " + subsystem.elevatorNumber);
		Printer.print("WATCHDOG: Powering off elevator " + subsystem.elevatorNumber + "..");

		subsystem.powerOff();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		Printer.print(
				"WATCHDOG: Notifying scheduler that elevator " + subsystem.elevatorNumber + " should be decomissioned");
		Printer.print("WATCHDOG: Attempting to redirect task floors " + new ArrayList<>(workDoing).toString());

		Integer lastJob = workDoing.poll();
		FloorMessage lastRequest = lastJob == null ? null : new FloorMessage() {
			@Override
			public Task getTask() {
				return null;
			}

			@Override
			public Integer[] getRequest() {
				return new Integer[] { lastJob, 0 };
			}
		};

		try {
			Integer[] faultNotice = new Integer[] { 0, subsystem.elevatorNumber };
			subsystem.scheduler.put(new ElevatorMessage() {
				@Override
				public FloorMessage getFloorRequest() {
					return lastRequest;
				}

				@Override
				public Integer[] getFaultNotice() {
					return faultNotice;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (Integer job : workDoing) {
			if (job == null)
				continue;
			try {
				Integer[] req = new Integer[] { job, subsystem.elevatorNumber };
				FloorMessage request = new FloorMessage() {
					@Override
					public Task getTask() {
						return null;
					}

					@Override
					public Integer[] getRequest() {
						return req;
					}
				};
				subsystem.scheduler.put(new ElevatorMessage() {
					@Override
					public FloorMessage getFloorRequest() {
						return request;
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (fault.isSoftFault()) {
			Printer.print("WATCHDOG: Fault was SOFT, shutting down instance for elevator " + subsystem.elevatorNumber
					+ ", powering on a new elevator instance..");

			subsystem.shutDown();

			try {
				ElevatorSubsystem newSubsystem = new ElevatorSubsystem(subsystem);
				newSubsystem.powerOn(7000);

				Printer.print("WATCHDOG: Notifying scheduler that elevator " + newSubsystem.elevatorNumber
						+ " should be recomissioned");

				Integer[] faultNotice = new Integer[] { 1, newSubsystem.elevatorNumber };
				newSubsystem.scheduler.put(new ElevatorMessage() {
					@Override
					public Integer[] getFaultNotice() {
						return faultNotice;
					}
				});

				subsystem = newSubsystem;
				recovering = false;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			Printer.print(
					"WATCHDOG: Fault was HARD, elevator " + subsystem.elevatorNumber + " will not be powered on again");
		}
	}

	public static class FaultConfig {
		public static final int HARD_FAULT = -2;
		public static final int SOFT_FAULT = -1;
		public static final int DEFAULT_IDENTIFIER = 0;

		private final long faultAt;
		private final int faultType;
		private final int identifier;

		public FaultConfig(long millisecondsTillFault, int faultType, int identifier) {
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
