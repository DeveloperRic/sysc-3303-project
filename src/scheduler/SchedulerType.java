package scheduler;

import java.io.IOException;

import util.Communication.Selector;

/**
 * The scheduler interface that controls the put and get functions *
 */
public interface SchedulerType<A, B> {

	public B get(Selector selector) throws IOException;

	public void put(A o) throws IOException;
}
