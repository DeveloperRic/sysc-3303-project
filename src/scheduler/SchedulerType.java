/**
 * @author Kevin
 *
 */
package scheduler;

/**
 * The scheduler interface that controls the put and get functions
 * 
 * @author Kevin
 *
 */
public interface SchedulerType {

	public Object get();
	
	public boolean put(Object o);
}
