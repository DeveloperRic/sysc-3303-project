package scheduler;

import java.util.Arrays;

import util.Transport;
import util.Communication.Selector;
import util.Printer;

/**
 * This class limits the access of the MainScheduler to only the elevator put
 * and get functions
 *
 */
public class ElevatorScheduler implements SchedulerType<ElevatorMessage, FloorRequest> {
	public static final int ELEVATOR_PORT = 63974;

	// The main scheduler object
	private final Transport t;
	private final Object getLock = "get lock";
	private final Object putLock = "put lock";
	private BytesWrapper receivedBytes = new BytesWrapper(null);

	/**
	 * Instantiates the elevator scheduler (lives in elevator-subsystem runtime)
	 */
	public ElevatorScheduler() {
		t = new Transport("Elevator", ELEVATOR_PORT, false);
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_ELEVATOR);
		Printer.print("Elevator send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	@Override
	public FloorRequest get(Selector selector) {
		synchronized (getLock) {
//			while (waitingOnData.value) {
//				try {
//					waitingOnData.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			
			t.send(new byte[0]);

//			waitingOnData.value = true;

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {
					if (receivedBytes.value == null) {
						Printer.print("--->[data] Elevator receiving\n");
						try {
							receivedBytes.value = (byte[]) t.receive()[0];
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							receivedBytes.value = (byte[]) t.receive()[0];
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						Printer.print("CheckDATA: " + t.receive()[0]);
//						Printer.print("^^ get()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length == 0) {
						try {
							Printer.print("--->[data] Elevator waiting\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				FloorRequest floorRequest = FloorRequest.deserialize(receivedBytes.value);
				Printer.print("Received " + floorRequest + "\n");

				receivedBytes.value = null;

//				waitingOnData.value = false;
//				waitingOnData.notifyAll();

				return floorRequest;
			}
		}
	}

//	private int putBlocked;

	@Override
	public void put(ElevatorMessage message) {
//		Printer.print("put called " + (++putBlocked) + " potentially blocked");
		synchronized (putLock) {
			Printer.print("got ack lock");

//			while (waitingOnAcknowledgement.value) {
//				Printer.print("wait on ack");
//				try {
//					waitingOnAcknowledgement.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			Printer.print("sending " + message + "\n");

			t.send(message.serialize());

//			waitingOnAcknowledgement.value = true;

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {
					if (receivedBytes.value == null) {
						Printer.print("--->[conf] Elevator receiving\n");
						try {
							receivedBytes.value = (byte[]) t.receive()[0];
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						Printer.print("^^ put()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length > 0) {
						try {
							Printer.print("--->[conf] Elevator waiting\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				receivedBytes.value = null;
				receivedBytes.notifyAll();

//				waitingOnAcknowledgement.value = false;
//				waitingOnAcknowledgement.notifyAll();
			}
		}
//		Printer.print("released put lock " + (--putBlocked) + " potentially blocked");
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

	private static class BytesWrapper {
		private byte[] value;

		private BytesWrapper(byte[] value) {
			this.value = value;
		}
	}

	public void closeComms() {
		t.close();
	}
	
	
	public Transport getTransport() {
		return t;
	}
}
