package scheduler;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import scheduler.RequestHeader.RequestType;
import util.Communication.Selector;
import util.Printer;
import util.Transport;

public class FloorsScheduler implements SchedulerType<FloorMessage, ElevatorMessage> {

	private final byte floorNumber;
	private final Transport t;
	private final Object getLock = "get lock";
	private final Object putLock = "put lock";
	private BytesWrapper receivedBytes = new BytesWrapper(null);

	/**
	 * Instantiates the floor scheduler (lives in floor-subsystem runtime)
	 * 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public FloorsScheduler(Integer floorNumber) throws UnknownHostException, SocketException {
		this.floorNumber = floorNumber.byteValue();
		t = new Transport("Floor");
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_FLOOR);
		Printer.print("Floor send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	/**
	 * Synchronized retrieval of ElevatorMessages through UDP 
	 * 
	 * @param selector Selector interface for information passed between scheduler and subsystems, not used here. 
	 * 
	 * @return elevatorRequest ElevatorMessage a request from the ElevatorSubsystem.
	 */
	@Override
	public ElevatorMessage get(Selector selector) throws IOException {
		synchronized (getLock) {

			t.send(new RequestHeader(RequestType.GET_DATA, t.getReceivePort(), floorNumber).getBytes());

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {

					if (receivedBytes.value == null) {
						Printer.print("--->[data] Floor receiving\n");

						receivedBytes.value = (byte[]) t.receive()[0];
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

				Printer.print("floorInt: " + elevatorMessage.getFloorArrivedOn());

				return elevatorMessage;
			}
		}
	}
	
	/**
	 * Sends FloorMessages through UDP and waits for confirmation message (receivedBytes).
	 * 
	 * @param request FloorMessage A message that the Floor sends. 
	 */
	@Override
	public void put(FloorMessage request) throws IOException {
		synchronized (putLock) {

			System.out.println("sending " + request + "\n");

			t.send(new RequestHeader(RequestType.SEND_DATA, t.getReceivePort(), floorNumber)
					.attachDataBytes(request.serialize()));

			// receive confirmation of message received

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length > 0) {
					if (receivedBytes.value == null) {
						System.out.println("--->[conf] Floor receiving\n");

						receivedBytes.value = (byte[]) t.receive()[0];
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
			}
		}
	}

	private static class BytesWrapper {
		private byte[] value;

		private BytesWrapper(byte[] value) {
			this.value = value;
		}
	}

	/**
	 * Gets elevator number.
	 * 
	 * @return elevatorNumber byte number representing an elevator. 
	 */
	public byte getElevatorNumber() {
		return floorNumber;
	}
	
	/**
	 * Gets transport.
	 * 
	 * @return t Transport. 
	 */
	public Transport getTransport() {
		return t;
	}

}
