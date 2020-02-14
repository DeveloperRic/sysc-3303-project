package schedulerStates;

import main.MainScheduler;

public class SendUpdateToFloor implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		
		//send the update to the floor that elevator has arrived
		//go to send command to elevator state
		m.setState(new SendElevatorCommand());
		
	}

}
