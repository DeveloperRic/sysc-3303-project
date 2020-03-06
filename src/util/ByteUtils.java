package util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ByteUtils {

	/**
	 * Converts a byte string (not using a text format) E.g. returns "123"
	 * 
	 * @param byte
	 */
	public static String toString(byte aByte) {
		return toString(new byte[] { aByte });
	}

	/**
	 * Converts a byte array to string (not using a text format) E.g. returns "0 1 2
	 * 3 4"
	 * 
	 * @param bytes
	 */
	public static String toString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
//			for (int pos = 0; pos <= 7; ++pos) {
//				sb.append((b >> pos) & 1);
//			}
//			sb.append(" ");
			sb.append(((Byte) b).intValue()).append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * Converts a floating point number to an array of exactly 4 bytes
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] floatToBytes(float value) {
		return ByteBuffer.allocate(4).putFloat(value).array();
	}

	/**
	 * Converts a byte array into a floating point number using the first 4 bytes
	 * 
	 * @param bytes
	 * @return
	 */
	public static float bytesToFloat(byte[] bytes) {
//		System.out.println("b->f " + toString(bytes) + " | " + Arrays.toString(ByteBuffer.wrap(bytes, 0, 4).array()));
		return ByteBuffer.wrap(bytes, 0, 4).getFloat();
	}

}
