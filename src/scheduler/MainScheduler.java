package scheduler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import scheduler.RequestHeader.RequestType;
import util.BlockingList;
import util.ByteUtils;
import util.Transport;
import util.Printer;

public class MainScheduler {

	public static final int PORT_FOR_FLOOR = 63972;
	public static final int PORT_FOR_ELEVATOR = 63973;
	private static final int NUMBER_OF_ELEVATORS = 2;
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
		Printer.print("Floor    -> Elevator socket bound on port " + floorTransport.getReceivePort());
		Printer.print("Elevator -> Floor    socket bound on port " + elevatorTransport.getReceivePort());
		// initialize message objects
		floorsMessages = new BlockingList<>(new ArrayList<>());
		elevatorsMessages = new BlockingList<>(new ArrayList<>());
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
	
	public SchedulerState getState() { return currentState; }
	public List<byte[]> getElevatorMessages() { return elevatorsMessages;}
	public List<byte[]> getFloorMessages() {return floorsMessages;}
	public void setState(SchedulerState state) { currentState = state; };

	public void setVerbose(boolean verbose) {
		MainScheduler.verbose = verbose;
//		floorCommunication.setVerbose(verbose);
//		elevatorCommunication.setVerbose(verbose);
		Transport.setVerbose(verbose);
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
//					if (verbose)
//						System.out.println("waiting for a new packet");
					Object[] request;
					try {
						request = transport.receive();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
					byte[] receivedBytes = (byte[]) request[0];

					RequestHeader requestHeader = RequestHeader.parseFromBytes(receivedBytes);

					// add request to messages
					if (requestHeader.getType() == RequestType.SEND_DATA) {
						synchronized (putList) {
							if (verbose) {
								Printer.print("[" + sourceName + "->Scheduler] Received bytes: "
										+ ByteUtils.toString(receivedBytes));
							}

							int argsLength = receivedBytes.length - RequestHeader.HEADER_SIZE;

							ByteBuffer buffer = ByteBuffer.wrap(receivedBytes, RequestHeader.HEADER_SIZE, argsLength);

							byte[] args = new byte[argsLength];
							buffer.get(args);

							switchState(nextState, args);

//							putList.add(receivedBytes);
//
							// send reply
							transport.send(DEFAULT_REPLY, sourceName, requestHeader.getPortToReplyTo());

//
//							// notify waiting threads that something's been added
//							putList.notifyAll();
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

										if (!getList.isEmpty() && subsystemNumber != null) {

											// check if (elevator) action is needed
											if (!isForFloor) {
												// go through all messages to try and find at least 1
												// request for which action is needed
												for (byte[] reqBytes : getList) {
													FloorRequest req = FloorRequest.deserialize(reqBytes);

													if (verbose)
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
											Printer.print(subsystemNumber + ": []---> " + sourceName
													+ " waiting (try " + (++numTimesWaited) + ")");
										}

										// if there's still nothing to do, wait
										if (getList.isEmpty() || (!isForFloor && !actionNeeded)) {
											try {
												getList.wait();
											} catch (InterruptedException e) {
											}
										}
									}
									if (verbose) {
										Printer.print(subsystemNumber + ": []---> " + sourceName + " ready to send");
									}
									// send message
									if (bytesToSend == null) {
										bytesToSend = getList.remove(0);
									} else {
										getList.remove(bytesToSend);
									}
									transport.send(bytesToSend, sourceName, requestHeader.getPortToReplyTo());
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
//					Printer.print("SCHEDULER SUBSYSTEM: Middleman processing message from " + sourceName + "\n");
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
	
	public void closeComms() {
		floorTransport.close();
		elevatorTransport.close();
	}

	public static void main(String args[]) {

		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(false); // code works now, no need for spam
		mainScheduler.activate();

	}

}
