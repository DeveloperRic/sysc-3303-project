package scheduler;

import java.nio.ByteBuffer;

public class RequestHeader {

	public enum RequestType {
		GET_DATA((byte) 0), SEND_DATA((byte) 1);

		private byte id;

		RequestType(byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}

		public static RequestType valueOf(byte id) {
			for (RequestType type : values()) {
				if (type.id == id)
					return type;
			}
			return null;
		}
	}

	public static final int HEADER_SIZE = 1 + 2 * 4;

	private RequestType type;
	private int portToReplyTo;
	private int subsystemNumber;

	public RequestHeader(RequestType type) {
		this.type = type;
		this.portToReplyTo = this.subsystemNumber = Integer.MIN_VALUE;
	}

	public RequestHeader(RequestType type, int portToReplyTo) {
		this.type = type;
		this.portToReplyTo = portToReplyTo;
		this.subsystemNumber = Integer.MIN_VALUE;
	}

	public RequestHeader(RequestType type, int portToReplyTo, int subsystemNumber) {
		this.type = type;
		this.portToReplyTo = portToReplyTo;
		this.subsystemNumber = subsystemNumber;
	}

	public RequestType getType() {
		return type;
	}

	public Integer getPortToReplyTo() {
		return portToReplyTo == Integer.MIN_VALUE ? null : portToReplyTo;
	}

	public Integer getSubsystemNumber() {
		return subsystemNumber == Integer.MIN_VALUE ? null : subsystemNumber;
	}

	public byte[] getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
		buffer.put(type.id);
		buffer.putInt(portToReplyTo);
		buffer.putInt(subsystemNumber);
		return buffer.array();
	}

	public byte[] attachDataBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + bytes.length);
		buffer.put(getBytes());
		buffer.put(bytes);
		return buffer.array();
	}

	public static RequestHeader parseFromBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, HEADER_SIZE);
		return new RequestHeader(RequestType.valueOf(buffer.get()), buffer.getInt(), buffer.getInt());
	}
}
