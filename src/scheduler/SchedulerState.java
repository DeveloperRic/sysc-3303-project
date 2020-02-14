package scheduler;

public enum SchedulerState {

	WAIT_FOR_INPUT(new State() {

		// waiting for some input
		@Override
		public void doWork(MainScheduler m) {
			System.out.println("WAITING STATE");
			// if input is request from floor
			// go to ReceiveRequestFromFloor
			// if input is acknowledgement from elevator
			// go to ReceiveAcknowledgementFromElevator
			// if input is elevator update
			// go to ReceiveUpdateFromElevator
			if (m.isFloorRequest()) {
				changeTo(m, RECEIVE_REQUEST_FROM_FLOOR);
			} else if (m.isElevatorAck()) {
				changeTo(m, RECEIVE_ACKNOWLEDGEMENT_FROM_ELEVATOR);
			} else if (m.isElevatorUpdate()) {
				changeTo(m, RECEIVE_UPDATE_FROM_ELEVATOR);
			} else {
				System.out.println("big problem");
			}
		}
	}),

	RECEIVE_REQUEST_FROM_FLOOR(new State() {

		@Override
		public void doWork(MainScheduler m) {
			System.out.println("RECEIVE REQUEST FROM FLOOR STATE");
			// receive the request
			// do some math
			// if the elevator can go to a requested floor, send the request
			
			Integer[] o = m.pendingRequests.get(0);
			
			if (m.elevatorStatus.addToQueue(o)) {
				changeTo(m, SEND_REQUEST_TO_ELEVATOR);
			} else {
				changeTo(m, WAIT_FOR_INPUT);
			}
		}
	}),

	SEND_REQUEST_TO_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m) {
			System.out.println("SEND REQUEST TO ELEVATOR STATE");
			// send the request to the elevator to come to floor (floor number, direction)
			// send tasks that have been added to a sending queue based on the doMath
			// function
			while (!m.elevatorStatus.workDoing.isEmpty()) {
				m.elevatorCommunication.aPut(m.elevatorStatus.workDoing.toArray());
			}
			// go into waiting state
			changeTo(m, WAIT_FOR_INPUT);
		}
	}),

	RECEIVE_ACKNOWLEDGEMENT_FROM_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m) {
			System.out.println("RECEIVE ACKNOWLEDGEMENT FROM ELEVATOR STATE " + m.elevatorAcknowledgement);
			// use the acknowledgement to update stuff
			// go to SendAcknowledgementToFloor
			changeTo(m, SEND_ACKNOWLEDGEMENT_TO_FLOOR);
		}
	}),

	SEND_ACKNOWLEDGEMENT_TO_FLOOR(new State() {

		@Override
		public void doWork(MainScheduler m) {
			System.out.println("SEND ACKNOWLEDGEMENT TO FLOOR STATE");
			m.floorCommunication.bPut(m.elevatorAcknowledgement);
			changeTo(m, WAIT_FOR_INPUT);
		}
	}),

	RECEIVE_UPDATE_FROM_ELEVATOR(new State() {

		@Override
		public void doWork(MainScheduler m) {
			System.out.println("RECEIVE UPDATE FROM ELEVATOR STATE");
			// use the update from the elevator to update some things
			// if it finished all assigned work, give it new work from doMath
			// m.doMath();
			// if it reached a floor it wants, tell floor it's here
			// if it just needs to send a command to elevator, send command to elevator
			m.elevatorStatus.update(m.elevatorStatusUpdate);
			changeTo(m, SEND_UPDATE_TO_FLOOR);
			// m.setState(new SendElevatorCommand());
		}
	}),

	SEND_UPDATE_TO_FLOOR(new State() {

		@Override
		public void doWork(MainScheduler m) {
			System.out.println("SEND UPDATE TO FLOOR STATE");
			// send the update to the floor that elevator has arrived
			// go to send command to elevator state
			if (m.elevatorStatus.previousFloor != m.elevatorStatus.currentFloor) {
				m.elevatorStatus.previousFloor = m.elevatorStatus.currentFloor;
				m.floorCommunication.bPut("RECEIVE ACKNOWLEDGEMENT FROM SCHEDULER floor = " + m.elevatorStatus.currentFloor);
			}
			changeTo(m, WAIT_FOR_INPUT);
		}
	});

	private State state;

	SchedulerState(State state) {
		this.state = state;
	}

	private static void changeTo(MainScheduler m, SchedulerState nextState) {
		m.setState(nextState);
		nextState.doWork(m);
	}

	private interface State {
		void doWork(MainScheduler m);
	}

	public void doWork(MainScheduler m) {
		state.doWork(m);
	}
}
