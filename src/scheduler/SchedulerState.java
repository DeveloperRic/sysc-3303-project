package scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
			FloorRequest request = (FloorRequest) param;
			request.responses = new Float[MainScheduler.getNumberOfElevators()];
			request.numResponses = 0;
			// forward request to elevator
			m.elevatorCommunication.aPut(request);
			notifyDone(m);
		}
	}),

	RECEIVE_MESSAGE_FROM_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			ElevatorMessage message = (ElevatorMessage) param;
			if (message.getFloorRequest() != null) {
				FloorRequest request = message.getFloorRequest();
				if (request.getSourceElevator() == null) {
					changeTo(m, PROCESS_ETA_FROM_ELEVATOR, request);
				} else {
					changeTo(m, SEND_ACKNOWLEDGEMENT_TO_ELEVATOR,
							new Object[] { request, request.getSourceElevator() });
				}
			} else if (message.getAcknowledgement() != null) {
				changeTo(m, FORWARD_ACKNOWLEDGEMENT_TO_FLOOR, message);
			} else {
				notifyDone(m);
			}
		}
	}),

	PROCESS_ETA_FROM_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			FloorRequest request = (FloorRequest) param;
			// increment number of eta responses
			// wait for responses to come in from all elevators
			if (request.numResponses == request.responses.length) {
				List<Float> responses = new ArrayList<Float>(request.responses.length);
				// subtract 2 seconds (for processing delay)
				for (int i = 0; i < request.responses.length; ++i) {
					responses.add(request.responses[i] -= 2);
				}
				// pick the smallest (non-negative) eta
				for (int i = 0; i < responses.size(); ++i) {
					if (responses.get(i) < 0) {
						responses.set(i, Collections.max(responses) - responses.get(i));
					}
				}
				int selectedElevator = responses.indexOf(Collections.min(responses)) + 1;
				// message elevator it has been picked
				changeTo(m, SEND_ACKNOWLEDGEMENT_TO_ELEVATOR, new Object[] { request, selectedElevator });
			} else {
				m.elevatorCommunication.aPut(request);
				notifyDone(m);
			}
		}
	}),

	SEND_ACKNOWLEDGEMENT_TO_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			Object[] params = ((Object[]) param);
			FloorRequest request = (FloorRequest) params[0];
			// prioritize selected elevator
			request.selectedElevator = (int) params[1];
			// send acknowledgement to elevator
			if (MainScheduler.verbose) {
				System.out.println("SCHEDULER SUBSYSTEM: Selected elevator " + request.selectedElevator + " for task\n"
						+ " Content: " + request + "\n");
			}
			m.elevatorCommunication.aPut(request);
			notifyDone(m);
		}
	}),

	FORWARD_ACKNOWLEDGEMENT_TO_FLOOR(new State() {

		@Override
		public void doWork(MainScheduler m, Object param) {
			m.floorCommunication.bPut((ElevatorMessage) param);
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
			System.out.println("SCHEDULER SUBSYSTEM: state changing to -> " + nextState + "\n");
		}
		nextState.working = true;
		m.currentState = nextState;
		nextState.doWork(m, param);
	}

	private static void notifyDone(MainScheduler m) {
		m.currentState.working = false;
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