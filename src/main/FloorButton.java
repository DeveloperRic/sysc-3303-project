package main;

/**
 * This class represents a floor button in a floor
 * to be used for simulating button presses.
 * 
 */
public class FloorButton {
	
	private FloorLamp floorLamp;
	private int direction;
	
	/**
	 * Initializes the floor button by indicating what direction
	 * it is and initializing its floor lamp.
	 * 
	 */
	public FloorButton(int direction) {	
		floorLamp = new FloorLamp();
		this.direction = direction;
	}
	
	/**
	 * Simulates a floor button being pressed by turning
	 * on its lamp.
	 * 
	 */
	public void pressButton() {
		floorLamp.turnOn();
	}
	
	public boolean isButtonPressed() { return floorLamp.getLamp(); }
	
	//Getters
	public int getDirection() { return direction; }
	public FloorLamp getFloorLamp() { return floorLamp; }

}