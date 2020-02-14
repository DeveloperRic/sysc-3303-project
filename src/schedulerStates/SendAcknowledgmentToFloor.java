package schedulerStates;

import main.MainScheduler;

/**
 * This class is the send acknowledgement to floor state
 * 
 * @author Kevin
 *
 */
public class SendAcknowledgmentToFloor implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("SEND ACKNOWLEDGEMENT TO FLOOR STATE");
		//send the acknowledgement to the floor
		//go to waiting state
		m.setState(new WaitForInput());
		
	}

}
