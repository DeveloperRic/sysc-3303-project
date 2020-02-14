package schedulerStates;

import main.MainScheduler;

public class ReceiveRequestFromFloor implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		
		//receive the request
		//do some math
		//go to SendRequestToElevator
		m.setState(new SendRequestToElevator());
		
	}

}
