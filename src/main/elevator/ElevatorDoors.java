package main.elevator;

class ElevatorDoors {
	private final float DOOR_MOVE_TIME = 6.74f;

	private boolean doorsOpen;
	// private int movingDirection; // 1 for opening, -1 for closing

	void openDoors() {
		if (doorsOpen)
			return;
		System.out.println("Opening doors");

		// movingDirection = 1;
		try {
			Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
		} catch (InterruptedException e) {
		}
		doorsOpen = true;
	}

	void closeDoors() {
		if (!doorsOpen)
			return;
		System.out.println("Closing doors");
		// movingDirection = -1;
		try {
			Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
		} catch (InterruptedException e) {
		}
		doorsOpen = false;
	}
}