package scheduler;

import java.nio.ByteBuffer;
import java.util.Arrays;

import elevator.ElevatorSubsystem;

public abstract class ElevatorMessage {
	public FloorRequest getFloorRequest() {
		return null;
	}

	public String getAcknowledgement() {
		return null;
	}

	@Override
	public String toString() {
		FloorRequest req = getFloorRequest();
		String ack = getAcknowledgement();
		return "<ElevMsg: " + (req != null ? "(" + req + ")" : "") + (ack != null ? "(<Ack: " + ack + ")" : "") + ">";
	}

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