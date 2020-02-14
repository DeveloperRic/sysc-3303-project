package main;

public class Floor {
		int floorNumber;
		Lamp upRequestLight;
		Lamp downRequestLight;
		
		FloorState bothRequested;
		FloorState upRequested;
		FloorState downRequested;
		FloorState noRequest;
		
		FloorState state;
		
		//FLOOR CONSTRUCTOR
		Floor (int floorNumber) {
			upRequestLight = new Lamp();
			downRequestLight = new Lamp();
			
			this.floorNumber = floorNumber;
			
			upRequested = new upRequested(this);
			downRequested = new downRequested(this);
			bothRequested = new bothRequested(this);
			noRequest = new noRequest(this);
			
			state = noRequest;
		}
		
		//FLOORSTATE
		public interface FloorState {
			void requestUp();
			void requestDown();
			void requestServed(int direction);
		}

		//UP REQUESTED
		public class upRequested implements FloorState {
			Floor floor;
			public upRequested(Floor _floor) {
				floor = _floor;
			}
			
			@Override
			public void requestUp() {
				System.out.println("Up requested on floor " + floorNumber);
				floor.upRequestLight.turnOn();
				
				if (downRequestLight.lampOn) {
					floor.setState(bothRequested);
				}
			}

			@Override
			public void requestDown() {
				floor.setState(downRequested);
				floor.requestDown();
				
			}

			@Override
			public void requestServed(int direction) {
				switch (direction) {
				case 1:
					downRequestLight.turnOff();
					floor.setState(noRequest);
					break;
				default: 
					System.out.println("There's no request for an elevator in that direction on this floor.");
				}	
			}
		}
		
		//DOWN REQUESTED
		public class downRequested implements FloorState {
			Floor floor;
			public downRequested(Floor _floor) {
				floor = _floor;
			}
			
			@Override
			public void requestUp() {
				// If in downState switch to upState
				floor.setState(upRequested);
				floor.requestUp();
			}

			@Override
			public void requestDown() {
				// If upState already requested turn on light and then switch to bothState
				System.out.println("Down requested on floor " + floorNumber);
				downRequestLight.turnOn();
				
				if (upRequestLight.lampOn) {
					floor.setState(bothRequested);
				}
			}

			@Override
			public void requestServed(int direction) {
				switch (direction) {
				case -1:
					downRequestLight.turnOff();
					floor.setState(noRequest);
					break;
				default: 
					System.out.println("There's no request for an elevator in that direction on this floor.");
				}
			}
		}
		
		//BOTH REQUESTED
		public class bothRequested implements FloorState {
			Floor floor;
			public bothRequested(Floor _floor) {
				floor = _floor;
			}
			
			@Override
			public void requestUp() {
				System.out.println("Up requested on floor " + floorNumber);
			}

			@Override
			public void requestDown() {
				System.out.println("Down requested on floor " + floorNumber);
			}

			@Override
			public void requestServed(int direction) {
				switch (direction) {
				case -1:
					downRequestLight.turnOff();
					floor.setState(upRequested);
					break;
				case 1: 
					upRequestLight.turnOff();
					floor.setState(downRequested);
					break;
				}
			}
		}
		
		//NO REQUESTS
		public class noRequest implements FloorState {
			Floor floor;
			public noRequest(Floor _floor) {
				floor = _floor;
			}
			
			@Override
			public void requestUp() {
				floor.setState(upRequested);
				floor.requestUp();
			}

			@Override
			public void requestDown() {
				floor.setState(downRequested);
				floor.requestDown();
				
			}

			@Override
			public void requestServed(int direction) {
				System.out.println("There are no requests on this floor.");
			}
		}
		
		public void requestDown() {
			state.requestDown();
		}
		
		public void requestUp() {
			state.requestUp();
		}
		
		public void requestServed(int direction) {
			state.requestServed(direction);
		}
		
		public void setState (FloorState _state) {
			state = _state;
		}
}
