package scheduler;

import java.nio.ByteBuffer;

import util.Communication.Selector;
import util.Transport;

/**
 * This class limits the access of the MainScheduler to only the elevator put
 * and get functions
 *
 */
public class FloorsScheduler implements SchedulerType<FloorRequest, String> {
//	public static final int ELEVATOR_PORT = 63971;

	// The main scheduler object
	private final byte floorNumber;
	private final Transport t;
	private final byte[] receivePort;
	private final Object getLock = "get lock";
	private final Object putLock = "put lock";
	private BytesWrapper receivedBytes = new BytesWrapper(null);

	/**
	 * Instantiates the floor scheduler (lives in floor-subsystem runtime)
	 */
	public FloorsScheduler(Integer floorNumber) {
		this.floorNumber = floorNumber.byteValue();
		t = new Transport("Floor");
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_FLOOR);
		receivePort = ByteBuffer.allocate(4).putInt(t.getReceivePort()).array();
		System.out.println("Floor send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	@Override
	public String get(Selector selector) {
		synchronized (getLock) {
//			while (waitingOnData.value) {
//				try {
//					waitingOnData.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			byte[] bytes = new byte[1 + receivePort.length];

			bytes[0] = floorNumber;
			for (int i = 0; i < receivePort.length; ++i) {
				bytes[i + 1] = receivePort[i];
			}

			t.send(bytes);

//			waitingOnData.value = true;

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {
					if (receivedBytes.value == null) {
						System.out.println("--->[data] Floor receiving\n");
						receivedBytes.value = (byte[]) t.receive()[0];
//						System.out.println("CheckDATA: " + t.receive()[0]);
//						System.out.println("^^ get()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length == 0) {
						try {
							System.out.println("--->[data] Floor waiting\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				ElevatorMessage elevatorMessage = ElevatorMessage.deserialize(receivedBytes.value);
				System.out.println("Received " + elevatorMessage + "\n");

				receivedBytes.value = null;

//				waitingOnData.value = false;
//				waitingOnData.notifyAll();

				return elevatorMessage.getAcknowledgement();
			}
		}
	}

//	private int putBlocked;

	@Override
	public void put(FloorRequest request) {
//		System.out.println("put called " + (++putBlocked) + " potentially blocked");
		synchronized (putLock) {
//			System.out.println("got ack lock");

//			while (waitingOnAcknowledgement.value) {
//				System.out.println("wait on ack");
//				try {
//					waitingOnAcknowledgement.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			System.out.println("sending " + request + "\n");

			byte[] messageBytes = request.serialize();
			ByteBuffer buffer = ByteBuffer.allocate(messageBytes.length + 4);

			buffer.putInt(t.getReceivePort());
			buffer.put(messageBytes);

			t.send(buffer.array());

//			waitingOnAcknowledgement.value = true;

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {
					if (receivedBytes.value == null) {
						System.out.println("--->[conf] Floor receiving\n");
						receivedBytes.value = (byte[]) t.receive()[0];
//						System.out.println("^^ put()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length > 0) {
						try {
							System.out.println("--->[conf] Floor waiting\n");
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

	private static class BytesWrapper {
		private byte[] value;

		private BytesWrapper(byte[] value) {
			this.value = value;
		}
	}

	public byte getElevatorNumber() {
		return floorNumber;
	}

	public Transport getTransport() {
		return t;
	}
}
