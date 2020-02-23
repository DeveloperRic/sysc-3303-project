package scheduler;

import util.Communication.Selector;

/**
 * This class limits the access of the MainScheduler to only the elevator put
 * and get functions
 *
 */
public class ElevatorScheduler implements SchedulerType<ElevatorMessage, FloorRequest> {

	// The main scheduler object
	private MainScheduler s;

	/**
	 * Instantiates the shared main scheduler
	 * 
	 * @param s the shared main scheduler
	 */
	public ElevatorScheduler(MainScheduler s) {
		this.s = s;
	}

	/**
	 * Allows access to the MainScheduler.elevatorGet function
	 * 
	 * @returns an object from MainScheduler.elevatorGet
	 */
	@Override
	public FloorRequest get(Selector selector) {
		return s.elevatorCommunication.bGet(selector);
	}

	/**
	 * Allows access to the MainScheduler.elevatorPut function
	 * 
	 * @returns a boolean from MainScheduler.elevatorPut
	 */
	@Override
	public void put(ElevatorMessage o) {
		s.elevatorCommunication.bPut(o);
	}

	public void delay(ElevatorMessage o) {
		s.elevatorCommunication.delayBPut(o, (int) Math.ceil(3 / MainScheduler.getNumberOfElevators()));
	}

}
