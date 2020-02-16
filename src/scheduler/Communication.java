package scheduler;

import java.util.ArrayList;
import java.util.List;


public class Communication<A, B> {
	private List<A> aToB;
	private List<B> bToA;
	private String aName;
	private String bName;

	public Communication(String aName, String bName) {
		aToB = new ArrayList<A>();
		bToA = new ArrayList<B>();
		this.aName = aName;
		this.bName = bName;
	}

	public synchronized void aPut(A workDoing, String paramText) {
		System.out.println(aName.toUpperCase() + " SUBSYSTEM: " + aName + " SENDING message to " + bName
				+ "\n Content : " + paramText + "\n");
		aToB.add(workDoing);
		notifyAll();
	}

	public synchronized void bPut(B something) {
		System.out.println(bName.toUpperCase() + " SUBSYSTEM: " + bName + " SENDING message to " + aName
				+ "\n Content : " + something + "\n");
		bToA.add(something);
		notifyAll();
	}

	public synchronized B aGet() {
		while (bToA.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		B obj = bToA.remove(0);

		System.out.println(aName.toUpperCase() + " SUBSYSTEM: " + aName + " RECEIVING message from " + bName
				+ "\n Content : " + obj + "\n");
		

		notifyAll();
		return obj;
	}

	public synchronized A bGet() {
		while (aToB.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		A obj = aToB.remove(0);

		System.out.println(bName.toUpperCase() + " SUBSYSTEM: " + bName + " RECEIVING message from " + aName
				+ "\n Content : " + obj + "\n");
		
		notifyAll();
		return obj;
	}

	public synchronized B aPeek() {
		while (bToA.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		notifyAll();
		return bToA.get(0);
	}

	public synchronized A bPeek() {
		while (aToB.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		notifyAll();
		return aToB.get(0);
	}

}