package scheduler;

import java.nio.ByteBuffer;

import util.Communication.Selector;
import util.Transport;

/**
 * This class limits the access of the MainScheduler to only the elevator put
 * and get functions
 *
 */
public class ElevatorScheduler implements SchedulerType<ElevatorMessage, FloorRequest> {
//	public static final int ELEVATOR_PORT = 63974;

	// The main scheduler object
	private final byte elevatorNumber;
	private final Transport t;
	private final byte[] receivePort;
	private final Object getLock = "get lock";
	private final Object putLock = "put lock";
	private BytesWrapper receivedBytes = new BytesWrapper(null);

	/**
	 * Instantiates the elevator scheduler (lives in elevator-subsystem runtime)
	 */
	public ElevatorScheduler(Integer elevatorNumber) {
		this.elevatorNumber = elevatorNumber.byteValue();
		t = new Transport("Elevator");
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_ELEVATOR);
		receivePort = ByteBuffer.allocate(4).putInt(t.getReceivePort()).array();
		System.out.println("Elevator send/receive socket bound on port " + t.getReceivePort() + "\n");
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
			
			byte[] bytes = new byte[1 + receivePort.length];
			
			bytes[0] = elevatorNumber;
			for (int i = 0; i < receivePort.length; ++i) {
				bytes[i + 1] = receivePort[i];
			}

			t.send(bytes);

//			waitingOnData.value = true;

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {
					if (receivedBytes.value == null) {
						System.out.println("--->[data] Elevator receiving\n");
						receivedBytes.value = (byte[]) t.receive()[0];
//						System.out.println("CheckDATA: " + t.receive()[0]);
//						System.out.println("^^ get()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length == 0) {
						try {
							System.out.println("--->[data] Elevator waiting\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				FloorRequest floorRequest = FloorRequest.deserialize(receivedBytes.value);
				System.out.println("Received " + floorRequest + "\n");

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
//		System.out.println("put called " + (++putBlocked) + " potentially blocked");
		synchronized (putLock) {
			System.out.println("got ack lock");

//			while (waitingOnAcknowledgement.value) {
//				System.out.println("wait on ack");
//				try {
//					waitingOnAcknowledgement.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			System.out.println("sending " + message + "\n");
			
			byte[] messageBytes = message.serialize();
			ByteBuffer buffer = ByteBuffer.allocate(messageBytes.length + 4);
			
			buffer.putInt(t.getReceivePort());
			buffer.put(messageBytes);

			t.send(buffer.array());

//			waitingOnAcknowledgement.value = true;

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {
					if (receivedBytes.value == null) {
						System.out.println("--->[conf] Elevator receiving\n");
						receivedBytes.value = (byte[]) t.receive()[0];
//						System.out.println("^^ put()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length > 0) {
						try {
							System.out.println("--->[conf] Elevator waiting\n");
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
//		System.out.println("released put lock " + (--putBlocked) + " potentially blocked");
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

	public byte getElevatorNumber() {
		return elevatorNumber;
	}

	public Transport getTransport() {
		return t;
	}
}
