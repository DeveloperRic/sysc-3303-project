package main;

/** Lamp.java
 * This class is used to indicate the lamp turned on when a floor requests an elevator.
 *                    to indicate the lamp turned off when a floor is serviced by an elevator.
 *
 */
public class FloorLamp {
	
	//Indicates if a floor button is pressed or not.
	private boolean lamp;

	public FloorLamp () {
		lamp = false;
	}

	
    /**
     * Indicates that an elevator has been requested for the associated floor.
     */
	public void turnOn() {
		lamp = true;
	}

    /**
     * Indicates that a floor has been serviced by the requested elevator.
     */
	public void turnOff() {
		lamp = false;
	}
	
    /**
     * Getter to see the lamp status.
     */
	public boolean getLamp() { return lamp; }
	
    /**
     * Setter to turn the lamp off or on.
     */
	public void setLamp(boolean lamp) { this.lamp = lamp; }
}