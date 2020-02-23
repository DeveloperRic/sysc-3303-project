package elevator;

public class ElevatorLamp {

	private boolean illuminated;

	public void turnOn() {
		if (!illuminated) {
			if (ElevatorSubsystem.verbose) {
				System.out.println("\tlamp on\n");
			}
			illuminated = true;
		}
	}

	public void turnOff() {
		if (illuminated) {
			if (ElevatorSubsystem.verbose) {
				System.out.println("\tlamp off\n");
			}
			illuminated = false;
		}
	}

}