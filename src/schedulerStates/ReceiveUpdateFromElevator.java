package schedulerStates;

import main.MainScheduler;

public class ReceiveUpdateFromElevator implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		
		//use the update from the elevator to update some things
		//if it reached a floor it wants, tell floor it's here
		//if it just needs to send a command to elevator, send command to elevator
		m.setState(new SendUpdateToFloor());
		//m.setState(new SendElevatorCommand());
	}

}
