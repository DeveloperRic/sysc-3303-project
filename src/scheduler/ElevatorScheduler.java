package scheduler;

import util.Transport;
import util.Communication.Selector;

/**
 * This class limits the access of the MainScheduler to only the elevator put
 * and get functions
 *
 */
public class ElevatorScheduler implements SchedulerType<ElevatorMessage, FloorRequest> {
	public static final int ELEVATOR_PORT = 63974;

	// The main scheduler object
	private final Transport t;
	private final BooleanWrapper waitingOnData = BooleanWrapper.FALSE;
	private final BooleanWrapper waitingOnAcknowledgement = BooleanWrapper.FALSE;
	private byte[] receivedBytes = null;

	/**
	 * Instantiates the elevator scheduler (lives in elevator-subsystem runtime)
	 */
	public ElevatorScheduler() {
		t = new Transport("Elevator", ELEVATOR_PORT, false);
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_ELEVATOR);
		System.out.println("Elevator send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	@Override
	public FloorRequest get(Selector selector) {
		synchronized (waitingOnData) {
			while (waitingOnData.value) {
				try {
					waitingOnData.wait();
				} catch (InterruptedException e) {
				}
			}

			t.send(new byte[0]);

			waitingOnData.value = true;

			System.out.println("--->[data] Elevator waiting to receive");
			receivedBytes = (byte[]) t.receive()[0];

			synchronized (this) {
				this.notifyAll();

				while (receivedBytes == null || receivedBytes.length == 0) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}

				waitingOnData.value = false;
				waitingOnData.notifyAll();

				return FloorRequest.deserialize(receivedBytes);
			}
		}
	}

	private int putBlocked;

	@Override
	public void put(ElevatorMessage o) {
		System.out.println("put called " + (++putBlocked) + " potentially blocked");
		synchronized (waitingOnAcknowledgement) {
			System.out.println("got ack lock");

			while (waitingOnAcknowledgement.value) {
				System.out.println("wait on ack");
				try {
					waitingOnAcknowledgement.wait();
				} catch (InterruptedException e) {
				}
			}

			System.out.println("sending");

			t.send(o.serialize());

			waitingOnAcknowledgement.value = true;

			// receive confirmation of message received
			System.out.println("--->[conf] Elevator waiting to receive");
			receivedBytes = (byte[]) t.receive()[0];

			synchronized (this) {
				this.notifyAll();

				while (receivedBytes == null || receivedBytes.length > 0) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}

				waitingOnAcknowledgement.value = false;
				waitingOnAcknowledgement.notifyAll();
			}
		}
		System.out.println("released put lock " + (--putBlocked) + " potentially blocked");
	}

	public void delay(ElevatorMessage o) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3 / MainScheduler.getNumberOfElevators() * 1000);
				} catch (InterruptedException e) {
				}
				put(o);
			}
		}).start();
//		s.elevatorCommunication.delayBPut(o, (int) Math.ceil(3 / MainScheduler.getNumberOfElevators()));
	}

	private static class BooleanWrapper {
		private static final BooleanWrapper FALSE = new BooleanWrapper(false);
		private boolean value;

		private BooleanWrapper(boolean value) {
			this.value = value;
		}
	}

}
