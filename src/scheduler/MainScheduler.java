package scheduler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

//import util.Communication;
import util.Transport;
import util.ByteUtils;

public class MainScheduler {

	public static final int PORT_FOR_FLOOR = 63972;
	public static final int PORT_FOR_ELEVATOR = 63973;
	private static final int NUMBER_OF_ELEVATORS = 1;
//	private static final byte[] DEFAULT_REPLY = "< msg received >".getBytes();
	private static final byte[] DEFAULT_REPLY = new byte[0];

	static boolean verbose = true;

//	Communication<FloorRequest, ElevatorMessage> floorCommunication;
//	Communication<FloorRequest, ElevatorMessage> elevatorCommunication;
	private Transport floorTransport;
	private Transport elevatorTransport;
	List<byte[]> floorsMessages;
	List<byte[]> elevatorsMessages;

	private boolean active;
	SchedulerState currentState;

	public MainScheduler() {
//		floorCommunication = new Communication<>("Floor", "Scheduler");
//		elevatorCommunication = new Communication<>("Scheduler", "Elevator");
		floorTransport = new Transport("Floor<->Scheduler", PORT_FOR_FLOOR, false);
		elevatorTransport = new Transport("Scheduler<->Elevator", PORT_FOR_ELEVATOR, false);
		// set the default destination parameters for client->server and server->client
		floorTransport.setDestinationRole("Floor");
		elevatorTransport.setDestinationRole("Elevator");
		System.out.println("Floor    -> Elevator socket bound on port " + floorTransport.getReceivePort());
		System.out.println("Elevator -> Floor    socket bound on port " + elevatorTransport.getReceivePort());
		// initialize message objects
		floorsMessages = new ArrayList<>();
		elevatorsMessages = new ArrayList<>();
	}

	public void activate() {
		active = true;
//		new Thread(new Middleman<>(floorCommunication, elevatorCommunication,
//				SchedulerState.FORWARD_REQUEST_TO_ELEVATOR, "Floor")).start();
//		new Thread(new Middleman<>(elevatorCommunication.reverse(), floorCommunication.reverse(),
//				SchedulerState.RECEIVE_MESSAGE_FROM_ELEVATOR, "Elevator")).start();

		// create 2 threads for both directions in the intermediate host
		// (they do the exact same thing, but with different sources/sinks)

		Thread floorThread = new Thread(makeRunnable("Floor", floorTransport, elevatorsMessages, floorsMessages,
				SchedulerState.FORWARD_REQUEST_TO_ELEVATOR, true));

		Thread elevatorThread = new Thread(makeRunnable("Elevator", elevatorTransport, floorsMessages,
				elevatorsMessages, SchedulerState.RECEIVE_MESSAGE_FROM_ELEVATOR, false));

		floorThread.start();
		elevatorThread.start();
	}

	public void deactivate() {
		active = false;
	}

	public static int getNumberOfElevators() {
		return NUMBER_OF_ELEVATORS;
	}

	public void setVerbose(boolean verbose) {
		MainScheduler.verbose = verbose;
//		floorCommunication.setVerbose(verbose);
//		elevatorCommunication.setVerbose(verbose);
		Transport.setVerbose(verbose);
	}

	private synchronized void switchState(SchedulerState state, Object param) {
		while (currentState != null && state.working) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		if (verbose) {
			System.out.println("SCHEDULER SUBSYSTEM: state changing to -> " + state + "\n");
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
//					if (verbose)
//						System.out.println("waiting for a new packet");
					Object[] request = transport.receive();
					byte[] receivedBytes = (byte[]) request[0];

					// add request to messages
					if (receivedBytes.length > 5) {
						synchronized (putList) {
							if (verbose) {
								System.out.println("[" + sourceName + "->Scheduler] Received bytes: "
										+ ByteUtils.toString(receivedBytes));
							}

							ByteBuffer buffer = ByteBuffer.wrap(receivedBytes);

							int responsePort = buffer.getInt();

							byte[] args = new byte[receivedBytes.length - 4];
							buffer.get(args, 0, args.length);

							switchState(nextState, args);

//							putList.add(receivedBytes);
//
							// send reply
							transport.send(DEFAULT_REPLY, sourceName, responsePort);

//
//							// notify waiting threads that something's been added
//							putList.notifyAll();
						}
					} else {
						Integer subsystemNumber;
						if (receivedBytes.length == 1 || receivedBytes.length == 5) {
							subsystemNumber = ((Byte) receivedBytes[0]).intValue();
						} else {
							subsystemNumber = null;
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								synchronized (getList) {
									// wait for list to be non-empty
									// also wait for the elevator to need to do something
									boolean actionNeeded = false;

									byte[] bytesToSend = null;
									while (getList.isEmpty() || !actionNeeded) {

										if (!getList.isEmpty() && subsystemNumber != null) {

											// check if (elevator) action is needed
											if (!isForFloor) {
												// go through all messages to try and find at least 1
												// request for which action is needed
												for (byte[] reqBytes : getList) {
													FloorRequest req = FloorRequest.deserialize(reqBytes);
													
													System.out.println(req);

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
											System.out.println("[]---> " + sourceName + " waiting");
										}

										// if there's still nothing to do, wait
										if (getList.isEmpty() || !actionNeeded) {
											try {
												getList.wait();
											} catch (InterruptedException e) {
											}
										}
									}
									if (verbose) {
										System.out.println("[]---> " + sourceName + " ready to send");
									}
									// send message
									if (bytesToSend == null) {
										bytesToSend = getList.remove(0);
									} else {
										getList.remove(bytesToSend);
									}
									int portToSendTo;
									if (receivedBytes.length == 5) {
										portToSendTo = ByteBuffer.wrap(receivedBytes, 1, 4).getInt();
										System.out.println("rec b = " + receivedBytes[1] + " port = " + portToSendTo);
									} else {
										portToSendTo = ((Byte) request[1]).intValue();
									}
									transport.send(bytesToSend, sourceName, portToSendTo);
								}
							}
						}).start();
//						if (verbose)
//							System.out.println("created thread");
					}
				}
			}
		};
	}

	// communication
	// floor <-> scheduler <-> elevator <-> ... <-> ... <->
	// | |
	// >>>> middleman >>>>
	// <<<< middleman <<<<
//	private class Middleman<X, Y, Z> implements Runnable {
//		private Communication<X, Y> source;
//		private Communication<X, Z> sink;
//		private SchedulerState state;
//		private String sourceName;
//
//		private Middleman(Communication<X, Y> source, Communication<X, Z> sink, SchedulerState state,
//				String sourceName) {
//			this.source = source;
//			this.sink = sink;
//			this.state = state;
//			this.sourceName = sourceName;
//		}
//
//		@Override
//		public void run() {
//			while (active) {
//
//				X obj = source.bGet();
//
//				if (verbose) {
//					System.out.println("SCHEDULER SUBSYSTEM: Middleman processing message from " + sourceName + "\n");
//				}
//
//				switchState(state, obj);
//
////				sink.aPut(obj);
//
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//				}
//			}
//		}
//
//	}

}
