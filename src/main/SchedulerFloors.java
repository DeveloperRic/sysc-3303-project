/**
 * @author Kevin
 *
 */
package main;

/**
 * This class limits the access of the MainScheduler to 
 * only the floor put and get functions
 * 
 * @author Kevin
 *
 */
public class SchedulerFloors implements Scheduler{

	//The main scheduler object
	private MainScheduler s;
	
	/**
	 * Instantiates the shared main scheduler
	 * 
	 * @param s the shared main scheduler
	 */
	public SchedulerFloors(MainScheduler s) {
		this.s = s;
	}
	
	/**
	 * Allows access to the MainScheduler.floorGet function
	 * 
	 * @returns an object from MainScheduler.floorGet
	 */
	@Override
	public synchronized Object get() {
		return s.floorGet();
	}

	/**
	 * Allows access to the MainScheduler.floorPut function
	 * 
	 * @returns a boolean from MainScheduler.floorPut
	 */
	@Override
	public synchronized boolean put(Object o) {
		return s.floorPut(o);
		
	}

}
