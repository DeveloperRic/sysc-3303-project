package scheduler;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * FloorRequest.java This class is used as a communication class to store the
 * floor destination and the direct so the elevator can use it.
 *
 */
public abstract class FloorRequest {
	// Meant to store the ETA of each elevator so the scheduler can decide which
	// elevator to choose.
	public Float[] responses = new Float[MainScheduler.getNumberOfElevators()];
	// Keeps track of the valid number of responses in the response[]
	public int numResponses = 0;
	// The elevator chosen to go do this Floor Request
	public int selectedElevator = -1;
	public Integer sourceElevator;

	/**
	 * Returns the floor destination and direction message saved at run time.
	 * FloorRequest.getRequest()[0] contains the floor destination to go to.
	 * FloorRequest.getRequest()[1] contains the direction the elevator should be
	 * going in.
	 * 
	 * @return Integer[] The floor destination and the direction.
	 */
	public abstract Integer[] getRequest();

	/**
	 * Returns the button pressed inside the elevator if any.
	 * 
	 * @return Integer object The number pressed.
	 */
	public Integer getSourceElevator() {
		return sourceElevator;
	}

	/**
	 * Returns the formatted String message of the Floor destination and direction.
	 * 
	 * @return String object The formatted String of the data in this class.
	 */
	@Override
	public String toString() {
		Integer[] req = getRequest();
		return "<FlrReq: (fl " + req[0] + (req[1] != 0 ? " going " + (req[1] == 1 ? "up" : "down") : "")
				+ (getSourceElevator() != null ? " (src-elev " + getSourceElevator() + ")" : "") + " (sel-elev "
				+ selectedElevator + ")" + " (res " + Arrays.toString(responses.clone()) + ")" + ")>";
	}

	/**
	 * Converts the FloorRequest into a byte[] to be sent through UDP. byte[0] is
	 * the length of the request array. byte[1] is the length of the number of
	 * responses * 4(number of bytes in a float). Stores the request and responses
	 * data in the middle of the byte[] byte[byte.length-2] is numResponses stored.
	 * byte[byte.length-1] is the selectedElevator stored.
	 * 
	 * @return byte[] The byte[] version of the FloorRequest.
	 */
	public synchronized byte[] serialize() {
		Integer[] request = getRequest();

//		byte[] bytes = new byte[2 + request.length + (responses.length * 4) + 3];
//		int i = 0, j;
//
//		// register lengths of arrays (for deserialize)
//		bytes[i] = Integer.valueOf(request.length).byteValue();
//		bytes[++i] = Integer.valueOf(responses.length * 4).byteValue();
//
//		// serialize the request
//		j = ++i;
//		for (; i - j < (request.length); ++i) {
//			// System.out.println("Check1: " + i + " " + j + " " + request.length);
//			bytes[i] = request[i - j].byteValue();
//		}
//
//		// serialize the responses array
//		j = i;
//		for (; i - j < responses.length; i += 4) {
//			Float response = responses[i - j];
//			byte[] responseBytes = ByteUtils.floatToBytes(response != null ? response : Float.NEGATIVE_INFINITY);
//
//			for (int k = i; k - i < 4; ++k) {
//				bytes[k] = responseBytes[k - i];
//			}
//		}
//
//		// add numResponses to byte array
//		bytes[i] = Integer.valueOf(numResponses).byteValue();
//
//		// add selectedElevator to byte array
//		bytes[++i] = Integer.valueOf(selectedElevator).byteValue();
//
//		// add sourceElevator to byte array
//		bytes[++i] = getSourceElevator() != null ? getSourceElevator().byteValue() : -1;
//
//		return bytes;

		ByteBuffer buffer = ByteBuffer.allocate((2 + request.length + responses.length + 3) * 4);

		buffer.putInt(request.length);
		buffer.putInt(responses.length);

		for (Integer num : request) {
			buffer.putInt(num);
		}
		for (Float response : responses) {
			buffer.putFloat(response != null ? response : Float.NEGATIVE_INFINITY);
		}

		buffer.putInt(numResponses);
		buffer.putInt(selectedElevator);
		buffer.putInt(getSourceElevator() != null ? getSourceElevator().byteValue() : -1);

		return buffer.array();
	}

	/**
	 * Converts the serialized byte[] back into an FloorMessage. Retrieves the Floor
	 * Destination and Direction if any. Retrieves the Responses if any. Retrieves
	 * the Selected Elevator if any.
	 * 
	 * @param bytes The byte array that is to be converted back into an
	 *              FloorRequest.
	 * 
	 * @return FloorRequest object The class that contains the Floor Destination,
	 *         Direction and ETAs from the elevators.
	 */
	public static FloorRequest deserialize(byte[] bytes) {
//		System.out.println("flr deserializing " + ByteUtils.toString(bytes));
//		final int HEAD_SIZE = 2;
//		final int requestEnd = HEAD_SIZE + ((Byte) bytes[0]).intValue() - 1;
//		final int responseEnd = requestEnd + ((Byte) bytes[1]).intValue();
//
//		int i = HEAD_SIZE, j;
//
//		// initialize request
//		Integer[] request = new Integer[requestEnd + 1 - HEAD_SIZE];
//		j = i;
//		for (; i <= requestEnd; ++i) {
//			request[i - j] = ((Byte) bytes[i]).intValue();
//		}
//
//		// initialize responses
//		Float[] responses = new Float[(responseEnd - requestEnd) / 4];
//		j = i;
//		for (; i <= responseEnd; i += 4) {
//			byte[] responseBytes = new byte[4];
//
//			for (int k = i; k - i < 4; ++k) {
//				responseBytes[k - i] = bytes[k];
//			}
//
//			Float response = ByteUtils.bytesToFloat(responseBytes);
//
////			System.out.println("= " + response);
//
//			if (response != Float.NEGATIVE_INFINITY) {
//				responses[(i - j) / 4] = response;
//			}
//		}
////		System.out.println("~ " + Arrays.toString(responses.clone()));
//
//		// initialize integers
//		int numResponses = ((Byte) bytes[i]).intValue();
//		int selectedElevator = ((Byte) bytes[++i]).intValue();
//		int sourceElevator = ((Byte) bytes[++i]).intValue();

		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		int requestLength = buffer.getInt();
		int responsesLength = buffer.getInt();

		Integer[] request = new Integer[requestLength];
		for (int i = 1; i <= requestLength; ++i) {
			request[i - 1] = buffer.getInt();
		}

		Float[] responses = new Float[responsesLength];
		for (int i = 1; i <= responsesLength; ++i) {
			Float response = buffer.getFloat();
			responses[i - 1] = response == Float.NEGATIVE_INFINITY ? null : response;
		}

		// create floor request
		FloorRequest floorRequest = new FloorRequest() {
			@Override
			public Integer[] getRequest() {
				return request;
			}
		};

		floorRequest.responses = responses;
		floorRequest.numResponses = buffer.getInt();
		floorRequest.selectedElevator = buffer.getInt();

		int sourceElevator = buffer.getInt();
		floorRequest.sourceElevator = sourceElevator != -1 ? sourceElevator : null;

		return floorRequest;
	}
}