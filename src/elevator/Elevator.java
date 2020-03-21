package elevator;

import util.Printer;

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

	public synchronized float remainingDistance() {
		float RemainMeterTraveled = 0;

		float seconds2Stop = secondsToStop();

		for (int i = 0; i < seconds2Stop; i++) {
			RemainMeterTraveled += (velocity - i * ACCELERATION);
		}

		return RemainMeterTraveled;
	}

	public synchronized float timeToStopAtFloor(int floor, int direction) {
		if (ElevatorSubsystem.verbose) {
			Printer.print("(req = " + floor + " going " + direction + ") (cur = " + currentFloor + " going "
					+ this.direction + ")");
		}

		// If directions are opposites
		if (direction != 0 && this.direction != 0 && direction != this.direction) {
			if (ElevatorSubsystem.verbose) {
				Printer.print("dif direction (req = " + floor + " going " + direction + ") (cur = " + currentFloor
						+ " going " + this.direction + ")");
			}
			return -1;
		}

		// If elevator is already on the floor
		if (currentFloor == floor) {
			return 0;
		}

		// If start floor lower than elevator while elevator going up
		if (floor < currentFloor && this.direction == UP) {
			if (ElevatorSubsystem.verbose) {
				Printer.print("If start floor lower than elevator while elevator going up");
			}
			return -1;
		}

		// If start floor higher than elevator while elevator going down
		if (floor > currentFloor && this.direction == DOWN) {
			if (ElevatorSubsystem.verbose) {
				Printer.print("If start floor higher than elevator while elevator going down " + floor + " > "
						+ currentFloor);
				Printer.print("direction is " + direction + " for " + subsystem.workDoing);
			}
			return -1;
		}

//		if (floor < floorRequestBoundary[0] || floor > floorRequestBoundary[1]) {
//			return false;
//		}

		// if velocity is too fast to slow down, return false;
//		float distanceToFloor = Math.abs(floor - currentFloor) * FLOOR_HEIGHT;
//		float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / velocity;
//		return secondsToFloor >= secondsToStop() ? secondsToFloor : -1;


		int ticks = 0; // how many ticks (seconds) has it been
		float accVelocity = velocity; // how fast were we going when we had to stop
		float distanceTraveled = 0; // just a marker for when to exit the loop, not needed for math
		float halfDistanceToFloor = (Math.abs(floor - currentFloor) * FLOOR_HEIGHT) / 2;

		do {
			accVelocity += accVelocity == MAX_VELOCITY ? 0 : ACCELERATION;
			distanceTraveled += accVelocity;
			ticks++;
		} while (distanceTraveled < halfDistanceToFloor);

		int secondsToFloor = ticks * 2; // x2 value to account for accelerating then decelerating

		return secondsToFloor;
	}

	public boolean isAwake() {
		return motor != null && motor.running;
	}

	public synchronized void wakeup() {
		if (isAwake())
			return;

		motor = new ElevatorMotor(this);
		Thread motionThread = new Thread(motor, "ElevatorMotion");
		Printer.print("Waking up elevator");
		motor.running = true;
		motionThread.start();
	}

}