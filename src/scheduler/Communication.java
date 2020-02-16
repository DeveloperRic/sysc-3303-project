package scheduler;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import util.DblEndedPQ;


public class Communication<A, B> {
	private BlockingQueue<A> aToB;
	private BlockingQueue<B> bToA;
	private String aName;
	private String bName;

	public Communication(String aName, String bName) {
		aToB = new LinkedBlockingQueue<A>();
		bToA = new LinkedBlockingQueue<B>();
		this.aName = aName;
		this.bName = bName;
	}

	public synchronized void aPut(A workDoing, String paramText) {
		System.out.println(aName.toUpperCase() + " SUBSYSTEM: " + aName + " SENDING message to " + bName
				+ "\n Content : " + paramText + workDoing + "\n");
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
		B obj = bToA.remove();

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
		A obj = aToB.remove();

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
		return bToA.peek();
	}

	public synchronized A bPeek() {
		while (aToB.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		notifyAll();
		return aToB.peek();
	}

}