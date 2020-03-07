package elevator;

public class Elevator {

	public static final float ACCELERATION = 0.68f;
	public static final float MAX_VELOCITY = 4.31f;
	public static final float FLOOR_HEIGHT = 3.23f; // This should be a function call to Floor but whatever for now

	private static final int UP = 1;
	private static final int DOWN = -1;

	final ElevatorSubsystem subsystem;

	ElevatorDoors doors;
	ElevatorMotor motor;
	ElevatorButton[] buttons;
	public Integer currentFloor;
	public float velocity;
	public int direction; // 1 is up, -1 is down
	float metresTravelled; // per floor, NOT total

	public Elevator(ElevatorSubsystem subsystem) {
		this.subsystem = subsystem;
		doors = new ElevatorDoors(this);
		buttons = new ElevatorButton[21];
		for (int i = 1; i <= 21; ++i) {
			buttons[i - 1] = new ElevatorButton(subsystem, i);
		}
		currentFloor = 1;
		velocity = ACCELERATION;
		metresTravelled = 0;
	}

	public boolean isMoving() {
		return velocity > 0;
	}
	
	public float getMetersTravelled() {
		return metresTravelled;
	}

	public synchronized float secondsToStop() {
		return velocity <= ACCELERATION ? 0 : velocity / ACCELERATION;
	}

	public synchronized float timeToStopAtFloor(int floor, int direction) {

		if (ElevatorSubsystem.verbose) {
			System.out.println("(req = " + floor + " going " + direction + ") (cur = " + currentFloor + " going "
					+ this.direction + ")");
		}

		// If elevator has not been assigned anything yet
		if (velocity == 0 || currentFloor == floor || this.direction == 0) {
			return 0;
		}

		// If directions are different
		if (direction != 0 && direction != this.direction) {
			if (ElevatorSubsystem.verbose) {
				System.out.println("dif direction (req = " + floor + " going " + direction + ") (cur = " + currentFloor
						+ " going " + this.direction + ")");
			}
			return -1;
		}

		// If start floor lower than elevator while elevator going up
		if (floor < currentFloor && this.direction == UP) {
			if (ElevatorSubsystem.verbose) {
				System.out.println("If start floor lower than elevator while elevator going up");
			}
			return -1;
		}

		// If start floor higher than elevator while elevator going down
		if (floor > currentFloor && this.direction == DOWN) {
			if (ElevatorSubsystem.verbose) {
				System.out.println("If start floor higher than elevator while elevator going down " + floor + " > "
						+ currentFloor);
				System.out.println("direction is " + direction + " for " + subsystem.workDoing);
			}
			return -1;
		}

//		if (floor < floorRequestBoundary[0] || floor > floorRequestBoundary[1]) {
//			return false;
//		}

		// If elevator has not been assigned anything yet
		if (velocity == 0 || currentFloor == floor || this.direction == 0) {
			return 0;
		}

		// if velocity is too fast to slow down, return false;
		float distanceToFloor = Math.abs(floor - currentFloor) * FLOOR_HEIGHT;
		float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / velocity;
		return secondsToFloor >= secondsToStop() ? secondsToFloor : -1;
	}

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
		return motor != null && motor.running;
	}

	public synchronized void wakeup() {
		if (isAwake())
			return;

		motor = new ElevatorMotor(this);
		Thread motionThread = new Thread(motor, "ElevatorMotion");
		System.out.println("Waking up elevator");
		motor.running = true;
		motionThread.start();
	}

}