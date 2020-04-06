package scheduler;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import scheduler.RequestHeader.RequestType;
import util.BlockingList;
import util.ByteUtils;
import util.Printer;
import util.Transport;

public class MainScheduler {

	public static final int PORT_FOR_FLOOR = 63972;
	public static final int PORT_FOR_ELEVATOR = 63973;
	private static final int NUMBER_OF_ELEVATORS = 1;
	private static final byte[] DEFAULT_REPLY = new byte[0];

	static boolean verbose = true;

	private Transport floorTransport;
	private Transport elevatorTransport;
	List<byte[]> floorsMessages;
	List<byte[]> elevatorsMessages;
	List<Integer> decommissionedElevators;

	private boolean active;
	SchedulerState currentState;

	public MainScheduler() throws SocketException, UnknownHostException {
		// create transport instances
		floorTransport = new Transport("Floor<->Scheduler", PORT_FOR_FLOOR, false);
		elevatorTransport = new Transport("Scheduler<->Elevator", PORT_FOR_ELEVATOR, false);

		// set the default destination parameters for client->server and server->client
		floorTransport.setDestinationRole("Floor");
		elevatorTransport.setDestinationRole("Elevator");

		Printer.print("Floor    -> Elevator socket bound on port " + floorTransport.getReceivePort());
		Printer.print("Elevator -> Floor    socket bound on port " + elevatorTransport.getReceivePort());

		// initialize message objects
		floorsMessages = new BlockingList<>(new ArrayList<>());
		elevatorsMessages = new BlockingList<>(new ArrayList<>());
		decommissionedElevators = new ArrayList<Integer>();
	}

	public void activate() {

		// create 2 threads for both directions in the intermediate host
		// (they do the exact same thing, but with different sources/sinks)

		Thread floorThread = new Thread(makeRunnable("Floor", floorTransport, elevatorsMessages, floorsMessages,
				SchedulerState.FORWARD_REQUEST_TO_ELEVATOR, true));

		Thread elevatorThread = new Thread(makeRunnable("Elevator", elevatorTransport, floorsMessages,
				elevatorsMessages, SchedulerState.RECEIVE_MESSAGE_FROM_ELEVATOR, false));

		active = true;

		floorThread.start();
		elevatorThread.start();
	}

	public void deactivate() {
		active = false;
	}

	public static int getNumberOfElevators() {
		return NUMBER_OF_ELEVATORS;
	}

	public SchedulerState getState() {
		return currentState;
	}

	public List<byte[]> getElevatorMessages() {
		return elevatorsMessages;
	}

	public List<byte[]> getFloorMessages() {
		return floorsMessages;
	}

	public List<Integer> getDecommissionedElevators() {
		return decommissionedElevators;
	}

	public void setState(SchedulerState state) {
		currentState = state;
	};

	public void setVerbose(boolean verbose) {
		MainScheduler.verbose = verbose;
		Transport.setVerbose(verbose);
	}

	public boolean elevatorIsDecommissioned(Integer elevatorNumber) {
		synchronized (decommissionedElevators) {
			return elevatorNumber != null && decommissionedElevators.contains(elevatorNumber);
		}
	}

	public synchronized void switchState(SchedulerState state, Object param) {
		while (currentState != null && state.working) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		if (verbose) {
			Printer.print("SCHEDULER SUBSYSTEM: state changing to -> " + state + "\n");
		}
		state.working = true;
		currentState = state;
		state.doWork(this, param);
	}

	private Runnable makeRunnable(String sourceName, Transport transport, List<byte[]> putList, List<byte[]> getList,
			SchedulerState nextState, boolean isForFloor) {
		return new Runnable() {
			@Override
			public void run() {
				while (active) {
					// wait for request
					Object[] request;
					try {
						request = transport.receive();
					} catch (Exception e1) {
						e1.printStackTrace();
						return;
					}
					byte[] receivedBytes = (byte[]) request[0];

					RequestHeader requestHeader = RequestHeader.parseFromBytes(receivedBytes);

					// add request to messages
					if (requestHeader.getType() == RequestType.SEND_DATA) {
						synchronized (putList) {
							if (verbose)
								Printer.print("[" + sourceName + "->Scheduler] Received bytes: "
										+ ByteUtils.toString(receivedBytes));

							int argsLength = receivedBytes.length - RequestHeader.HEADER_SIZE;

							ByteBuffer buffer = ByteBuffer.wrap(receivedBytes, RequestHeader.HEADER_SIZE, argsLength);

							byte[] args = new byte[argsLength];
							buffer.get(args);

							switchState(nextState, args);

							// send reply
							try {
								transport.send(DEFAULT_REPLY, sourceName, requestHeader.getPortToReplyTo());
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						}
					} else {
						Integer subsystemNumber = requestHeader.getSubsystemNumber();
						new Thread(new Runnable() {
							@Override
							public void run() {
								synchronized (getList) {
									// wait for list to be non-empty
									// also wait for the elevator to need to do something
									boolean actionNeeded = false;

									byte[] bytesToSend = null;

									int numTimesWaited = 0;
									while (getList.isEmpty() || (!isForFloor && !actionNeeded)) {

										if (elevatorIsDecommissioned(subsystemNumber))
											return;

										if (!getList.isEmpty() && subsystemNumber != null) {

											// check if (elevator) action is needed
											if (!isForFloor) {
												// go through all messages to try and find at least 1
												// request for which action is needed
												for (byte[] reqBytes : getList) {
													FloorRequest req = FloorRequest.deserialize(reqBytes);

													if (verbose)
														Printer.print(req);

													// if request is needing elevator's input OR
													// if request is assigned to the elevator
													if (req.selectedElevator == subsystemNumber
															|| req.responses[subsystemNumber - 1] == null) {
														actionNeeded = true;
														bytesToSend = reqBytes;
														break;
													}
												}
											}

											if (isForFloor) {
												// TODO check if (floor) has message
											}
										}

										if (verbose) {
											Printer.print(subsystemNumber + ": []---> " + sourceName + " waiting (try "
													+ (++numTimesWaited) + ")");
										}

										// if there's still nothing to do, wait
										if (getList.isEmpty() || (!isForFloor && !actionNeeded)) {
											try {
												getList.wait();
											} catch (InterruptedException e) {
											}
										}
									}
									if (elevatorIsDecommissioned(subsystemNumber))
										return;

									if (verbose) {
										Printer.print(subsystemNumber + ": []---> " + sourceName + " ready to send");
									}
									// send message
									if (bytesToSend == null) {
										bytesToSend = getList.remove(0);
									} else {
										getList.remove(bytesToSend);
									}
									try {
										transport.send(bytesToSend, sourceName, requestHeader.getPortToReplyTo());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}).start();
					}
				}
			}
		};
	}

	public void closeComms() {
		floorTransport.close();
		elevatorTransport.close();
	}

	public static void main(String args[]) {

		MainScheduler mainScheduler;
		try {
			mainScheduler = new MainScheduler();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
			return;
		}

		mainScheduler.setVerbose(false);
		mainScheduler.activate();

	}

}
