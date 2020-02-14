package schedulerStates;

import main.MainScheduler;

public class SendAcknowledgmentToFloor implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		
		//send the acknowledgement to the floor
		//go to waiting state
		m.setState(new WaitForInput());
		
	}

}
