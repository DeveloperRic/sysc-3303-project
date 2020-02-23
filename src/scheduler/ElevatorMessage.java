package scheduler;

public interface ElevatorMessage {
	default FloorRequest getFloorRequest() {
		return null;
	}
	default String getAcknowledgement() {
		return null;
	}
}