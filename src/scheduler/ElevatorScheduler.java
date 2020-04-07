package scheduler;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Supplier;

import elevator.ElevatorSubsystem;
import main.Task;
import scheduler.RequestHeader.RequestType;
import util.Communication.Selector;
import util.Printer;
import util.Transport;

public class ElevatorScheduler implements SchedulerType<ElevatorMessage, FloorMessage> {

	private final byte elevatorNumber;
	private final Transport t;
	private final Object getLock = "get lock";
	private final Object putLock = "put lock";
	private BytesWrapper receivedBytes = new BytesWrapper(null);
	private Supplier<Boolean> isPoweredOnSupplier;

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

	/**
	 * copy constructor
	 * 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public ElevatorScheduler(ElevatorScheduler scheduler) throws UnknownHostException, SocketException {
		elevatorNumber = scheduler.elevatorNumber;
		t = new Transport("Elevator");
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_ELEVATOR);
		System.out.println("Elevator send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	/**
	 * Synchronized retrieval of floorMessages through UDP 
	 * 
	 * @param selector Selector interface for information passed between scheduler and subsystems, not used here. 
	 * 
	 * @return floorRequest floorMessage a request from the FloorSubsystem.
	 */
	@Override
	public FloorMessage get(Selector selector) throws IOException {
		if (!elevatorIsPoweredOn())
			return null;

		synchronized (getLock) {

			t.send(new RequestHeader(RequestType.GET_DATA, t.getReceivePort(), elevatorNumber).getBytes());

			FloorMessage floorRequest;

			synchronized (receivedBytes) {

				while (receivedBytes.value == null || receivedBytes.value.length == 0) {

					if (!elevatorIsPoweredOn())
						return null;

					if (receivedBytes.value == null) {
						if (ElevatorSubsystem.verbose)
							System.out.println("--->[data] Elevator open for receiving data\n");

						receivedBytes.value = (byte[]) t.receive()[0];
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length == 0) {
						try {
							if (ElevatorSubsystem.verbose)
								Printer.print("--->[data] Elevator waiting for data\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				floorRequest = FloorMessage.deserialize(receivedBytes.value);
				if (ElevatorSubsystem.verbose)
					Printer.print("Received " + floorRequest + "\n");

				if (elevatorIsPoweredOn()) {
					receivedBytes.value = null;

					return floorRequest;
				}
			}

			// attempt to rescue lost request by bouncing it back to the scheduler
			if (floorRequest.getTask() == null) {
				Integer[] req = floorRequest.getRequest();
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
				put(new ElevatorMessage() {
					@Override
					public FloorMessage getFloorRequest() {
						return request;
					}
				});
			} else {
				put(new ElevatorMessage() {
					@Override
					public FloorMessage getFloorRequest() {
						return floorRequest;
					}
				});
			}
			return null;
		}
	}
	
	/**
	 * Sends ElevatorMessages through UDP and waits for confirmation message (receivedBytes).
	 * 
	 * @param message ElevatorMessage A message that the elevator sends. 
	 */
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

					if (!elevatorIsPoweredOn())
						return;

					if (receivedBytes.value == null) {
						if (ElevatorSubsystem.verbose)
							System.out.println("--->[conf] Elevator open for receiving conf\n");

						receivedBytes.value = (byte[]) t.receive()[0];
						receivedBytes.notifyAll();
					}

					if (receivedBytes.value.length > 0) {
						try {
							if (ElevatorSubsystem.verbose)
								Printer.print("--->[conf] Elevator waiting for conf\n");
							receivedBytes.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				if (!elevatorIsPoweredOn())
					return;

				receivedBytes.value = null;
				receivedBytes.notifyAll();
			}
		}
	}
	
	/**
	 * Sleeps thread to delay it before putting the ElevatorMessage.
	 * 
	 * @param o ElevatorMessage A message that the elevator sends. 
	 */
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

	/**
	 * Gets elevator number.
	 * 
	 * @return elevatorNumber byte number representing an elevator. 
	 */
	public byte getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Gets transport.
	 * 
	 * @return t Transport. 
	 */
	public Transport getTransport() {
		return t;
	}

	/**
	 * Sets the check to see if elevator is powered on to a new passed value.
	 * 
	 * @param isPoweredOnSupplier Supplier<Boolean> what is being check for isPoweredOn(). 
	 */
	public void setIsPoweredOnSupplier(Supplier<Boolean> isPoweredOnSupplier) {
		this.isPoweredOnSupplier = isPoweredOnSupplier;
	}

	/**
	 * Checks if elevator is powered on.
	 * 
	 * @return on boolean true if elevator is turned on. 
	 */
	private boolean elevatorIsPoweredOn() {
		boolean on = isPoweredOnSupplier.get();
		if (!on) {
			Printer.print("ELEVATOR_SCHEDULER: Elevator was powered off, attempting to cancel processes");
		}
		return on;
	}
}
