package schedulerStates;

import main.MainScheduler;

public class ReceiveAcknowledgmentFromElevator implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		//use the acknowledgement to update stuff
		//go to SendAcknowledgementToFloor
		m.setState(new SendAcknowledgmentToFloor());
	}

}
