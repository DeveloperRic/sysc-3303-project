package schedulerStates;

import main.MainScheduler;

/**
 * This class is the receive request from floor state
 * 
 * @author Kevin
 *
 */
public class ReceiveRequestFromFloor implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("RECEIVE REQUEST FROM FLOOR STATE");
		//receive the request
		//do some math
		//if the elevator can go to a requested floor, send the request
		if(m.doMath()) {
			//go to SendRequestToElevator
			m.setState(new SendRequestToElevator());
			m.currentState.doWork(m);
		}else {
			m.setState(new WaitForInput());
			m.currentState.doWork(m);
		}

	}

}