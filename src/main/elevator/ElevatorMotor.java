package main.elevator;

final class ElevatorMotor implements Runnable {

	private final ElevatorState elevatorState;
	boolean running;
	private boolean taskAssigned;

	ElevatorMotor(ElevatorState elevatorState) {
		this.elevatorState = elevatorState;
	}

	@Override
	public void run() {
		this.elevatorState.velocity = ElevatorState.ACCELERATION;
		this.elevatorState.metresTravelled = 0;
		while (this.elevatorState.elevator.poweredOn && (!this.elevatorState.elevator.workDoing.isEmpty()
				|| !this.elevatorState.elevator.workToDo.isEmpty())) {

			Integer targetFloor;
			boolean isWorkToDo = false;
			if ((targetFloor = this.elevatorState.elevator.workDoing.peek()) == null) {
				targetFloor = this.elevatorState.elevator.workToDo.peek();
				isWorkToDo = true;
			}
			this.elevatorState.direction = targetFloor > this.elevatorState.currentFloor ? 1 : -1;

			if (!taskAssigned) {
				// System.out.println("Starting a task (-> " + targetFloor + ")");
			}
			taskAssigned = true;

			float distanceToFloor = Math.abs(targetFloor - this.elevatorState.elevator.state.currentFloor)
					* ElevatorState.FLOOR_HEIGHT;
			float secondsToFloor = distanceToFloor == 0 ? 0
					: distanceToFloor / this.elevatorState.elevator.state.velocity;

//					System.out.print(nextFloor + ", " + distanceToFloor + ", " + secondsToFloor + " {} ");

			if (secondsToFloor - 1 < this.elevatorState.elevator.state.secondsToStop()) {
				if (this.elevatorState.elevator.state.currentFloor == targetFloor) {
					// System.out.println("\nArrived at floor " + targetFloor);

					this.elevatorState.velocity = ElevatorState.ACCELERATION;
					this.elevatorState.metresTravelled = 0;

					this.elevatorState.elevator.doors.openDoors();
					// TODO allow people to press button just in time and open doors again
					this.elevatorState.elevator.doors.closeDoors();

					if (isWorkToDo) {
						this.elevatorState.elevator.workToDo.poll();
					} else {
						this.elevatorState.elevator.workDoing.poll();
					}

					// System.out.println(
					// "\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " +
					// workToDo);

					if ((targetFloor = this.elevatorState.elevator.workDoing.peek()) == null) {
						targetFloor = this.elevatorState.elevator.workToDo.peek();
					}

					// System.out.println("Moving towards floor " + targetFloor);
				} else {
					if (this.elevatorState.velocity == ElevatorState.TERMINAL_VELOCITY) {
						System.out.print(".");// + currentFloor
					} else {
						System.out.print("-");
					}

					this.elevatorState.decelerate();
				}
			} else {
				if (this.elevatorState.velocity == ElevatorState.TERMINAL_VELOCITY) {
					System.out.print(".");
				} else {
					System.out.print("+");
				}

				this.elevatorState.accelerate();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		running = false;
		System.out.println("\nElevator is sleeping");
	}
}