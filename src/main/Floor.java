package main;

import java.util.ArrayList;
import util.Printer;

public class Floor {
		
		private static final int UP = 1;
		private static final int DOWN = -1;
		
		private int floorNumber;
		private FloorButton upButton;
		private FloorButton downButton;
		
		//TODO: Implement Direction Lamps
		//private DirectionLamp (x4)
		
		private ArrayList<Task> requests;
		
		public Floor (int floorNumber, int maxFloor) {
			
			this.floorNumber = floorNumber;
			requests = new ArrayList<Task>();
			
			upButton = new FloorButton(UP);
			downButton = new FloorButton(DOWN);
			
			if(this.floorNumber == 1)
				downButton = null;
			
			if(this.floorNumber == maxFloor)
				upButton = null;

		}
		
		public void storeRequest(Task task) {
			requests.add(task);
			
			if(task.getDirection() == UP && upButton != null)
				upButton.pressButton();
			
			if(task.getDirection() == DOWN && downButton != null)
				downButton.pressButton();
		}
		
		public void serviceRequest() {
			
			//If the task matches a task in the requests and the direction is stopping
			//Remove it from the requests (Should i clear all requests?)
			if(!requests.isEmpty()) {
				requests.clear();
			}
			
			//Turn the lamps off
			if(upButton != null)
				upButton.getFloorLamp().turnOff();
			
			if(downButton != null)
				downButton.getFloorLamp().turnOff();
			
		}
		
		public ArrayList<Task> getRequests() { return requests; }
		public int getFloorNumber() { return floorNumber; }
}
