package main.elevator;

public class Elevator {
	
	private static final float ACCELERATION = 0.68f;
	private static final float TERMINAL_VELOCITY = 4.31f;
	private static final float FLOOR_HEIGHT = 3.23f;
	private static final float DOOR_MOVE_TIME = 6.74f;
	
	private static boolean poweredOn;
	private static int currentFloor;
	private static float velocity;
	private static int direction; // 1 is up, -1 is down
	private static float metresTravelled;
	
	private static boolean doorsOpen;	


	private static elevatorStates currentState;
	
	
	//List all the states include Idle/Stopped, Accelerating, MaxSpeed, Decelerating, 
	//DoorsOpening, DoorsOpened, DoorsClosing, DoorsClosed
	enum elevatorStates{
		Idle {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

		},
		Accelerating {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				if (!isMoving())
					return;
				doMovement(velocity = Math.min(velocity + ACCELERATION, TERMINAL_VELOCITY));
				
				System.out.println("Accelerating! Velocity: " + velocity);
				
				try{
					Thread.sleep(2000);
				}catch(InterruptedException e){
					
				}
				
				if (velocity == TERMINAL_VELOCITY){
					currentState = MaxSpeed;
					MaxSpeed.accelerate();
				}
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

		},
		Decelerating {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				if (!isMoving())
					return;
				doMovement(velocity = Math.max(velocity - ACCELERATION, 0));
				
				System.out.println("Decelerating! Velocity: " + velocity);
				
				try{
					Thread.sleep(2000);
				}catch(InterruptedException e){			
				}
			}

		},
		MaxSpeed {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				System.out.println("Reach To Max Speed! Velocity: " + velocity);
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

		},
		DoorsOpening{
			
			protected void openDoors(){
				if (doorsOpen)
					return;
				System.out.println("Opening doors");
				try {
					Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
				} catch (InterruptedException e) {
				}
				doorsOpen = true;
				currentState = DoorsOpened;
				DoorsOpened.openDoors();
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

			
		},
		DoorsOpened {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				System.out.println("Doors Opened");
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

		},
		DoorsClosing {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				if (!doorsOpen)
					return;
				System.out.println("Closing doors");
				try {
					Thread.sleep((long) (DOOR_MOVE_TIME * 1000));
				} catch (InterruptedException e) {
				}
				doorsOpen = false;
				currentState = DoorsClosed;
				DoorsClosed.closeDoors();
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

		},
		DoorsClosed {
			@Override
			protected void openDoors() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void closeDoors() {
				// TODO Auto-generated method stub
				System.out.println("Doors Closed");
			}

			@Override
			protected void accelerate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void decelerate() {
				// TODO Auto-generated method stub
				
			}

		};
		
		protected abstract void openDoors();
		protected abstract void closeDoors();
		protected abstract void accelerate();
		protected abstract void decelerate();
		

	}
	
	
	
	public static boolean isMoving() {
		return velocity > 0;
	}

	private static void doMovement(float velocity) {
		metresTravelled += velocity;

		if (metresTravelled >= FLOOR_HEIGHT) {
			currentFloor += direction;
			metresTravelled = 0;
		}
	}
	
	
	//Possible events from scheduler
	enum EventTypes{
		EVENT_IDLE,
	    EVENT_OPENING,
	    EVENT_CLOSING,
	    EVENT_ACCELERATING,
	    EVENT_DECELERATING,
	}

	
	public Elevator(){
		currentState = elevatorStates.Idle;
		poweredOn = true;
		currentFloor = 1;
		velocity = ACCELERATION;
		metresTravelled = 0;
	}
	
	private void setState(elevatorStates state){
		currentState = state;
	}
	
	private elevatorStates getCurrentState(){
		return currentState;
	}
	
	
	private void StateIdle(EventTypes event){
		
		switch (event){
			case EVENT_IDLE:
				setState(elevatorStates.Idle);
				break;
				
			case EVENT_OPENING:
				setState(elevatorStates.DoorsOpening);
				break;
				
			case EVENT_CLOSING:
				setState(elevatorStates.DoorsClosing);
				break;
				
			case EVENT_ACCELERATING:
				setState(elevatorStates.Accelerating);
				break;
				
			case EVENT_DECELERATING:
				setState(elevatorStates.Decelerating);
				break;
			
			default:
				break;
		}
		
	}
	
	
	private void StateAccelerating(EventTypes event){
		
		switch (event){
			case EVENT_IDLE:
				setState(elevatorStates.Idle);
				break;
				
			case EVENT_OPENING:
				setState(elevatorStates.DoorsOpening);
				break;
				
			case EVENT_CLOSING:
				setState(elevatorStates.DoorsClosing);
				break;
				
			case EVENT_ACCELERATING:
				setState(elevatorStates.Accelerating);
				break;
				
			case EVENT_DECELERATING:
				setState(elevatorStates.Decelerating);
				break;
			
			default:
				break;
		}
		
	}
	
		
	private void StateDecelerating(EventTypes event){
		
		switch (event){
			case EVENT_IDLE:
				setState(elevatorStates.Idle);
				break;
				
			case EVENT_OPENING:
				setState(elevatorStates.DoorsOpening);
				break;
				
			case EVENT_CLOSING:
				setState(elevatorStates.DoorsClosing);
				break;
				
			case EVENT_ACCELERATING:
				setState(elevatorStates.Accelerating);
				break;
				
			case EVENT_DECELERATING:
				setState(elevatorStates.Decelerating);
				break;
			
			default:
				break;
		}
		
	}
	
	
	private void StateDoorsOpening(EventTypes event){
		
		switch (event){
			case EVENT_IDLE:
				setState(elevatorStates.Idle);
				break;
				
			case EVENT_OPENING:
				setState(elevatorStates.DoorsOpening);
				break;
				
			case EVENT_CLOSING:
				setState(elevatorStates.DoorsClosing);
				break;
				
			case EVENT_ACCELERATING:
				setState(elevatorStates.Accelerating);
				break;
				
			case EVENT_DECELERATING:
				setState(elevatorStates.Decelerating);
				break;
			
			default:
				break;
		}
		
	}
	
	
	private void StateDoorsClosing(EventTypes event){
		
		switch (event){
			case EVENT_IDLE:
				setState(elevatorStates.Idle);
				break;
				
			case EVENT_OPENING:
				setState(elevatorStates.DoorsOpening);
				break;
				
			case EVENT_CLOSING:
				setState(elevatorStates.DoorsClosing);
				break;
				
			case EVENT_ACCELERATING:
				setState(elevatorStates.Accelerating);
				break;
				
			case EVENT_DECELERATING:
				setState(elevatorStates.Decelerating);
				break;
			
			default:
				break;
		}
	}
	

	//Testing
	public static void main(String args[]){
		Elevator e = new Elevator();

		e.StateIdle(EventTypes.EVENT_IDLE);
		System.out.println("Current State: " + e.getCurrentState());
		
		e.StateDoorsOpening(EventTypes.EVENT_OPENING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.openDoors();
		
		e.StateDoorsClosing(EventTypes.EVENT_CLOSING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.closeDoors();
		
		e.StateAccelerating(EventTypes.EVENT_ACCELERATING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.accelerate();
		
		e.StateAccelerating(EventTypes.EVENT_ACCELERATING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.accelerate();
		
		e.StateAccelerating(EventTypes.EVENT_ACCELERATING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.accelerate();
	    
		e.StateDecelerating(EventTypes.EVENT_DECELERATING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.decelerate();

		e.StateDecelerating(EventTypes.EVENT_DECELERATING);
		System.out.println("Current State: " + e.getCurrentState());
		currentState.decelerate();

		
	}
}
