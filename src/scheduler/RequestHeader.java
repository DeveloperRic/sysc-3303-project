package scheduler;

import java.nio.ByteBuffer;

import util.ByteUtils;

public class RequestHeader {
	
	public static final int HEADER_SIZE = 3 * 4;

	public enum RequestType {
		GET_DATA(0), SEND_DATA(1);
		
		private int id;
		
		RequestType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		public static RequestType valueOf(int id) {
			for (RequestType type : values()) {
				if (type.id == id) return type;
			}
			return null;
		}
	}
	
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
		buffer.putInt(type.id);
		buffer.putInt(portToReplyTo);
		buffer.putInt(subsystemNumber);
		return buffer.array();
	}
	
	public byte[] attachDataBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + bytes.length);
		buffer.put(getBytes());
		buffer.put(bytes);
		System.out.println("attaching " + ByteUtils.toString(buffer.array()));
		return buffer.array();
	}
	
	public static RequestHeader parseFromBytes(byte[] bytes) {
		System.out.println("parsing " + ByteUtils.toString(bytes));
		ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, HEADER_SIZE);
		return new RequestHeader(
				RequestType.valueOf(buffer.getInt()), 
				buffer.getInt(), 
				buffer.getInt());
	}
}
