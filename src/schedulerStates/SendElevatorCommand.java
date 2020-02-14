package schedulerStates;

import main.MainScheduler;

public class SendElevatorCommand implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("SEND ELEVATOR COMMAND STATE");
		//send a command to elevator to do specified work
		//go back to waiting state
		m.setState(new WaitForInput());
		
	}
	
	
	
}
