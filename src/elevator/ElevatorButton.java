package elevator;

import util.Printer;

/**
 * This class represents a button in the Elevator to be used for the
 * simulation of pressing and sending requests back to the scheduler.
 * 
 */
public class ElevatorButton {

	private ElevatorSubsystem subsystem;
	private int floor;
	private boolean pressed;
	private ElevatorLamp lamp;
	
	/**
	 * Initializes the button by associating a floor with it and instantiating
	 * the elevator lamp to indicate it turning on and off. The Elevator button is
	 * also given a correlation with the elevator subsystem so it can alert it when
	 * somebody presses a button inside the elevator.
	 * 
	 */
	public ElevatorButton(ElevatorSubsystem subsystem, int floor) {
		this.subsystem = subsystem;
		this.floor = floor;
		this.lamp = new ElevatorLamp();
	}
	
	/**
	 * Simulates the pressing of a specific button inside the elevator by
	 * turning on its lamp and notifying the subsystem.
	 * 
	 */
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

	/**
	 * Simulates the turning off of the lamp when the elevator arrives at the targeted floor.
	 * It is called as a result of receiving a message from the Scheduler.
	 * 
	 */
	public void unpress() {
		if (pressed) {
			pressed = false;
			if (ElevatorSubsystem.verbose) {
				Printer.print("ELEVATOR BUTTON: floor " + floor + " unpressed");
			}
			lamp.turnOff();
		}
	}
	
	/**
	 * Indicates whether the elevator button has been pressed on or not.
	 * 
	 */
	public boolean isPressed() {
		return pressed;
	}

}