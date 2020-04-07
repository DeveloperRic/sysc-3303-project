package main;

import java.util.ArrayList;


/**
 * This class represents a unique floor level in this simulation by representing
 * a floor level in a building to be used for simulating elevator arrivals.
 * 
 */
public class Floor {

	private static final int UP = 1;
	private static final int DOWN = -1;

	private int floorNumber;			//Identifies which floor this is.
	private FloorButton upButton;		
	private FloorButton downButton;

	private ArrayList<Task> requests;	//Stores "requests" from this floor for an elevator.
	
	/**
	 * Creates a floor by initializing its floor number
	 * and requests coming from this floor.
	 * 
	 * @param floorNumber 	Indicates what level this floor is.
	 * @param maxFloor		Lets us know if we are creating the top floor.	
	 * 
	 */
	public Floor(int floorNumber, int maxFloor) {

		this.floorNumber = floorNumber;
		requests = new ArrayList<Task>();

		upButton = new FloorButton(UP);
		downButton = new FloorButton(DOWN);
		
		//Should only have a up button if this is floor 1
		if (this.floorNumber == 1)
			downButton = null;

		//Should only have a down button if this is floor 2
		if (this.floorNumber == maxFloor)
			upButton = null;
	}

	/**
	 * Stores a given task associated with the floor
	 * and simulates the floor button being pressed.
	 * 
	 * @param task A request from the floor which 
	 * 			   will be serviced by an elevator.
	 * 
	 */
	public void storeRequest(Task task) {
		requests.add(task);

		if (task.getDirection() == UP && upButton != null)
			upButton.pressButton();

		if (task.getDirection() == DOWN && downButton != null)
			downButton.pressButton();
	}
	
	/**
	 * Removes the stored request from the floor once
	 * and elevator reaches the floor and simulates
	 * the floor button lamp turning off.
	 * 
	 */
	public void serviceRequest() {
		if (!requests.isEmpty()) {
			requests.clear();
		}

		// Turn the lamps off
		if (upButton != null)
			upButton.getFloorLamp().turnOff();

		if (downButton != null)
			downButton.getFloorLamp().turnOff();
	}
	
	//Getters
	public ArrayList<Task> getRequests() { return requests; }
	public int getFloorNumber() { return floorNumber; }
}
