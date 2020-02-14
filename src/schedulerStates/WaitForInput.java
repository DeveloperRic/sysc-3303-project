package schedulerStates;

import main.MainScheduler;

public class WaitForInput implements SchedulerState {

	@Override
	public void doWork(MainScheduler m) {
		
		//	if input is request from floor
		//		go to ReceiveRequestFromFloor
		//  if input is acknowledgement from elevator
		//		go to ReceiveAcknowledgementFromElevator
		//	if input is elevator update
		//		go to ReceiveUpdateFromElevator
		if(m.isFloorRequest()) {
			m.setState(new ReceiveRequestFromFloor());
		}
		else if (m.isElevatorAck()) {
			m.setState(new ReceiveAcknowledgmentFromElevator());
		}
		else if (m.isElevatorUpdate()) {
			m.setState(new ReceiveUpdateFromElevator());
		}
		else {
			System.out.println("big problem");
		}
		
		
	}
	
}
