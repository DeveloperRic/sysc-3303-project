package elevator;

import java.util.ArrayList;

import elevator.ElevatorSubsystem.ElevatorState;
import main.Task;
import util.Printer;

public final class ElevatorMotor implements Runnable {

	private final Elevator elevator;
	boolean running;
	private boolean taskAssigned;
	boolean Ready = false;

	public ElevatorMotor(Elevator elevator) {
		this.elevator = elevator;
	}

	@Override
	public void run() {
		if (ElevatorSubsystem.verbose) {
			Printer.print("[Elevator] woken up");
			running = true;
		}
		this.elevator.velocity = 0;
		this.elevator.metresTravelled = 0;
		while (this.elevator.subsystem.poweredOn && (!this.elevator.subsystem.workDoing.isEmpty())) {

			synchronized (elevator.subsystem.workDoing) {
				synchronized (elevator) {

					Integer targetFloor;
					boolean isWorkToDo = false;
					if ((targetFloor = elevator.subsystem.workDoing.peek()) == null) {
						break;
					}

//					Printer.print("Moving towards floor " + targetFloor);

					if (targetFloor > elevator.currentFloor) {
						elevator.direction = 1;
					} else if (targetFloor < elevator.currentFloor) {
						elevator.direction = -1;
					}

//					Printer.print("MOTOR: direction is now " + elevator.direction + " for task "
//							+ elevator.currentFloor + " -> " + targetFloor);

					if (!taskAssigned) {
//						Printer.print("Starting a task (-> " + targetFloor + ")");
					}
					taskAssigned = true;

					float distanceToFloor = Math.abs(targetFloor - this.elevator.currentFloor) * Elevator.FLOOR_HEIGHT;

//					System.out.println(this.elevator.velocity + ", " + distanceToFloor + ", " +
//					secondsToFloor + " {} " + this.elevator.currentFloor);

					if (this.elevator.remainingDistance() >= (distanceToFloor - elevator.metresTravelled))
						Ready = true;

					System.out.println("Current Floor: " + this.elevator.currentFloor);

					if (Ready) {

						if (this.elevator.currentFloor == targetFloor) {
							// Printer.print("\nArrived at floor " + targetFloor);

							this.elevator.velocity = Elevator.ACCELERATION;
							this.elevator.metresTravelled = 0;

							elevator.subsystem.notifyArrivedAtFloor(targetFloor);

							this.elevator.doors.openDoors();

							if (elevator.subsystem.WATCHDOG.checkForFault())
								return;

							ArrayList<Task> tasks = elevator.subsystem.getTasks();

							for (int i = 0; i < tasks.size(); i++) {
								if (tasks.get(i).getStartFloor() == targetFloor) {
									elevator.subsystem.pressButton(tasks.get(i).getDestinationFloor());
									elevator.subsystem.getTasks().remove(i);
								}
							}

							this.elevator.doors.closeDoors();

							if (!isWorkToDo) {
								elevator.subsystem.WATCHDOG.removeFromWorkDoing(elevator.subsystem.workDoing.poll());
							}

							// reset elevator direction when not heading anywhere
							if (elevator.subsystem.workDoing.size() == 0) {
								elevator.direction = 0;
							}

							Ready = false;

//							Printer.print(
//									"\nelev: " + state.currentFloor + "\ndoing: " + workDoing + "\ntodo: " + workToDo);

						} else {

							if (elevator.subsystem.WATCHDOG.checkForFault())
								return;

							if (this.elevator.velocity == Elevator.MAX_VELOCITY) {
//								 System.out.print(".");// + currentFloor
								System.out.println("Reached To Max Speed...   ");
								this.decelerate(this.elevator);
							}

							else if (this.elevator.velocity <= Elevator.ACCELERATION
									&& this.elevator.currentFloor != targetFloor) {

								System.out.println("Decelerating (moving " + (elevator.direction == 1 ? "up" : "down")
										+ ")...   ");
								doMovement(elevator, this.elevator.velocity);
							}

							else {
//								 System.out.print("-");
								System.out.println("Decelerating (moving " + (elevator.direction == 1 ? "up" : "down")
										+ ")...   ");
								this.decelerate(this.elevator);
							}

//							 this.decelerate(this.elevator);

							this.elevator.subsystem.currentState = ElevatorState.DECELERATING;
						}
					} else {

						if (elevator.subsystem.WATCHDOG.checkForFault())
							return;

						boolean atMaxVelocity = this.elevator.velocity == Elevator.MAX_VELOCITY;
						if (atMaxVelocity) {
//							 System.out.print(".");
							System.out.println("Reached Max Speed...   ");
						} else {
//							 System.out.print("+");
							System.out.println(
									"Accerlating (moving " + (elevator.direction == 1 ? "up" : "down") + ")...   ");
						}

						this.accelerate(this.elevator);

						this.elevator.subsystem.currentState = atMaxVelocity ? ElevatorState.MAX_SPEED
								: ElevatorState.ACCELERATING;
					}
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
		Printer.print("\nElevator " + elevator.subsystem.elevatorNumber + " is sleeping");
	}

	public void accelerate(Elevator elevator) {
		if (!running)
			return;
		elevator.velocity = Math.min(elevator.velocity + Elevator.ACCELERATION, Elevator.MAX_VELOCITY);
		if (elevator.velocity == Elevator.MAX_VELOCITY) {
			this.elevator.subsystem.currentState = ElevatorState.MAX_SPEED;
		}
		doMovement(elevator, elevator.velocity);
	}

	public void decelerate(Elevator elevator) {
		if (!running)
			return;
		doMovement(elevator, elevator.velocity = Math.max(elevator.velocity - Elevator.ACCELERATION, 0));
	}

	public void doMovement(Elevator elevator, float velocity) {
		elevator.metresTravelled += velocity;

		if (elevator.metresTravelled >= Elevator.FLOOR_HEIGHT) {
			elevator.currentFloor += elevator.direction;
			elevator.metresTravelled = 0;
			// System.out.print("|");
		}

		// System.out.print(" Meter Traveled: " + elevator.metresTravelled + " ");

	}
}
