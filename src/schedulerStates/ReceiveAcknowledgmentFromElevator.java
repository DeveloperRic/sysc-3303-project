package schedulerStates;

import main.MainScheduler;

/**
 * This class is the receive acknkowledgement from elevator state
 * 
 * @author Kevin
 *
 */
public class ReceiveAcknowledgmentFromElevator implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("RECEIVE ACKNOWLEDGEMENT FROM ELEVATOR STATE");
		//use the acknowledgement to update stuff
		//go to SendAcknowledgementToFloor
		m.setState(new SendAcknowledgmentToFloor());
		m.currentState.doWork(m);
	}

}
