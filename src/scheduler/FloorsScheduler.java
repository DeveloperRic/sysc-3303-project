package scheduler;

import util.Communication.Selector;

/**
 * This class limits the access of the MainScheduler to only the floor put and
 * get functions
 *
 */
public class FloorsScheduler implements SchedulerType<FloorRequest, String> {

	// The main scheduler object
	private MainScheduler s;

	/**
	 * Instantiates the shared main scheduler
	 * 
	 * @param s the shared main scheduler
	 */
	public FloorsScheduler(MainScheduler s) {
		this.s = s;
	}

	/**
	 * Allows access to the MainScheduler.floorGet function
	 * 
	 * @returns an object from MainScheduler.floorGet
	 */
	@Override
	public synchronized String get(Selector selector) {
		return s.floorCommunication.aGet(selector).getAcknowledgement();
	}

	/**
	 * Allows access to the MainScheduler.floorPut function
	 * 
	 * @returns a boolean from MainScheduler.floorPut
	 */
	@Override
	public synchronized void put(FloorRequest o) {
		s.floorCommunication.aPut(o);
	}

}
