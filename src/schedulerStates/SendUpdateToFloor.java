package schedulerStates;

import main.MainScheduler;

public class SendUpdateToFloor implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("SEND UPDATE TO FLOOR STATE");
		//send the update to the floor that elevator has arrived
		//go to send command to elevator state
		m.setState(new SendElevatorCommand());
		
	}

}
