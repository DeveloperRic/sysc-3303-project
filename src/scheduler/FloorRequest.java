package scheduler;

import java.util.Arrays;

import util.ByteUtils;

public abstract class FloorRequest {
	public Float[] responses = new Float[MainScheduler.getNumberOfElevators()];
	public int numResponses = 0;
	public int selectedElevator = -1;

	public abstract Integer[] getRequest();

	public Integer getSourceElevator() {
		return null;
	}

	public synchronized byte[] serialize() {
		Integer[] request = getRequest();

		byte[] bytes = new byte[2 + request.length + (responses.length * 4) + 2];
		int i = 0, j;

		// register lengths of arrays (for deserialize)
		bytes[i] = Integer.valueOf(request.length).byteValue();
		bytes[++i] = Integer.valueOf(responses.length * 4).byteValue();

		// serialize the request
		j = ++i;
		for (; i - j < request.length; ++i) {
			bytes[i] = request[i - j].byteValue();
		}

		// serialize the responses array
		j = i;
		for (; i - j < responses.length; i += 4) {
			Float response = responses[i - j];
			byte[] responseBytes = ByteUtils.floatToBytes(response != null ? response : Float.NEGATIVE_INFINITY);
			
			for (int k = i; k - i < 4; ++k) {
				bytes[k] = responseBytes[k - i];
			}
		}

		// add numResponses to byte array
		bytes[i] = Integer.valueOf(numResponses).byteValue();

		// add selectedElevator to byte array
		bytes[++i] = Integer.valueOf(selectedElevator).byteValue();

		return bytes;
	}

	public static FloorRequest deserialize(byte[] bytes) {
		System.out.println("flr deserializing " + ByteUtils.toString(bytes));
		final int HEAD_SIZE = 2;
		final int requestEnd = HEAD_SIZE + ((Byte) bytes[0]).intValue() - 1;
		final int responseEnd = requestEnd + ((Byte) bytes[1]).intValue();

		int i = HEAD_SIZE, j;

		// initialize request
		Integer[] request = new Integer[requestEnd + 1 - HEAD_SIZE];
		j = i;
		for (; i <= requestEnd; ++i) {
			request[i - j] = ((Byte) bytes[i]).intValue();
		}

		// initialize responses
		Float[] responses = new Float[(responseEnd - requestEnd) / 4];
		j = i;
		for (; i <= responseEnd; i += 4) {
			byte[] responseBytes = new byte[4];
			
			for (int k = i; k - i < 4; ++k) {
				responseBytes[k - i] = bytes[k];
			}
			
			Float response = ByteUtils.bytesToFloat(responseBytes);
			
			System.out.println("= " + response);

			if (response != Float.NEGATIVE_INFINITY) {
				responses[(i - j) / 4] = response;
			}
		}
		System.out.println("~ " + Arrays.toString(responses.clone()));

		// initialize integers
		int numResponses = ((Byte) bytes[i]).intValue();
		int selectedElevator = ((Byte) bytes[++i]).intValue();

		// create floor request
		FloorRequest floorRequest = new FloorRequest() {
			@Override
			public Integer[] getRequest() {
				return request;
			}
		};
		floorRequest.responses = responses;
		floorRequest.numResponses = numResponses;
		floorRequest.selectedElevator = selectedElevator;

		return floorRequest;
	}
}