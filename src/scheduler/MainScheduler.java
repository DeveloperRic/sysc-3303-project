package scheduler;

import util.Communication;

public class MainScheduler {

	private static final int NUMBER_OF_ELEVATORS = 1;

	static boolean verbose = true;

	Communication<FloorRequest, ElevatorMessage> floorCommunication;
	Communication<FloorRequest, ElevatorMessage> elevatorCommunication;
	private boolean active;
	SchedulerState currentState;

	public MainScheduler() {
		floorCommunication = new Communication<>("Floor", "Scheduler");
		elevatorCommunication = new Communication<>("Scheduler", "Elevator");
	}

	public void activate() {
		active = true;
		new Thread(new Middleman<>(floorCommunication, elevatorCommunication,
				SchedulerState.FORWARD_REQUEST_TO_ELEVATOR, "Floor")).start();
		new Thread(new Middleman<>(elevatorCommunication.reverse(), floorCommunication.reverse(),
				SchedulerState.RECEIVE_MESSAGE_FROM_ELEVATOR, "Elevator")).start();
	}

	public void deactivate() {
		active = false;
	}

	public static int getNumberOfElevators() {
		return NUMBER_OF_ELEVATORS;
	}

	public void setVerbose(boolean verbose) {
		MainScheduler.verbose = verbose;
		floorCommunication.setVerbose(verbose);
		elevatorCommunication.setVerbose(verbose);
	}

	private synchronized void switchState(SchedulerState state, Object param) {
		while (currentState != null && state.working) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		if (verbose) {
			System.out.println("SCHEDULER SUBSYSTEM: state changing to -> " + state + "\n");
		}
		state.working = true;
		currentState = state;
		state.doWork(this, param);
	}

	private class Middleman<X, Y, Z> implements Runnable {
		private Communication<X, Y> source;
		private Communication<X, Z> sink;
		private SchedulerState state;
		private String sourceName;

		private Middleman(Communication<X, Y> source, Communication<X, Z> sink, SchedulerState state,
				String sourceName) {
			this.source = source;
			this.sink = sink;
			this.state = state;
			this.sourceName = sourceName;
		}

		@Override
		public void run() {
			while (active) {

				X obj = source.bGet();

				if (verbose) {
					System.out.println("SCHEDULER SUBSYSTEM: Middleman processing message from " + sourceName + "\n");
				}

				switchState(state, obj);

//				sink.aPut(obj);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}

	}

}
