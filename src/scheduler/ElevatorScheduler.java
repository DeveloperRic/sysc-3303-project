package scheduler;

/**
 * This class limits the access of the MainScheduler to 
 * only the elevator put and get functions
 * 
 * @author Kevin
 *
 */
public class ElevatorScheduler implements SchedulerType{
	
	//The main scheduler object
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
	public synchronized Object get() {
		return s.elevatorGet();
	}

	/**
	 * Allows access to the MainScheduler.elevatorPut function
	 * 
	 * @returns a boolean from MainScheduler.elevatorPut
	 */
	@Override
	public synchronized boolean put(Object o) {
		return s.elevatorPut(o);
	}

}
