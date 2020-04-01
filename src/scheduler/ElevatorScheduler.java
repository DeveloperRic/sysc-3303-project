package scheduler;

import elevator.ElevatorSubsystem;
import scheduler.RequestHeader.RequestType;
import util.Communication.Selector;
import util.Printer;
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

			t.send(new RequestHeader(RequestType.GET_DATA, t.getReceivePort(), elevatorNumber).getBytes());

//			waitingOnData.value = true;

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {
					if (receivedBytes.value == null) {
						if (ElevatorSubsystem.verbose)
							System.out.println("--->[data] Elevator receiving\n");
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
							if (ElevatorSubsystem.verbose)
								Printer.print("--->[data] Elevator waiting\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				FloorRequest floorRequest = FloorRequest.deserialize(receivedBytes.value);
				if (ElevatorSubsystem.verbose)
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
//			Printer.print("got ack lock");

//			while (waitingOnAcknowledgement.value) {
//				Printer.print("wait on ack");
//				try {
//					waitingOnAcknowledgement.wait();
//				} catch (InterruptedException e) {
//				}
//			}

			if (ElevatorSubsystem.verbose)
				System.out.println("sending " + message + "\n");

			t.send(new RequestHeader(RequestType.SEND_DATA, t.getReceivePort(), elevatorNumber)
					.attachDataBytes(message.serialize()));

//			waitingOnAcknowledgement.value = true;

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {
					if (receivedBytes.value == null) {
						if (ElevatorSubsystem.verbose)
							System.out.println("--->[conf] Elevator receiving\n");
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
							if (ElevatorSubsystem.verbose)
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
	
	public byte getElevatorNumber() {
		return elevatorNumber;
	}

	public Transport getTransport() {
		return t;
	}
}
