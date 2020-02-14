package schedulerStates;

import main.MainScheduler;

public class ReceiveUpdateFromElevator implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("RECEIVE UPDATE FROM ELEVATOR STATE");
		//use the update from the elevator to update some things
		//if it finished all assigned work, give it new work from doMath
		//m.doMath();
		//if it reached a floor it wants, tell floor it's here
		//if it just needs to send a command to elevator, send command to elevator
		m.updateElevatorVals();
		m.setState(new SendUpdateToFloor());
		m.currentState.doWork(m);
		//m.setState(new SendElevatorCommand());
	}

}
