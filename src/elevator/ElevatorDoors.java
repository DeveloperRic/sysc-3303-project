package elevator;

import elevator.ElevatorSubsystem.ElevatorState;

public class ElevatorDoors {
	private final float DOOR_MOVE_TIME = 6.74f;

	private Elevator elevator;
	private boolean doorsOpen;
	private int movingDirection; // 1 for opening, -1 for closing

	public ElevatorDoors(Elevator elevator) {
		this.elevator = elevator;
	}

	public void openDoors() {
		if (doorsOpen)
			return;
		System.out.println("Opening doors");

		this.elevator.subsystem.currentState = ElevatorState.DOORS_OPENING;

		movingDirection = 1;
		try {
			Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
		} catch (InterruptedException e) {
		}
		movingDirection = 0;
		doorsOpen = true;
		this.elevator.subsystem.currentState = ElevatorState.DOORS_OPEN;
	}

	public void closeDoors() {
		if (!doorsOpen)
			return;
		System.out.println("Closing doors");

		this.elevator.subsystem.currentState = ElevatorState.DOORS_CLOSING;

		movingDirection = -1;
		try {
			Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
		} catch (InterruptedException e) {
		}
		movingDirection = 0;
		doorsOpen = false;
		this.elevator.subsystem.currentState = ElevatorState.DOORS_CLOSED;
	}

	public int getMovingDirection() {
		return movingDirection;
	}

	public boolean isOpen() {
		return doorsOpen;
	}
}