package elevatorSubsystem;

import elevatorSubsystem.ElevatorSubsystem.ElevatorState;

final class ElevatorMotor implements Runnable {

	private final Elevator elevator;
	boolean running;
	private boolean taskAssigned;

	ElevatorMotor(Elevator elevator) {
		this.elevator = elevator;
	}

	@Override
	public void run() {
		this.elevator.velocity = Elevator.ACCELERATION;
		this.elevator.metresTravelled = 0;
		this.elevator.notifyStatus();
		while (this.elevator.subsystem.poweredOn && (!this.elevator.subsystem.workDoing.isEmpty())) {
//				|| !this.elevatorState.subsystem.workToDo.isEmpty())) {

			
			
			Integer targetFloor;
			boolean isWorkToDo = false;
			if ((targetFloor = this.elevator.subsystem.workDoing.get(0)) == null) {
//				targetFloor = this.elevatorState.subsystem.workToDo.peek();
//				isWorkToDo = true;
				break;
			}

			this.elevator.direction = targetFloor > this.elevator.currentFloor ? 1 : -1;

			if (!taskAssigned) {
				// System.out.println("Starting a task (-> " + targetFloor + ")");
			}
			taskAssigned = true;

			float distanceToFloor = Math.abs(targetFloor - this.elevator.currentFloor) * Elevator.FLOOR_HEIGHT;
			float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / this.elevator.velocity;

//					System.out.print(nextFloor + ", " + distanceToFloor + ", " + secondsToFloor + " {} ");

			if (secondsToFloor - 1 < this.elevator.secondsToStop()) {
				if (this.elevator.currentFloor == targetFloor) {
					// System.out.println("\nArrived at floor " + targetFloor);
					
					

					this.elevator.velocity = Elevator.ACCELERATION;
					this.elevator.metresTravelled = 0;

					this.elevator.doors.openDoors();
					// TODO allow people to press button just in time and open doors again
					this.elevator.doors.closeDoors();

					if (isWorkToDo) {
//						this.elevatorState.subsystem.workToDo.poll();
					} else {
						this.elevator.subsystem.workDoing.remove(0);
					}

					elevator.subsystem.notifyArrivedAtFloor(targetFloor);
					
					// System.out.println(
					// "\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " +
					// workToDo);

//					if ((targetFloor = this.elevatorState.subsystem.workDoing.peek()) == null) {
//						targetFloor = this.elevatorState.subsystem.workToDo.peek();
//					}

					// System.out.println("Moving towards floor " + targetFloor);
				} else {
					if (this.elevator.velocity == Elevator.MAX_VELOCITY) {
						System.out.print(".");// + currentFloor
					} else {
						System.out.print("-");
					}

					this.decelerate(this.elevator);
					this.elevator.subsystem.currentState = ElevatorState.DECELERATING;
				}
			} else {
				if (this.elevator.velocity == Elevator.MAX_VELOCITY) {
					System.out.print(".");
				} else {
					System.out.print("+");
				}

				this.accelerate(this.elevator);
				
				this.elevator.subsystem.currentState = ElevatorState.ACCELERATING;
			}
			
			this.elevator.notifyStatus();
			
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
		elevator.velocity = Math.min(elevator.velocity + Elevator.ACCELERATION, Elevator.MAX_VELOCITY);
		if (elevator.velocity == Elevator.MAX_VELOCITY) {
			this.elevator.subsystem.currentState = ElevatorState.MAX_SPEED;
		}
		doMovement(elevator, elevator.velocity);
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
