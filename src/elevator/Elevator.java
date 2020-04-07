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

	/**
	 * Returns true if elevator isMoving.
	 * 
	 * @return boolean true if elevator has velocity greater than 0.
	 */
	public boolean isMoving() {
		return velocity > 0;
	}
	
	/**
	 * Calculates how far the elevator has traveled.
	 * 
	 * @return metresTravelled float that represents how much far the elevator has traveled in meters.
	 */
	public float getMetersTravelled() {
		return metresTravelled;
	}

	/**
	 * Calculates how many seconds it will take for the elevator to stop given its velocity.
	 * 
	 * @return float that represents how much longer in seconds the elevator must travel to stop.
	 */
	public synchronized float secondsToStop() {
		return velocity <= ACCELERATION ? 0 : velocity / ACCELERATION;
	}

	/**
	 * Calculates the the distance the elevator must travel in order to stop using how many more seconds the 
	 * elevator must travel for (seconds2Stop).
	 * 
	 * @return RemainMeterTraveled float that represents how much further the elevator must travel to stop.
	 */
	public synchronized float remainingDistance() {
		float RemainMeterTraveled = 0;

		float seconds2Stop = secondsToStop();

		for (int i = 0; i < seconds2Stop; i++) {
			RemainMeterTraveled += (velocity - i * ACCELERATION);
		}

		return RemainMeterTraveled;
	}

	/**
	 * Calculates the time it will take to reach the target floor from the current floor using velocity and acceleration measurements.
	 * 
	 * @param floor The floor in which the elevator is headed towards (the request floor).
	 *        direction The direction the elevator is moving in denoted by an integer. 1 is up, -1 is down.
	 * 
	 * @return secondsToFloor float that represents how long it will take to reach the target floor. Will return 0 or -1 if there is
	 * a problem in the logic of the request.
	 */
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

		//Time calculations
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

	/**
	 * Checks if elevator is awake
	 * @return Boolean true if motor has been instantiated and the motor is running
	 */
	public boolean isAwake() {
		return motor != null && motor.running;
	}

	/**
	 * Simulates elevator becoming awake
	 * Starts and runs new thread (motionThread) to handle elevator movement
	 */
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