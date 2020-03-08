package main;
import java.time.*;

public class Task implements Comparable<Task>{
	//CLASS VARIABLES
	//private final LocalTime requestTime;
	//private final String direction;
	//private final int startFloor;
	//private final int destinationFloor;
	
	private LocalTime timeOfRequest;
	private int startFloor;
	private int endFloor;
	private String direction;
	
	public Task(String timeOfRequest, String startFloor, String direction) {
		this.timeOfRequest = LocalTime.parse(timeOfRequest);
		this.startFloor = Integer.parseInt(startFloor);
//		this.endFloor = Integer.parseInt(endFloor);
		this.direction = direction;
	}

	//CONSTRUCTORS
	//Task(String requestTime, String startFloor, String direction, String destinationFloor) {
		//this.requestTime = LocalTime.parse(requestTime);
		//this.startFloor = Integer.parseInt(startFloor);
		//this.direction = direction;
		//this.destinationFloor = Integer.parseInt(destinationFloor);
	//}
	
	//GETTERS
	public LocalTime getRequestTime() {
		return this.timeOfRequest;
	}
	
	//an input of 0 denotes a downward direction while 1 denotes an upward one.
	public int getDirection() {
		switch (direction) {
		case "Up":
			return 1;
		case "Down":
			return -1;
		default:
			return 0;
		}
	}
	
	public int getStartFloor() {
		return this.startFloor;
	}
	
	public int getDestinationFloor() {
		return this.endFloor;
	}

	@Override
	public int compareTo(Task o) {
		return this.endFloor - o.endFloor;
	}
	
	@Override
	public String toString() {
	
		return "Time of Request: " + timeOfRequest + ", Start Floor: " + startFloor + ", End Floor: " + endFloor + ", Direction: " + direction;
	}
	
}
