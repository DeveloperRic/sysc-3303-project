package main.elevator;

final class ElevatorMotor implements Runnable {

	private final Elevator elevatorState;
	boolean running;
	private boolean taskAssigned;

	ElevatorMotor(Elevator elevatorState) {
		this.elevatorState = elevatorState;
	}

	@Override
	public void run() {
		this.elevatorState.velocity = Elevator.ACCELERATION;
		this.elevatorState.metresTravelled = 0;
		while (this.elevatorState.subsystem.poweredOn && (!this.elevatorState.subsystem.workDoing.isEmpty())) {
//				|| !this.elevatorState.subsystem.workToDo.isEmpty())) {

			Integer targetFloor;
			boolean isWorkToDo = false;
			if ((targetFloor = this.elevatorState.subsystem.workDoing.peek()) == null) {
//				targetFloor = this.elevatorState.subsystem.workToDo.peek();
//				isWorkToDo = true;
				break;
			}
			this.elevatorState.direction = targetFloor > this.elevatorState.currentFloor ? 1 : -1;

			if (!taskAssigned) {
				// System.out.println("Starting a task (-> " + targetFloor + ")");
			}
			taskAssigned = true;

			float distanceToFloor = Math.abs(targetFloor - this.elevatorState.currentFloor) * Elevator.FLOOR_HEIGHT;
			float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / this.elevatorState.velocity;

//					System.out.print(nextFloor + ", " + distanceToFloor + ", " + secondsToFloor + " {} ");

			if (secondsToFloor - 1 < this.elevatorState.secondsToStop()) {
				if (this.elevatorState.currentFloor == targetFloor) {
					// System.out.println("\nArrived at floor " + targetFloor);

					this.elevatorState.velocity = Elevator.ACCELERATION;
					this.elevatorState.metresTravelled = 0;

					this.elevatorState.doors.openDoors();
					// TODO allow people to press button just in time and open doors again
					this.elevatorState.doors.closeDoors();

					if (isWorkToDo) {
//						this.elevatorState.subsystem.workToDo.poll();
					} else {
						this.elevatorState.subsystem.workDoing.poll();
					}

					// System.out.println(
					// "\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " +
					// workToDo);

//					if ((targetFloor = this.elevatorState.subsystem.workDoing.peek()) == null) {
//						targetFloor = this.elevatorState.subsystem.workToDo.peek();
//					}

					// System.out.println("Moving towards floor " + targetFloor);
				} else {
					if (this.elevatorState.velocity == Elevator.MAX_VELOCITY) {
						System.out.print(".");// + currentFloor
					} else {
						System.out.print("-");
					}

					this.decelerate(this.elevatorState);
				}
			} else {
				if (this.elevatorState.velocity == Elevator.MAX_VELOCITY) {
					System.out.print(".");
				} else {
					System.out.print("+");
				}

				this.accelerate(this.elevatorState);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		running = false;
		System.out.println("\nElevator is sleeping");
	}

	void accelerate(Elevator elevator) {
		if (!elevator.isMoving())
			return;
		doMovement(elevator,
				elevator.velocity = Math.min(elevator.velocity + Elevator.ACCELERATION, Elevator.MAX_VELOCITY));
	}

	void decelerate(Elevator elevator) {
		if (!elevator.isMoving())
			return;
		doMovement(elevator, elevator.velocity = Math.max(elevator.velocity - Elevator.ACCELERATION, 0));
	}

	void doMovement(Elevator elevator, float velocity) {
		elevator.metresTravelled += velocity;

		if (elevator.metresTravelled >= Elevator.FLOOR_HEIGHT) {
			elevator.currentFloor += elevator.direction;
			elevator.metresTravelled = 0;
			System.out.print("|");
		}
	}
}