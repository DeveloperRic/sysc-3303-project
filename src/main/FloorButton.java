package main;

public class FloorButton {
	
	private FloorLamp floorLamp;
	private int direction;
	
	public FloorButton(int direction) {	
		floorLamp = new FloorLamp();
		this.direction = direction;
	}
	
	public void pressButton() {
		floorLamp.turnOn();
	}
	
	public boolean isButtonPressed() { return floorLamp.getLamp(); }
	
	public int getDirection() { return direction; }
	public FloorLamp getFloorLamp() { return floorLamp; }

}