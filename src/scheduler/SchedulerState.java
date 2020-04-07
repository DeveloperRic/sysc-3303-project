package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import util.Printer;

public enum SchedulerState {

	WAIT_FOR_INPUT(new State() {

		@Override
		public void doWork(MainScheduler m, Object v) {
			// wait for some input
		}
	}),

	FORWARD_REQUEST_TO_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			// receive the request from floor
			FloorMessage request = FloorMessage.deserialize((byte[]) param);
			if (request.getSourceElevator() == null) {
				request.responses = new Float[MainScheduler.getNumberOfElevators()];
				request.numResponses = 0;
				// forward request to elevator
				m.elevatorsMessages.add(request.serialize());
				notifyDone(m, m.elevatorsMessages);
			} else {
				changeTo(m, SEND_ACKNOWLEDGEMENT_TO_ELEVATOR, new Object[] { request, request.getSourceElevator() });
			}
		}
	}),

	RECEIVE_MESSAGE_FROM_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			ElevatorMessage message = ElevatorMessage.deserialize((byte[]) param);
			if (message.getFaultNotice() != null) {
				if (message.getFaultNotice()[0] == 1) {
					changeTo(m, RECOMMISSION_ELEVATOR, message.getFaultNotice()[1]);
				} else {
					changeTo(m, DECOMMISSION_ELEVATOR, message);
				}
			} else if (message.getFloorRequest() != null) {
				FloorMessage request = message.getFloorRequest();
				if (request.getSourceElevator() == null) {
					changeTo(m, PROCESS_ETA_FROM_ELEVATOR, request);
				} else {
					changeTo(m, SEND_ACKNOWLEDGEMENT_TO_ELEVATOR,
							new Object[] { request, request.getSourceElevator() });
				}
			} else if (message.getAcknowledgement() != null) {
				changeTo(m, FORWARD_ACKNOWLEDGEMENT_TO_FLOOR, param);
			} else {
				notifyDone(m);
			}
		}
	}),

	PROCESS_ETA_FROM_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			FloorMessage request = (FloorMessage) param;
			// increment number of eta responses
			// wait for responses to come in from all elevators

			int numOfNeededResponses = request.responses.length - m.decommissionedElevators.size();

			if (request.numResponses == numOfNeededResponses) {

				List<Float[]> responses = new ArrayList<>(numOfNeededResponses);
				boolean noElevatorCanProcess = true;

				// subtract 2 seconds (for processing delay)
				for (int i = 0; i < request.responses.length; ++i) {
					if (m.decommissionedElevators.contains(i + 1))
						continue;

					if (MainScheduler.verbose) {
						Printer.print(Arrays.toString(request.responses.clone()));
					}

					if (request.responses[i] >= 0) {
						noElevatorCanProcess = false;
					}

					responses.add(new Float[] { request.responses[i] -= 2, (float) (i + 1) });
				}

				if (!noElevatorCanProcess) {

					// pick the smallest (non-negative) eta
					for (int i = 0; i < responses.size(); ++i) {
						Float[] response = responses.get(i);
						if (response[0] < 0) {
							responses.set(i,
									new Float[] { Collections
											.max(responses.stream().map(r -> r[0]).collect(Collectors.toList()))
											- response[0], response[1] });
						}
					}

					int selectedElevator = responses.stream().sorted(new Comparator<Float[]>() {
						@Override
						public int compare(Float[] o1, Float[] o2) {
							return o1[0] < o2[0] ? -1 : 0;
						}
					}).findFirst().get()[1].intValue();

					// message elevator it has been picked
					changeTo(m, SEND_ACKNOWLEDGEMENT_TO_ELEVATOR, new Object[] { request, selectedElevator });
				} else {

					changeTo(m, FORWARD_REQUEST_TO_ELEVATOR, request.serialize());
				}
			} else {
				m.elevatorsMessages.add(request.serialize());
				notifyDone(m, m.elevatorsMessages);
			}
		}
	}),

	SEND_ACKNOWLEDGEMENT_TO_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			Object[] params = ((Object[]) param);
			FloorMessage request = (FloorMessage) params[0];
			// prioritize selected elevator
			request.selectedElevator = (int) params[1];
			// send acknowledgement to elevator
			if (MainScheduler.verbose) {
				Printer.print("SCHEDULER SUBSYSTEM: Selected elevator " + request.selectedElevator + " for task\n"
						+ " Content: " + request + "\n");
			}
			m.elevatorsMessages.add(request.serialize());
			notifyDone(m, m.elevatorsMessages);
		}
	}),

	FORWARD_ACKNOWLEDGEMENT_TO_FLOOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			m.floorsMessages.add((byte[]) param);
			notifyDone(m, m.floorsMessages);
		}
	}),

	DECOMMISSION_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			ElevatorMessage message = (ElevatorMessage) param;
			Integer elevatorNumber = message.getFaultNotice()[1];

			Printer.print("SCHEDULER SUBSYSTEM: DEcomissioning elevator " + elevatorNumber
					+ " and redirecting its last task\n" + " Content: " + message.getFloorRequest() + "\n");

			synchronized (m.decommissionedElevators) {
				m.decommissionedElevators.add(elevatorNumber);
			}

			if (message.getFloorRequest() != null) {
				changeTo(m, FORWARD_REQUEST_TO_ELEVATOR, message.getFloorRequest().serialize());
			} else {
				notifyDone(m);
			}
		}
	}),

	RECOMMISSION_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			Printer.print("SCHEDULER SUBSYSTEM: REcomissioning elevator " + param + "\n");

			synchronized (m.decommissionedElevators) {
				m.decommissionedElevators.remove((Integer) param);
			}
			notifyDone(m);
		}
	});

	private State state;
	boolean working;

	SchedulerState(State state) {
		this.state = state;
	}

	private static void changeTo(MainScheduler m, SchedulerState nextState, Object param) {
		m.currentState.working = false;
		if (MainScheduler.verbose) {
			Printer.print("SCHEDULER SUBSYSTEM: state changing to -> " + nextState + "\n");
		}
		nextState.working = true;
		m.currentState = nextState;
		nextState.doWork(m, param);
	}

	private static void notifyDone(MainScheduler m) {
		notifyDone(m, null);
	}

	private static void notifyDone(MainScheduler m, Object objectToNotify) {
		if (objectToNotify != null) {
			synchronized (objectToNotify) {
				objectToNotify.notifyAll();
			}
		}
		m.currentState.working = false;
		Printer.print("SCHEDULER SUBSYSTEM: state changing to -> " + WAIT_FOR_INPUT + "\n");
		m.currentState = WAIT_FOR_INPUT;
		m.currentState.working = false;
		m.notifyAll();
	}

	private interface State {
		void doWork(MainScheduler m, Object param);
	}

	public void doWork(MainScheduler m, Object param) {
		state.doWork(m, param);
	}
}