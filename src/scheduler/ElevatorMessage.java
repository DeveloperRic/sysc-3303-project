package scheduler;

import java.nio.ByteBuffer;
import java.util.Arrays;
import util.Printer;

import elevator.ElevatorSubsystem;

public abstract class ElevatorMessage {
	
    /**
     * Returns the FloorRequest message saved at run time.
     * FloorRequest.getFloorRequest().getRequest[0] contains the floor destination to go to.
     * FloorRequest.getFloorRequest().getRequest[1] contains the direction the elevator should be going in.
     * 
     * @return FloorRequest object The FloorRequest class which contains the destination and direction.
     * 
     */
	public FloorRequest getFloorRequest() {
		return null;
	}

    /**
     * Returns the acknowledgement message saved at run time
     * to be sent back to the scheduler if the elevator accepts the work.
     * 
     * @return String object The acknowledgement message to confirm the elevator is doing this work.
     */
	public String getAcknowledgement() {
		return null;
	}

    /**
     * Returns the formatted String message of the FloorRequest and Acknowledgement.
     * 
     * @return String object The formatted String of the data in this class.
     */
	@Override
	public String toString() {
		FloorRequest req = getFloorRequest();
		String ack = getAcknowledgement();
		return "<ElevMsg: " + (req != null ? "(" + req + ")" : "") + (ack != null ? "(<Ack: " + ack + ")" : "") + ">";
	}

    /**
     * Converts the ElevatorMessage into a byte[] to be sent through UDP.
     * byte[0] is the length of the FloorRequest message.
     * byte[1] is the length of the Acknowledgement message.
     * byte[2] through byte[2 + (floorRequest.length - 1)] is the floorRequest message.
     * byte[2 + floorRequest.length] through byte[(2+floorRequest.length) + (acknowledgement.length - 1)] is the acknowledgement message.
     * 
     * @return byte[] The byte[] version of the ElevatorMessage.
     */
	public byte[] serialize() {
		byte[] floorRequest = null;
		byte[] acknowledgement = null;

		int bufferLength = 2 * 4;

		if (getFloorRequest() != null) {
			floorRequest = getFloorRequest().serialize();
			bufferLength += floorRequest.length;
		}

		if (getAcknowledgement() != null) {
			acknowledgement = getAcknowledgement().getBytes();
			bufferLength += acknowledgement.length;
		}

//		byte[] bytes = new byte[numbersLength];
//		int i = 0, j;

//		final int HEAD_SIZE = 2;

//		bytes[i] = Integer.valueOf(floorRequest != null ? floorRequest.length : 0).byteValue();
//		bytes[++i] = Integer.valueOf(acknowledgement != null ? acknowledgement.length : 0).byteValue();
//
//		i++;
//
//		if (floorRequest != null) {
//			j = i = HEAD_SIZE;
//			for (; i - j < floorRequest.length; ++i) {
//				bytes[i] = floorRequest[i - j];
//			}
//		}
//
//		if (acknowledgement != null) {
//			j = i;
//			for (; i - j < acknowledgement.length; ++i) {
//				bytes[i] = acknowledgement[i - j];
//			}
//		}
//
//		
//		return bytes;

		ByteBuffer buffer = ByteBuffer.allocate(bufferLength);

		buffer.putInt(floorRequest != null ? floorRequest.length : 0);
		buffer.putInt(acknowledgement != null ? acknowledgement.length : 0);

		if (floorRequest != null) {
			buffer.put(floorRequest);
		}
		if (acknowledgement != null) {
			buffer.put(acknowledgement);
		}

		byte[] bytes = buffer.array();

		if (ElevatorSubsystem.verbose)
			System.out.println("Elevator Messages Test: " + Arrays.toString(bytes));
		return bytes;
	}

    /**
     * Converts the serialized byte[] back into an ElevatorMessage.
     * Retrieves the FloorMessage if any.
     * Retrieves the Acknowledgement if any.
     * 
     * @param bytes The byte array that is to be converted back into an ElevatorMessage.
     * 
     * @return ElevatorMessage object The class that contains the FloorRequest and/or the Acknowledgement.
     */
	public static ElevatorMessage deserialize(byte[] bytes) {
//		final int HEAD_SIZE = 2;
//		final int floorRequestEnd = HEAD_SIZE + ((Byte) bytes[0]).intValue() - 1;
//		final int acknowledgementEnd = floorRequestEnd + ((Byte) bytes[1]).intValue();
//
//		int i = HEAD_SIZE, j;
//
//		// initialize floor request
//		FloorRequest floorRequest;
//		byte[] floorRequestBytes = new byte[floorRequestEnd + 1 - HEAD_SIZE];
//		if (floorRequestEnd != HEAD_SIZE - 1) {
//			j = i;
//			for (; i <= floorRequestEnd; ++i) {
//				floorRequestBytes[i - j] = bytes[i];
//			}
//			floorRequest = FloorRequest.deserialize(floorRequestBytes);
//		} else {
//			floorRequest = null;
//		}
//
//		// initialize acknowledgement
//		String acknowledgement;
//		if (acknowledgementEnd != floorRequestEnd) {
//
//			byte[] acknowledgementBytes = new byte[acknowledgementEnd - floorRequestBytes.length];
//			j = floorRequestEnd + 1;
//			for (; i <= acknowledgementEnd; ++i) {
//				acknowledgementBytes[i - j] = bytes[i];
//			}
//			acknowledgement = new String(acknowledgementBytes);
//		} else {
//			acknowledgement = null;
//		}
		
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		int floorRequestLength = buffer.getInt();
		int acknowledgementLength = buffer.getInt();
		
		FloorRequest floorRequest;
		if (floorRequestLength > 0) {
			byte[] floorRequestBytes = new byte[floorRequestLength];
			buffer.get(floorRequestBytes, 0, floorRequestLength);
			floorRequest = FloorRequest.deserialize(floorRequestBytes);
		} else {
			floorRequest = null;
		}
		
		String acknowledgement;
		if (acknowledgementLength > 0) {
			byte[] acknowledgementBytes = new byte[acknowledgementLength];
			buffer.get(acknowledgementBytes, 0, acknowledgementLength);
			acknowledgement = new String(acknowledgementBytes);
		} else {
			acknowledgement = null;
		}

		return new ElevatorMessage() {
			@Override
			public FloorRequest getFloorRequest() {
				return floorRequest;
			}

			@Override
			public String getAcknowledgement() {
				return acknowledgement;
			}
		};
	}
}