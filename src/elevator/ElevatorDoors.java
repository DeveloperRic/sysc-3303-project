package elevator;

import elevator.ElevatorSubsystem.ElevatorState;

class ElevatorDoors {
	private final float DOOR_MOVE_TIME = 6.74f;

	private Elevator elevator;
	private boolean doorsOpen;
	// private int movingDirection; // 1 for opening, -1 for closing
	
	public ElevatorDoors(Elevator elevator) {
		this.elevator = elevator;
	}

	void openDoors() {
		if (doorsOpen)
			return;
		System.out.println("Opening doors");
		
		this.elevator.subsystem.currentState = ElevatorState.DOORS_OPENING;

		// movingDirection = 1;
		try {
			Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
		} catch (InterruptedException e) {
		}
		doorsOpen = true;
		this.elevator.subsystem.currentState = ElevatorState.DOORS_OPEN;
	}

	void closeDoors() {
		if (!doorsOpen)
			return;
		System.out.println("Closing doors");
		
		this.elevator.subsystem.currentState = ElevatorState.DOORS_CLOSING;
		
		// movingDirection = -1;
		try {
			Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
		} catch (InterruptedException e) {
		}
		doorsOpen = false;
		this.elevator.subsystem.currentState = ElevatorState.DOORS_CLOSED;
	}
}