package schedulerStates;

import main.MainScheduler;
import main.Task;

/**
 * This class is the sending request to elevator state
 * 
 * @author Kevin
 *
 */
public class SendRequestToElevator implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("SEND REQUEST TO ELEVATOR STATE");
		//send the request to the elevator to come to floor (floor number, direction)
		//send tasks that have been added to a sending queue based on the doMath function
		m.elevatorMessageQueue.addAll(m.floorsToSendToElevator);
		m.elevatorPath.addAll(m.floorsToSendToElevator);
		m.floorsToSendToElevator.clear();
		//go into waiting state
		m.setState(new WaitForInput());
	}

}
