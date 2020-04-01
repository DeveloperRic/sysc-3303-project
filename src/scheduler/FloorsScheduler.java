package scheduler;

import scheduler.RequestHeader.RequestType;
import util.Communication.Selector;
import util.Printer;
import util.Transport;

/**
 * This class limits the access of the MainScheduler to only the elevator put
 * and get functions
 *
 */
public class FloorsScheduler implements SchedulerType<FloorRequest, ElevatorMessage> {
//	public static final int ELEVATOR_PORT = 63971;

	// The main scheduler object
	private final byte floorNumber;
	private final Transport t;
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
		Printer.print("Floor send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	@Override
	public ElevatorMessage get(Selector selector) {
		synchronized (getLock) {
//			while (waitingOnData.value) {
//				try {
//					waitingOnData.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			t.send(new RequestHeader(RequestType.GET_DATA, t.getReceivePort(), floorNumber).getBytes());

//			waitingOnData.value = true;

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {
					if (receivedBytes.value == null) {
						Printer.print("--->[data] Floor receiving\n");
						try {
							receivedBytes.value = (byte[]) t.receive()[0];
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						System.out.println("CheckDATA: " + t.receive()[0]);
//						System.out.println("^^ get()");
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length == 0) {
						try {
							Printer.print("--->[data] Floor waiting\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				ElevatorMessage elevatorMessage = ElevatorMessage.deserialize(receivedBytes.value);
				Printer.print("Received " + elevatorMessage + "\n");

				receivedBytes.value = null;

//				waitingOnData.value = false;
//				waitingOnData.notifyAll();

				Printer.print("floorInt: " + elevatorMessage.getFloorArrivedOn());

				return elevatorMessage;
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

			t.send(new RequestHeader(RequestType.SEND_DATA, t.getReceivePort(), floorNumber)
					.attachDataBytes(request.serialize()));

//			waitingOnAcknowledgement.value = true;

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {
					if (receivedBytes.value == null) {
						System.out.println("--->[conf] Floor receiving\n");
						try {
							receivedBytes.value = (byte[]) t.receive()[0];
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
//		Printer.print("released put lock " + (--putBlocked) + " potentially blocked");
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
