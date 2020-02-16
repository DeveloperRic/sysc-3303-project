/**
 * @author Kevin
 *
 */
package scheduler;

/**
 * This class limits the access of the MainScheduler to 
 * only the floor put and get functions
 * 
 * @author Kevin
 *
 */
public class FloorsScheduler implements SchedulerType{

	//The main scheduler object
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
	public synchronized Object get() {
		return s.floorCommunication.aGet();
	}

	/**
	 * Allows access to the MainScheduler.floorPut function
	 * 
	 * @returns a boolean from MainScheduler.floorPut
	 */
	@Override
	public synchronized boolean put(Object o) {
		return s.floorPut((Integer[]) o);
		
	}

}
