package scheduler;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import elevator.ElevatorSubsystem;
import scheduler.RequestHeader.RequestType;
import util.Communication.Selector;
import util.Printer;
import util.Transport;

public class ElevatorScheduler implements SchedulerType<ElevatorMessage, FloorRequest> {

	private final byte elevatorNumber;
	private final Transport t;
	private final Object getLock = "get lock";
	private final Object putLock = "put lock";
	private BytesWrapper receivedBytes = new BytesWrapper(null);

	/**
	 * Instantiates the elevator scheduler (lives in elevator-subsystem runtime)
	 * 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public ElevatorScheduler(Integer elevatorNumber) throws UnknownHostException, SocketException {
		this.elevatorNumber = elevatorNumber.byteValue();
		t = new Transport("Elevator");
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_ELEVATOR);
		System.out.println("Elevator send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	@Override
	public FloorRequest get(Selector selector) throws IOException {
		synchronized (getLock) {

			t.send(new RequestHeader(RequestType.GET_DATA, t.getReceivePort(), elevatorNumber).getBytes());

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {

					if (receivedBytes.value == null) {
						if (ElevatorSubsystem.verbose)
							System.out.println("--->[data] Elevator receiving\n");

						receivedBytes.value = (byte[]) t.receive()[0];
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

				return floorRequest;
			}
		}
	}

	@Override
	public void put(ElevatorMessage message) throws IOException {
		synchronized (putLock) {

			if (ElevatorSubsystem.verbose)
				System.out.println("sending " + message + "\n");

			t.send(new RequestHeader(RequestType.SEND_DATA, t.getReceivePort(), elevatorNumber)
					.attachDataBytes(message.serialize()));

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {

					if (receivedBytes.value == null) {
						if (ElevatorSubsystem.verbose)
							System.out.println("--->[conf] Elevator receiving\n");

						receivedBytes.value = (byte[]) t.receive()[0];
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
			}
		}
	}

	public void delay(ElevatorMessage o) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3 / MainScheduler.getNumberOfElevators() * 1000);
					put(o);
				} catch (InterruptedException | IOException e) {
					Printer.print(e);
				}
			}
		}).start();
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
