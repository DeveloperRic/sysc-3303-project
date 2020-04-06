package main;
import java.nio.ByteBuffer;
import java.time.*;

public class Task implements Comparable<Task>{
	
	private LocalTime timeOfRequest;
	private int startFloor;
	private int endFloor;
	private int faultNumber;
	private int elevatorNumber;
	private String direction;
	private boolean isFault;
	private int timeDifferenceSeconds;
	
	public Task(String parameterOne, String parameterTwo, String parameterThree, boolean isFault) {
		if(isFault) {
			initializeFault(parameterOne, parameterTwo, parameterThree);
		}
		else {
			initializeTask(parameterOne, parameterTwo, parameterThree);
		}
	}
	
	public Task(String timeOfRequest, String startFloor, String direction, String endFloor) {
		this.timeOfRequest = LocalTime.parse(timeOfRequest);
		this.startFloor = Integer.parseInt(startFloor);
		this.endFloor = Integer.parseInt(endFloor);
		this.direction = direction;
		isFault = false;
		elevatorNumber = 0;
		faultNumber = 0;
	}
		
	public void initializeTask(String timeOfRequest, String startFloor, String direction) {
		this.timeOfRequest = (timeOfRequest.equals("") ? null : LocalTime.parse(timeOfRequest));
		this.startFloor = Integer.parseInt(startFloor);
		this.direction = direction;
		isFault = false;
		elevatorNumber = faultNumber = endFloor = timeDifferenceSeconds = 0;
	}
	
	public void initializeFault(String timeOfRequest, String faultNumber, String elevatorNumber) {
		this.timeOfRequest = (timeOfRequest.equals("") ? null : LocalTime.parse(timeOfRequest));
		this.faultNumber = Integer.parseInt(faultNumber);
		this.elevatorNumber = Integer.parseInt(elevatorNumber);
		isFault = true;
		startFloor = endFloor = timeDifferenceSeconds = 0;
		direction = "";
	}
		
	//GETTERS
	public LocalTime getRequestTime() { return this.timeOfRequest; }
	public int getStartFloor() { return this.startFloor; }
	public int getDestinationFloor() { return this.endFloor; }
	public int getFaultNumber() { return this.faultNumber; }
	public int getElevatorNumber() { return this.elevatorNumber; }
	public int getTimeDifference() { return this.timeDifferenceSeconds; }
	
	//SETTERS
	public void setLocalTime(LocalTime timeOfRequest) { this.timeOfRequest = timeOfRequest; }
	public void setStartFloor(int startFloor) { this.startFloor = startFloor; }
	public void setDestinationFloor(int endFloor) { this.endFloor = endFloor; }
	public void setFaultNumber(int faultNumber) { this.faultNumber = faultNumber; }
	public void setElevatorNumber(int elevatorNumber) { this.elevatorNumber = elevatorNumber; }
	public void setTimeDifference(int seconds) { timeDifferenceSeconds = seconds; }
	
	public boolean isFault() { return isFault; }
	
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
	
	public Integer[] getRequest() {
		
		if(isFault())
			return null;
		
		Integer[] request = new Integer[2];
		request[0] = startFloor;
		request[1] = getDirection();
		return request;
	}
	
	@Override
	public int compareTo(Task o) {
		return this.endFloor - o.endFloor;
	}
	
	@Override
	public String toString() {
		
		String result = "Time of Request: " + timeOfRequest + ", Start Floor: " + startFloor + ", End Floor: " + endFloor + ", Direction: " + direction;
		
		if(isFault()) {
			result = "Time of Request: " + timeOfRequest + ", Fault Number: " + faultNumber + ", Elevator Number: " + elevatorNumber + ", Time Difference (Seconds): " + timeDifferenceSeconds;
		}
		
		return result;
	}
	
	public synchronized byte[] serialize() {
		
		ByteBuffer buffer;
		byte[] bytes = null;
		int bufferLength = 0;
		
		//Serialize with Fault data
		if(isFault) {
			
			bufferLength = 5 * 4;
			
			byte[] timeStamp = null;
			
			if(getRequestTime() != null) {
				timeStamp = getRequestTime().toString().getBytes();
				bufferLength += timeStamp.length;
			}
			
			buffer = ByteBuffer.allocate(bufferLength);
			
			buffer.putInt(1);
			buffer.putInt(timeStamp != null ? timeStamp.length : 0);
			
			if(timeStamp != null) {
				buffer.put(timeStamp);		//Serialize the time
			}
			
			buffer.putInt(faultNumber);		//Serialize the fault number
			buffer.putInt(elevatorNumber);	//Serialize the elevator number
			buffer.putInt(timeDifferenceSeconds); //Serialize the time difference
			
			bytes = buffer.array();
			
			return bytes;
		}
		
		//Serialize with normal data
		bufferLength = 3 * 4;
		
		buffer = ByteBuffer.allocate(bufferLength);
		
		buffer.putInt(0);
		buffer.putInt(getStartFloor());		//Serialize the start floor
		buffer.putInt(getDirection());		//Serialize the direction
		
		bytes = buffer.array();
		
		return bytes;
	}
	
	public static Task deserialize(byte[] bytes) {
		
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		int faultIndicator = buffer.getInt();
		
		if(faultIndicator == 1) {
			
			int timeStampLength = buffer.getInt();
			
			//Retrieve the time stamp
			String timeStamp = "";
			if(timeStampLength > 0) {
				byte[] timeStampBytes = new byte[timeStampLength];
				buffer.get(timeStampBytes,0,timeStampLength);
				timeStamp = new String(timeStampBytes);
			}
			
			String faultNumber = String.valueOf(buffer.getInt());		    //Retrieve the fault number
			String elevatorNumber = String.valueOf(buffer.getInt());	    //Retrieve the elevator number
			int timeDifferenceSeconds = buffer.getInt();                    //Retrieve the time difference
			
			Task task = new Task(timeStamp,faultNumber,elevatorNumber,true);
			task.setTimeDifference(timeDifferenceSeconds);
			
			return task;
		}
		
		String startFloor = String.valueOf(buffer.getInt());			//Retrieve the startFloor
		String direction = (buffer.getInt() == 1 ? "Up" : "Down");		//Retrieve the direction
		
		return new Task("",startFloor,direction,false);
	}
	
}
