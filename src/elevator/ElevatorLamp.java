package elevator;

import util.Printer;

public class ElevatorLamp {

	private boolean illuminated;

	public void turnOn() {
		if (!illuminated) {
			if (ElevatorSubsystem.verbose) {
				Printer.print("\tlamp on\n");
			}
			illuminated = true;
		}
	}

	public void turnOff() {
		if (illuminated) {
			if (ElevatorSubsystem.verbose) {
				Printer.print("\tlamp off\n");
			}
			illuminated = false;
		}
	}
	
	public boolean isIlluminated() {
		return illuminated;
	}

}