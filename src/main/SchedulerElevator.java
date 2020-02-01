/**
 * @author Kevin
 *
 */
package main;
public class SchedulerElevator implements Scheduler{
	
	private MainScheduler s;
	
	public SchedulerElevator(MainScheduler s) {
		this.s = s;
	}

	@Override
	public synchronized Object get() {
		return s.elevatorGet();
	}

	@Override
	public synchronized boolean put(Object o) {
		return s.elevatorPut(o);
	}

}
