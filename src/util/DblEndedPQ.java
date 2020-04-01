package util;

// Java program to implement double-ended  
// priority queue using self balancing BST.  
import java.util.*;

public final class DblEndedPQ<T> {
	Set<T> s;

	public DblEndedPQ() {
		s = new HashSet<T>();
	}

	// Returns size of the queue. Works in
	// O(1) time
	public int size() {
		return s.size();
	}

	// Returns true if queue is empty. Works
	// in O(1) time
	public boolean isEmpty() {
		return (s.size() == 0);
	}

	// Inserts an element. Works in O(Log n)
	// time
	public void insert(T x) {
		s.add(x);

	}

	// Returns minimum element. Works in O(1)
	// time
	public T getMin() {
		return Collections.min(s, null);
	}

	// Returns maximum element. Works in O(1)
	// time
	public T getMax() {
		return Collections.max(s, null);
	}

	// Deletes minimum element. Works in O(Log n)
	// time
	public void deleteMin() {
		if (s.size() == 0)
			return;
		s.remove(Collections.min(s, null));

	}

	// Deletes maximum element. Works in O(Log n)
	// time
	public void deleteMax() {
		if (s.size() == 0)
			return;
		s.remove(Collections.max(s, null));

	}

	public Object[] toArray() {
		return s.toArray();
	}
}