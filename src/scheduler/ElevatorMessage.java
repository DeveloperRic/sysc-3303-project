package scheduler;

import java.nio.ByteBuffer;
import java.util.Arrays;
import util.Printer;

/** ElevatorMessage.java
 * This class is used as a communication class between the Elevator/ElevatorSubsytem 
 * and the ElevatorScheduler/MainScheduler.
 *
 */
import elevator.ElevatorSubsystem;

public abstract class ElevatorMessage {

	/**
	 * Returns the FloorRequest message saved at run time.
	 * FloorRequest.getFloorRequest().getRequest[0] contains the floor destination
	 * to go to. FloorRequest.getFloorRequest().getRequest[1] contains the direction
	 * the elevator should be going in.
	 * 
	 * @return FloorRequest object The FloorRequest class which contains the
	 *         destination and direction.
	 * 
	 */
	public FloorMessage getFloorRequest() {
		return null;
	}

	/**
	 * Returns the acknowledgement message saved at run time to be sent back to the
	 * scheduler if the elevator accepts the work.
	 * 
	 * @return String object The acknowledgement message to confirm the elevator is
	 *         doing this work.
	 */
	public String getAcknowledgement() {
		return null;
	}

	public Integer getFloorArrivedOn() {
		return null;
	}
	
	//Integer[] of size 2
	//Integer[0] indicates if its being recommissioned (1) or decommissioned (0)
	//Integer[1] indicates the elevator number
	public Integer[] getFaultNotice() {
		return null;
	}

	/**
	 * Returns the formatted String message of the FloorRequest and Acknowledgement.
	 * 
	 * @return String object The formatted String of the data in this class.
	 */
	@Override
	public String toString() {
		FloorMessage req = getFloorRequest();
		String ack = getAcknowledgement();
		return "<ElevMsg: " + (req != null ? "(" + req + ")" : "") + (ack != null ? "(<Ack: " + ack + ")" : "") + ">";
	}

	/**
	 * Converts the ElevatorMessage into a byte[] to be sent through UDP. byte[0] is
	 * the length of the FloorRequest message. byte[1] is the length of the
	 * Acknowledgement message. byte[2] through byte[2 + (floorRequest.length - 1)]
	 * is the floorRequest message. byte[2 + floorRequest.length] through
	 * byte[(2+floorRequest.length) + (acknowledgement.length - 1)] is the
	 * acknowledgement message.
	 * 
	 * @return byte[] The byte[] version of the ElevatorMessage.
	 */
	public byte[] serialize() {
		byte[] floorRequest = null;
		byte[] acknowledgement = null;

		int faultNoticeLength = 0;
		int bufferLength = 3 * 4;

		if (getFloorRequest() != null) {
			floorRequest = getFloorRequest().serialize();
			bufferLength += floorRequest.length;
		}

		if (getAcknowledgement() != null) {
			acknowledgement = getAcknowledgement().getBytes();
			bufferLength += acknowledgement.length + 4;
		}

		if (getFaultNotice() != null) {
			bufferLength += faultNoticeLength = getFaultNotice().length * 4;
		}

		ByteBuffer buffer = ByteBuffer.allocate(bufferLength);

		buffer.putInt(floorRequest != null ? floorRequest.length : 0);
		buffer.putInt(acknowledgement != null ? acknowledgement.length : 0);
		buffer.putInt(faultNoticeLength);

		if (floorRequest != null) {
			buffer.put(floorRequest);
		}
		if (acknowledgement != null) {
			buffer.put(acknowledgement);
			buffer.putInt(getFloorArrivedOn());
		}
		if (faultNoticeLength > 0) {
			for (Integer i : getFaultNotice())
				buffer.putInt(i);
		}

		byte[] bytes = buffer.array();

		if (ElevatorSubsystem.verbose)
			Printer.print("Elevator Messages Test: " + Arrays.toString(bytes));
		return bytes;
	}

	/**
	 * Converts the serialized byte[] back into an ElevatorMessage. Retrieves the
	 * FloorMessage if any. Retrieves the Acknowledgement if any.
	 * 
	 * @param bytes The byte array that is to be converted back into an
	 *              ElevatorMessage.
	 * 
	 * @return ElevatorMessage object The class that contains the FloorRequest
	 *         and/or the Acknowledgement.
	 */
	public static ElevatorMessage deserialize(byte[] bytes) {

		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		int floorRequestLength = buffer.getInt();
		int acknowledgementLength = buffer.getInt();
		int faultNoticeLength = buffer.getInt();

		FloorMessage floorRequest;
		if (floorRequestLength > 0) {
			byte[] floorRequestBytes = new byte[floorRequestLength];
			buffer.get(floorRequestBytes, 0, floorRequestLength);
			floorRequest = FloorMessage.deserialize(floorRequestBytes);
		} else {
			floorRequest = null;
		}

		String acknowledgement;
		Integer arrivalFloor;
		if (acknowledgementLength > 0) {
			byte[] acknowledgementBytes = new byte[acknowledgementLength];
			buffer.get(acknowledgementBytes, 0, acknowledgementLength);
			acknowledgement = new String(acknowledgementBytes);
			arrivalFloor = buffer.getInt();
		} else {
			acknowledgement = null;
			arrivalFloor = null;
		}

		Integer[] faultNotice;
		if (faultNoticeLength > 0) {
			faultNotice = new Integer[faultNoticeLength / 4];
			for (int i = 0; i < faultNotice.length; ++i)
				faultNotice[i] = buffer.getInt();
		} else {
			faultNotice = null;
		}

		return new ElevatorMessage() {
			@Override
			public FloorMessage getFloorRequest() {
				return floorRequest;
			}

			@Override
			public String getAcknowledgement() {
				return acknowledgement;
			}

			@Override
			public Integer getFloorArrivedOn() {
				return arrivalFloor;
			}

			@Override
			public Integer[] getFaultNotice() {
				return faultNotice;
			}
		};
	}
}