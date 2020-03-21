package elevator;

import util.Printer;

public class ElevatorButton {

	private ElevatorSubsystem subsystem;
	private int floor;
	private boolean pressed;
	private ElevatorLamp lamp;

	public ElevatorButton(ElevatorSubsystem subsystem, int floor) {
		this.subsystem = subsystem;
		this.floor = floor;
		this.lamp = new ElevatorLamp();
	}

	public void press() {
		if (!pressed) {
			pressed = true;
			if (ElevatorSubsystem.verbose) {
				Printer.print("ELEVATOR BUTTON: floor " + floor + " pressed");
			}
			// TODO send request to Scheduler
			lamp.turnOn();
			subsystem.notifyButtonPressed(floor);
		}
	}

	// called as a result of a message from Scheduler
	public void unpress() {
		if (pressed) {
			pressed = false;
			if (ElevatorSubsystem.verbose) {
				Printer.print("ELEVATOR BUTTON: floor " + floor + " unpressed");
			}
			lamp.turnOff();
		}
	}
	
	public boolean isPressed() {
		return pressed;
	}

}