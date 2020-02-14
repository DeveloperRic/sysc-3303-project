package schedulerStates;

import main.MainScheduler;

public class SendRequestToElevator implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		
		//send the request to the elevator to come to floor (floor number, direction)
		//go into waiting state
		m.setState(new WaitForInput());
	}

}
