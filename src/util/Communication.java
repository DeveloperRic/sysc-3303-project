package util;

import java.util.ArrayList;
import java.util.List;

public class Communication<A, B> {
	private List<A> aToB;
	private List<B> bToA;
	private String aName;
	private String bName;
	private boolean verbose;

	public Communication(String aName, String bName) {
		aToB = new ArrayList<>();
		bToA = new ArrayList<>();
		this.aName = aName;
		this.bName = bName;
	}

	public Communication<B, A> reverse() {
		Communication<B, A> communication = new Communication<>(bName, aName);
		communication.aToB = bToA;
		communication.bToA = aToB;
		return communication;
	}

	public void aPut(A something) {
		synchronized (aToB) {
			if (verbose) {
				Printer.print("COMMUNICATION: " + aName + " SENDING message to " + bName + "\n Content : "
						+ something + "\n");
			}
			aToB.add(something);
			aToB.notifyAll();
		}
	}

	public void bPut(B something) {
		synchronized (bToA) {
			if (verbose) {
				Printer.print("COMMUNICATION: " + bName + " SENDING message to " + aName + "\n Content : "
						+ something + "\n");
			}
			bToA.add(something);
			bToA.notifyAll();
		}
	}

	public void delayBPut(B something, int secondsDelay) {
		if (verbose) {
			Printer.print("COMMUNICATION: Delaying message from " + bName + " -> " + aName + "\n");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(secondsDelay * 1000);
				} catch (InterruptedException e) {
				}
				bPut(something);
			}
		}).start();
	}

	public B aGet() {
		return aGet(new Selector() {
			@Override
			public boolean equals(Object obj) {
				return true;
			}
		});
	}

	public B aGet(Selector selector) {
		if (selector == null)
			return aGet();

		synchronized (bToA) {
			B obj;
			while ((obj = pluckItem(bToA, selector)) == null) {
				try {
					bToA.wait();
				} catch (InterruptedException e) {
				}
			}

			if (verbose) {
				Printer.print(
						"COMMUNICATION: " + aName + " RECEIVING message from " + bName + "\n Content : " + obj + "\n");
			}

			return obj;
		}
	}

	public A bGet() {
		return bGet(new Selector() {
			@Override
			public boolean equals(Object obj) {
				return true;
			}
		});
	}

	public A bGet(Selector selector) {
		if (selector == null)
			return bGet();

		synchronized (aToB) {
			A obj;
			while ((obj = pluckItem(aToB, selector)) == null) {
				try {
					aToB.wait();
				} catch (InterruptedException e) {
				}
			}

			if (verbose) {
				Printer.print(
						"COMMUNICATION: " + bName + " RECEIVING message from " + aName + "\n Content : " + obj + "\n");
			}

			aToB.notifyAll();
			return obj;
		}
	}

	public B aPeek() {
		synchronized (bToA) {
			while (bToA.isEmpty()) {
				try {
					bToA.wait();
				} catch (InterruptedException e) {
				}
			}
			notifyAll();
			return bToA.get(0);
		}
	}

	public A bPeek() {
		synchronized (aToB) {
			while (aToB.isEmpty()) {
				try {
					aToB.wait();
				} catch (InterruptedException e) {
				}
			}
			aToB.notifyAll();
			return aToB.get(0);
		}
	}

	public boolean aRemove(B something) {
		synchronized (bToA) {
			bToA.notifyAll();
			return bToA.remove(something);
		}
	}

	public boolean bRemove(A something) {
		synchronized (aToB) {
			aToB.notifyAll();
			return aToB.remove(something);
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public interface Selector {
		boolean equals(Object o);
	}

	private <T> T pluckItem(List<T> list, Selector selector) {
		synchronized (list) {
			for (T obj : list) {
				if (selector.equals(obj)) {
					list.remove(obj);
					list.notifyAll();
					return obj;
				}
			}
			return null;
		}
	}

}