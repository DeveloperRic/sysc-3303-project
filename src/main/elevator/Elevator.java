package main.elevator;

public class Elevator {

	static final float ACCELERATION = 0.68f;
	static final float MAX_VELOCITY = 4.31f;
	static final float FLOOR_HEIGHT = 3.23f; // This should be a function call to Floor but whatever for now

	final ElevatorSubsystem subsystem;

	ElevatorDoors doors;
	ElevatorMotor motor;
	ElevatorButton[] buttons;
	int currentFloor;
	float velocity;
	int direction; // 1 is up, -1 is down
	float metresTravelled; // per floor, NOT total

	Elevator(ElevatorSubsystem subsystem) {
		this.subsystem = subsystem;
		doors = new ElevatorDoors();
		motor = new ElevatorMotor(this);
		buttons = new ElevatorButton[21]; 
		for (int i = 1; i <= 21; ++i) {
			buttons[i - 1] = new ElevatorButton(i);
		}
		currentFloor = 1;
		velocity = 0;
		metresTravelled = 0;
	}

	public boolean isMoving() {
		return velocity > 0;
	}

	float secondsToStop() {
		return velocity <= ACCELERATION ? 0 : velocity / ACCELERATION;
	}

//	public boolean canStopAtFloor(int floor) {
//		if (direction == 0)
//			return true;
//		if ((this.direction == -1 && floor > this.currentFloor)
//				|| (this.direction == 1 && floor < this.currentFloor)) {
//			return false;
//		}
//		if (!this.isMoving() || this.currentFloor == floor)
//			return true;
//
//		float distanceToFloor = Math.abs(floor - this.currentFloor) * FLOOR_HEIGHT;
//		float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / this.velocity;
//		return secondsToFloor >= this.secondsToStop();
//	}
//
//	public synchronized void assignTask(Task task) {
//		// System.out.println("Elevator Received Task");
//		if (canStopAtFloor(task.getStartFloor())) {
//
//			if (currentFloor != task.getStartFloor() && !this.subsystem.workDoing.contains(task.getStartFloor())) {
//				this.subsystem.workDoing.add(task.getStartFloor());
//			}
//			if (currentFloor != task.getDestinationFloor()
//					&& !this.subsystem.workDoing.contains(task.getDestinationFloor())) {
//				this.subsystem.workDoing.add(task.getDestinationFloor());
//			}
//
//			// System.out.println("Added task to workDoing (" + task.getStartFloor() + " ->
//			// " + task.getDestinationFloor() + ")");
//
//			// System.out.println("\nelev: " + state.currentFloor + "\ndoing: " + workDoing
//			// + "\ntodo: " + workToDo);
//
//			if (!isAwake())
//				wakeup();
//		} else {
//			if (currentFloor != task.getStartFloor() && !this.subsystem.workToDo.contains(task.getStartFloor())) {
//				this.subsystem.workToDo.add(task.getStartFloor());
//			}
//			if (currentFloor != task.getDestinationFloor()
//					&& !this.subsystem.workToDo.contains(task.getDestinationFloor())) {
//				this.subsystem.workToDo.add(task.getDestinationFloor());
//			}
//
//			// System.out.println("Added task to workToDo (" + task.getStartFloor() + " -> "
//			// + task.getDestinationFloor() + ")");
//
//			// System.out.println("\nelev: " + state.currentFloor + "\ndoing: " + workDoing
//			// + "\ntodo: " + workToDo);
//		}
//	}

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