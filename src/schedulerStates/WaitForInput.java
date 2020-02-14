package schedulerStates;

import main.MainScheduler;

public class WaitForInput implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		System.out.println("WAITING STATE");
		//	if input is request from floor
		//		go to ReceiveRequestFromFloor
		//  if input is acknowledgement from elevator
		//		go to ReceiveAcknowledgementFromElevator
		//	if input is elevator update
		//		go to ReceiveUpdateFromElevator
		if(m.isFloorRequest()) {
			m.setState(new ReceiveRequestFromFloor());
			m.currentState.doWork(m);
		}
		else if (m.isElevatorAck()) {
			m.setState(new ReceiveAcknowledgmentFromElevator());
			m.currentState.doWork(m);
		}
		else if (m.isElevatorUpdate()) {
			m.setState(new ReceiveUpdateFromElevator());
			m.currentState.doWork(m);
		}
		else {
			System.out.println("big problem");
		}
		
		
	}
	
}
