package main.elevator;

import main.Task;

public class ElevatorState {

	static final float ACCELERATION = 0.68f;
	static final float TERMINAL_VELOCITY = 4.31f;
	static final float FLOOR_HEIGHT = 3.23f; // This should be a function call to Floor but whatever for now

	final ElevatorSubsystem elevator;

	private ElevatorMotor motor;
	int currentFloor;
	float velocity;
	int direction; // 1 is up, -1 is down
	float metresTravelled; // per floor, NOT total

	ElevatorState(ElevatorSubsystem elevator) {
		this.elevator = elevator;
		motor = new ElevatorMotor(this);
		currentFloor = 1;
		velocity = ACCELERATION; // i can't prove it but velocity must be >= ACCELERATION
		metresTravelled = 0;
	}

	public boolean isMoving() {
		return velocity > 0;
	}

	void accelerate() {
		if (!isMoving())
			return;
		doMovement(velocity = Math.min(velocity + ACCELERATION, TERMINAL_VELOCITY));
	}

	void decelerate() {
		if (!isMoving())
			return;
		doMovement(velocity = Math.max(velocity - ACCELERATION, 0));
	}

	private void doMovement(float velocity) {
		metresTravelled += velocity;

		if (metresTravelled >= FLOOR_HEIGHT) {
			currentFloor += direction;
			metresTravelled = 0;
			System.out.print("|");
		}
	}

	float secondsToStop() {
		return velocity <= ACCELERATION ? 0 : velocity / ACCELERATION;
	}

	public boolean canStopAtFloor(int floor) {
		if (direction == 0)
			return true;
		if ((this.elevator.state.direction == -1 && floor > this.elevator.state.currentFloor)
				|| (this.elevator.state.direction == 1 && floor < this.elevator.state.currentFloor)) {
			return false;
		}
		if (!this.elevator.state.isMoving() || this.elevator.state.currentFloor == floor)
			return true;

		float distanceToFloor = Math.abs(floor - this.elevator.state.currentFloor) * FLOOR_HEIGHT;
		float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / this.elevator.state.velocity;
		return secondsToFloor >= this.elevator.state.secondsToStop();
	}

	public synchronized void assignTask(Task task) {
		// System.out.println("Elevator Received Task");
		if (canStopAtFloor(task.getStartFloor())) {

			if (currentFloor != task.getStartFloor() && !this.elevator.workDoing.contains(task.getStartFloor())) {
				this.elevator.workDoing.add(task.getStartFloor());
			}
			if (currentFloor != task.getDestinationFloor()
					&& !this.elevator.workDoing.contains(task.getDestinationFloor())) {
				this.elevator.workDoing.add(task.getDestinationFloor());
			}

			// System.out.println("Added task to workDoing (" + task.getStartFloor() + " ->
			// " + task.getDestinationFloor() + ")");

			// System.out.println("\nelev: " + state.currentFloor + "\ndoing: " + workDoing
			// + "\ntodo: " + workToDo);

			if (!isAwake())
				wakeup();
		} else {
			if (currentFloor != task.getStartFloor() && !this.elevator.workToDo.contains(task.getStartFloor())) {
				this.elevator.workToDo.add(task.getStartFloor());
			}
			if (currentFloor != task.getDestinationFloor()
					&& !this.elevator.workToDo.contains(task.getDestinationFloor())) {
				this.elevator.workToDo.add(task.getDestinationFloor());
			}

			// System.out.println("Added task to workToDo (" + task.getStartFloor() + " -> "
			// + task.getDestinationFloor() + ")");

			// System.out.println("\nelev: " + state.currentFloor + "\ndoing: " + workDoing
			// + "\ntodo: " + workToDo);
		}
	}

	public boolean isAwake() {
		return motor.running;
	}

	private synchronized void wakeup() {
		if (isAwake())
			return;

		Thread motionThread = new Thread(motor, "ElevatorMotion");
		// System.out.println("Waking up elevator");
		motor.running = true;
		motionThread.start();
	}
}