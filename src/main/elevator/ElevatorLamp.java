package main.elevator;

public class ElevatorLamp {
	
	private boolean illuminated;
	
	public void turnOn() {
		if (!illuminated) {
			System.out.println("\tlamp on");
			illuminated = true;
		}
	}
	
	public void turnOff() {
		if (illuminated) {
			System.out.println("\tlamp off");
			illuminated = false;
		}
	}

}
