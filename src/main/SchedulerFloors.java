/**
 * @author Kevin
 *
 */
package main;
public class SchedulerFloors implements Scheduler{

	private MainScheduler s;
	
	public SchedulerFloors(MainScheduler s) {
		this.s = s;
	}
	
	@Override
	public synchronized Object get() {
		return s.floorGet();
	}

	@Override
	public synchronized boolean put(Object o) {
		return s.floorPut(o);
		
	}

}
