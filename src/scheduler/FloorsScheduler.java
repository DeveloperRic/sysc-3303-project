package scheduler;

import util.Communication.Selector;
import util.Printer;

import java.nio.ByteBuffer;

import util.Transport;

/**
 * This class limits the access of the MainScheduler to only the floor put and
 * get functions
 *
 */
public class FloorsScheduler implements SchedulerType<FloorRequest, String> {
//	public static final int FLOOR_PORT = 63971;

	// The main scheduler object
	private Transport t;

	/**
	 * Instantiates the floor scheduler (lives in floor-subsystem runtime)
	 */
	public FloorsScheduler() {
		t = new Transport("Floor", -1, true);
		t.setDestinationRole("Scheduler");
		t.setDestinationPort(MainScheduler.PORT_FOR_FLOOR);
		Printer.print("Floor send/receive socket bound on port " + t.getReceivePort() + "\n");
	}

	/**
	 * Allows access to the MainScheduler.floorGet function
	 * 
	 * @returns an object from MainScheduler.floorGet
	 */
	@Override
	public String get(Selector selector) {
//		return s.floorCommunication.aGet(selector).getAcknowledgement();
		t.send(new byte[0]);
		Printer.print("--->[] Floor waiting to receive");
		String s = "";
		try {
			s = ElevatorMessage.deserialize((byte[]) t.receive()[0]).getAcknowledgement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Allows access to the MainScheduler.floorPut function
	 * 
	 * @returns a boolean from MainScheduler.floorPut
	 */
	@Override
	public void put(FloorRequest o) {
//		s.floorCommunication.aPut(o);
		byte[] requestBytes = o.serialize();
		ByteBuffer buffer = ByteBuffer.allocate(requestBytes.length + 4);

		buffer.putInt(t.getReceivePort());
		buffer.put(requestBytes);

		t.send(buffer.array());

		// receive confirmation of message received
		Printer.print("--->[conf] Floor waiting to receive");
		try {
			t.receive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Transport getTransport() {
		return t;
	}

}
