package scheduler;

public abstract class FloorRequest {
	public Float[] responses;
	public int numResponses;
	public int selectedElevator = -1;
	
	public abstract Integer[] getRequest();
	public Integer getSourceElevator() {
		return null;
	}
}