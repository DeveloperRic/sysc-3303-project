package elevator;

import elevator.ElevatorSubsystem.ElevatorState;
import util.Printer;

public final class ElevatorMotor implements Runnable {

	private final Elevator elevator;
	boolean running;
	private boolean taskAssigned;

	public ElevatorMotor(Elevator elevator) {
		this.elevator = elevator;
	}

	@Override
	public void run() {
		if (ElevatorSubsystem.verbose) {
			Printer.print("[Elevator] woken up");
		}
		this.elevator.velocity = Elevator.ACCELERATION;
		this.elevator.metresTravelled = 0;
//		this.elevator.subsystem.notifyStatus();
//		Printer.print("Get in");
		while (this.elevator.subsystem.poweredOn && (!this.elevator.subsystem.workDoing.isEmpty())) {
//		while (this.elevator.subsystem.poweredOn && (this.elevator.subsystem.getTask() != null)) {
//				|| !this.elevatorState.subsystem.workToDo.isEmpty())) {

			synchronized (elevator.subsystem.workDoing) {
				synchronized (elevator) {

					Integer targetFloor;
					boolean isWorkToDo = false;
//			if ((targetFloor = this.elevator.subsystem.workDoing.get(0)) == null) {
					if ((targetFloor = elevator.subsystem.workDoing.peek()) == null) {
//				targetFloor = this.elevatorState.subsystem.workToDo.peek();
//				isWorkToDo = true;
						break;
					}
//				Printer.print("Moving towards floor " + targetFloor);

					if (targetFloor > elevator.currentFloor) {
						elevator.direction = 1;
					} else if (targetFloor < elevator.currentFloor) {
						elevator.direction = -1;
					}
//					Printer.print("MOTOR: direction is now " + elevator.direction + " for task "
//							+ elevator.currentFloor + " -> " + targetFloor);

					if (!taskAssigned) {
						// Printer.print("Starting a task (-> " + targetFloor + ")");
					}
					taskAssigned = true;

					float distanceToFloor = Math.abs(targetFloor - this.elevator.currentFloor) * Elevator.FLOOR_HEIGHT;
					float secondsToFloor = distanceToFloor == 0 ? 0 : distanceToFloor / this.elevator.velocity;

//					System.out.print(nextFloor + ", " + distanceToFloor + ", " + secondsToFloor + " {} ");

					if (secondsToFloor - 1 < this.elevator.secondsToStop()) {
						if (this.elevator.currentFloor == targetFloor) {
							// Printer.print("\nArrived at floor " + targetFloor);

							this.elevator.velocity = Elevator.ACCELERATION;
							this.elevator.metresTravelled = 0;

							elevator.subsystem.notifyArrivedAtFloor(targetFloor);

							this.elevator.doors.openDoors();
							// TODO allow people to press button just in time and open doors again
							this.elevator.doors.closeDoors();

							if (isWorkToDo) {
//						this.elevatorState.subsystem.workToDo.poll();
							} else {
//						this.elevator.subsystem.workDoing.remove(0);
								this.elevator.subsystem.workDoing.poll();
							}

							// reset elevator direction when not heading anywhere
							if (elevator.subsystem.workDoing.size() == 0) {
								elevator.direction = 0;
							}

							// Printer.print(
							// "\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " +
							// workToDo);

//					if ((targetFloor = this.elevatorState.subsystem.workDoing.peek()) == null) {
//						targetFloor = this.elevatorState.subsystem.workToDo.peek();
//					}

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

//			this.elevator.notifyStatus();

				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		this.elevator.velocity = Elevator.ACCELERATION;
		this.elevator.metresTravelled = 0;
		this.elevator.direction = 0;
		running = false;
		Printer.print("\nElevator is sleeping");
	}

	public void accelerate(Elevator elevator) {
		if (!elevator.isMoving())
			return;
		elevator.velocity = Math.min(elevator.velocity + Elevator.ACCELERATION, Elevator.MAX_VELOCITY);
		if (elevator.velocity == Elevator.MAX_VELOCITY) {
			this.elevator.subsystem.currentState = ElevatorState.MAX_SPEED;
		}
		doMovement(elevator, elevator.velocity);
	}

	public void decelerate(Elevator elevator) {
		if (!elevator.isMoving())
			return;
		doMovement(elevator, elevator.velocity = Math.max(elevator.velocity - Elevator.ACCELERATION, 0));
	}

	public void doMovement(Elevator elevator, float velocity) {
		elevator.metresTravelled += velocity;

		if (elevator.metresTravelled >= Elevator.FLOOR_HEIGHT) {
			elevator.currentFloor += elevator.direction;
			elevator.metresTravelled = 0;
			System.out.print("|");
		}
	}
}
