package elevatorSubsystem;

public class ElevatorButton {
	
	private int floor;
	private boolean pressed;
	private ElevatorLamp lamp;
	
	public ElevatorButton(int floor) {
		this.floor = floor;
		this.lamp = new ElevatorLamp();
	}

	public void press() {
		if (!pressed) {
			pressed = true;
			System.out.println("ELEVATOR BUTTON: floor " + floor + " pressed");
			// TODO send request to Scheduler
			lamp.turnOn();
		}
	}
	
	// called as a result of a message from Scheduler
	public void unpress() {
		if (pressed) {
			pressed = false;
			lamp.turnOff();
		}
	}

}