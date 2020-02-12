/**
 * @author Kevin
 *
 */
package main;

/**
 * The scheduler interface that controls the put and get functions
 * 
 * @author Kevin
 *
 */
public interface Scheduler {

	public Object get();
	
	public boolean put(Object o);
}
