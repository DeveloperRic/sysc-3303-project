package scheduler;

import util.Communication.Selector;

/**
 * The scheduler interface that controls the put and get functions *
 */
public interface SchedulerType<A, B> {

	public B get(Selector selector);
	
	public void put(A o);
}
