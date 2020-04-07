package main;
import java.nio.ByteBuffer;
import java.time.*;

/**
 * This class converts each input line into an object so it can be serialized and deserialized
 * when being sent across each subsystem. It determines whether each line is a fault or a request
 * and initializes the variables accordingly.
 * 
 */
public class Task implements Comparable<Task>{
	
	private LocalTime timeOfRequest;
	private int startFloor;
	private int endFloor;
	private int faultNumber;
	private int elevatorNumber;
	private String direction;
	private boolean isFault;
	private int timeDifferenceSeconds;
	
	/**
	 * A constructor used by the floor subsystem to initialize an input line into a "Request" or a "Fault".
	 * 
	 * @param parameterOne		Represents the time stamp of when the request or fault should occur.
	 * @param parameterTwo		Represents either the floor that requests an elevator or the type of fault that should occur.
	 * @param parameterThree	Represents either the direction the request is made or the elevator id in which the fault
	 * 							should occur in.
	 * @param isFault			Represents whether the line should be initiliazed as a request or a fault.
	 * 
	 */
	public Task(String parameterOne, String parameterTwo, String parameterThree, boolean isFault) {
		if(isFault) {
			initializeFault(parameterOne, parameterTwo, parameterThree);
		}
		else {
			initializeTask(parameterOne, parameterTwo, parameterThree);
		}
	}
	
	/**
	 * A constructor used by the elevator subsystem to initialize an input line to be used for comparison
	 * when receiving messages from the scheduler.
	 * 
	 *  @param timeOfRequest	Represents the time stamp of when the request should occur.
	 *  @param startFloor		Represents the floor that requests an elevator.
	 *  @param direction		Represents the direction the request is made.
	 *  @param endFloor			Represents the floor button pressed inside the elevator.
	 *
	 */
	public Task(String timeOfRequest, String startFloor, String direction, String endFloor) {
		this.timeOfRequest = LocalTime.parse(timeOfRequest);
		this.startFloor = Integer.parseInt(startFloor);
		this.endFloor = Integer.parseInt(endFloor);
		this.direction = direction;
		isFault = false;
		elevatorNumber = 0;
		faultNumber = 0;
	}
	
	/**
	 * Initializes the input line as a task by setting the values of the timeOfRequest, startFloor
	 * and the direction and defaulting everything else.
	 * 
	 * @param timeOfRequest	Represents the time stamp of when the request should occur.
	 * @param startFloor	Represents the floor that requests an elevator.
	 * @param direction		Represents the direction the request is made.
	 * 
	 */
	public void initializeTask(String timeOfRequest, String startFloor, String direction) {
		this.timeOfRequest = (timeOfRequest.equals("") ? null : LocalTime.parse(timeOfRequest));
		this.startFloor = Integer.parseInt(startFloor);
		this.direction = direction;
		isFault = false;
		elevatorNumber = faultNumber = endFloor = timeDifferenceSeconds = 0;
	}
	
	/**
	 * Initializes the input line as a fault by setting the values of the timeOfRequest, fault number 
	 * and elevatorNumber and defaulting everything else.
	 * 
	 * @param timeOfRequest		Represents the time stamp of when the fault should occur.
	 * @param faultNumber		Represents the type of fault that should occur.
	 * 							fault id(-1) represents a soft fault, which causes an elevator door to get stuck. 
	 * 							fault id(-2) represents a hard fault, which causes an elevator to get stuck between floors.
	 * @param elevatorNumber	Represents which elevator the fault should occur in.
	 * 
	 */
	public void initializeFault(String timeOfRequest, String faultNumber, String elevatorNumber) {
		this.timeOfRequest = (timeOfRequest.equals("") ? null : LocalTime.parse(timeOfRequest));
		this.faultNumber = Integer.parseInt(faultNumber);
		this.elevatorNumber = Integer.parseInt(elevatorNumber);
		isFault = true;
		startFloor = endFloor = timeDifferenceSeconds = 0;
		direction = "";
	}
		
	//Getters
	public LocalTime getRequestTime() { return this.timeOfRequest; }
	public int getStartFloor() { return this.startFloor; }
	public int getDestinationFloor() { return this.endFloor; }
	public int getFaultNumber() { return this.faultNumber; }
	public int getElevatorNumber() { return this.elevatorNumber; }
	public int getTimeDifference() { return this.timeDifferenceSeconds; }
	
	//Setters
	public void setLocalTime(LocalTime timeOfRequest) { this.timeOfRequest = timeOfRequest; }
	public void setStartFloor(int startFloor) { this.startFloor = startFloor; }
	public void setDestinationFloor(int endFloor) { this.endFloor = endFloor; }
	public void setFaultNumber(int faultNumber) { this.faultNumber = faultNumber; }
	public void setElevatorNumber(int elevatorNumber) { this.elevatorNumber = elevatorNumber; }
	public void setTimeDifference(int seconds) { timeDifferenceSeconds = seconds; }
	
	public boolean isFault() { return isFault; }
	
	/**
	 * Converts the stored direction string into a integer indicating the direction.
	 * "Up" represents 1.
	 * "Down" represents -1.
	 * Else 0 means it is doing neither.
	 * 
	 * @return int	the corresponding number equivalent to "Up" or "Down"
	 * 
	 */
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
	
	/**
	 * Returns the request stored which contains the floor requesting the elevator and 
	 * the direction of it.
	 * 
	 * @return Integer[] returns the request where Integer[0] is the floor and Integer[1] is the direction.
	 * 
	 */
	public Integer[] getRequest() {
		
		if(isFault())
			return null;
		
		Integer[] request = new Integer[2];
		request[0] = startFloor;
		request[1] = getDirection();
		return request;
	}
	
	/**
	 * Compares two tasks together by comparing their destination floors against each other.
	 * 
	 * @return int  A number indicating wheter this task is greater, lesser or equal to the other
	 * 				task it is being compared too.
	 */
	@Override
	public int compareTo(Task o) { return this.endFloor - o.endFloor; }
	
	/**
	 * Prints the Task object into a readable format for easier debugging purposes.
	 * 
	 * @return String 	A string representation of what the task object contains depending if 
	 * 					it is a fault or request.
	 * 
	 */
	@Override
	public String toString() {
		
		String result = "Time of Request: " + timeOfRequest + ", Start Floor: " + startFloor + ", End Floor: " + endFloor + ", Direction: " + direction;
		
		if(isFault()) {
			result = "Time of Request: " + timeOfRequest + ", Fault Number: " + faultNumber + ", Elevator Number: " + elevatorNumber + ", Time Difference (Seconds): " + timeDifferenceSeconds;
		}
		
		return result;
	}
	
	/**
	 * Converts the Task object into a byte[] so it can be sent to the main scheduler through a
	 * UDP. The Task object serializes the byte[] differently depending on whether it is a fault
	 * or request.
	 * Fault Format : [Fault Indicator(x4), TimeStamp Length(x4), TimeStamp(xTime Stamp Length), Fault Number(x4), Elevator Number (x4), TimeDifferenceSeconds (x4)]
	 * Request Format : [Fault Indicator(x4), Start Floor(x4), Direction(x4)]
	 * 
	 * @return byte[] The task object containing all of its information in a byte format.
	 * 
	 */
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
	
	/**
	 * Converts the byte[] back into a task object after it has been retrieved from the UDP. The function
	 * checks if the fault indicator is either a 0 or a 1. If it is a 0 it deserializes it as a request else
	 * it is a 1 and it is deserialized as a fault and returned back. 
	 * 
	 * @param bytes	The Task object stored in byte[] format.
	 * 
	 * @return Task	The Task object represent by the bytes parameter.
	 * 
	 */
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
