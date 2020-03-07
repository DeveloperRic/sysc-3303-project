package util;

import java.net.*;
import java.util.Arrays;
import java.io.*;

/**
 * Using code from SYSC3303B W20<br>
 * - SimpleEchoClient.java<br>
 * <br>
 * 
 * Made for SYSC3303B W20 Assignment 2 & 3,<br>
 * Distributed for SYSC3303B W20 Project<br>
 * <br>
 * *do not copy for the assignment*<br>
 * *shared only to my team, if you're not team7 don't use this*
 * 
 * @author Victor Olaitan (101088982)
 * @author Greg Franks
 *
 */
public final class Transport {

	private static boolean verbose;

	private String role;
	private String destinationRole;
	private InetAddress address;
	private DatagramSocket sendSocket;
	private DatagramSocket receiveSocket;
	private int receivePort;
	private int destinationPort;

	/**
	 * Initializes transport class onto the specified receivePort
	 * 
	 * @param role                       any of "Client", "Server" or "Host"; or
	 *                                   other
	 * @param receivePort                port to listen on (or -1 to use any
	 *                                   available port)
	 * @param useReceiveSocketForSending use the same socket for sending and
	 *                                   receiving?
	 */
	public Transport(String role, int receivePort, boolean useReceiveSocketForSending) {
		this.role = role;
		try {
			address = InetAddress.getLocalHost();
			if (useReceiveSocketForSending) {
				if (receivePort != -1) {
					receiveSocket = new DatagramSocket(receivePort);
				} else {
					receiveSocket = new DatagramSocket();
				}
				sendSocket = receiveSocket;
			} else {
				sendSocket = new DatagramSocket();
				if (receivePort != -1) {
					receiveSocket = new DatagramSocket(receivePort);
				}
			}
			this.receivePort = receivePort;
		} catch (UnknownHostException | SocketException e) { // Can't create the socket.
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Initializes transport class (receive only)
	 * 
	 * @param role        any of "Client", "Server" or "Host"; or other
	 * @param receivePort port to listen on (or -1 to use any available port)
	 */
	public Transport(String role, int receivePort) {
		this.role = role;
		try {
			address = InetAddress.getLocalHost();
			receiveSocket = new DatagramSocket(receivePort);
			this.receivePort = receivePort;
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Initializes transport class (send & receive) onto any available port
	 * 
	 * @param role any of "Client", "Server" or "Host"; or other
	 */
	public Transport(String role) {
		this.role = role;
		try {
			address = InetAddress.getLocalHost();
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket();
			receivePort = -1;
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Send data to a destination on localhost using this transport's default send
	 * destination parameters
	 * 
	 * @param data
	 * @param destRole e.g. "Host"
	 */
	public void send(byte[] data) {
		send(data, destinationRole, destinationPort);
	}

	/**
	 * Send data to a destination on localhost using this transport's default send
	 * destinationPort
	 * 
	 * @param data
	 * @param destRole e.g. "Host"
	 */
	public void send(byte[] data, String destRole) {
		send(data, destRole, destinationPort);
	}

	/**
	 * Send data to a destination on localhost
	 * 
	 * @param data
	 * @param destRole e.g. "Host"
	 * @param destPort e.g. 23
	 */
	public void send(byte[] data, String destRole, int destPort) {
		// Prepare a DatagramPacket and send it via sendReceiveSocket
		// to port 5000 on the destination host.

		if (verbose) {
			System.out.println("\n--------------------------\n");

			System.out.println(role + ": sending a packet\n");
		}

		int len = data.length;

		DatagramPacket sendPacket = new DatagramPacket(data, len, address, destPort);
		
		System.out.println("WTF444:  " + len + " " + (Arrays.toString(sendPacket.getData())));

		if (verbose) {
			System.out.println("To " + destRole + ": " + sendPacket.getAddress());
			System.out.println("\tPort: " + sendPacket.getPort());
			System.out.println("Packet length:\t" + len);
			System.out.print("Packet contents (string): ");
			System.out.println(Arrays.toString(data)); // or could print "s"
		}

		// Send the datagram packet to the server via the send/receive socket.

		try {
//			System.out.println("null? " + sendSocket + " (" + (sendSocket == null) + ")");
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (verbose) {
			System.out.println("\n" + role + ": Packet sent.\n--------------------------\n");
		}
	}

	/**
	 * Waits for data on the Transport's receiveSocket
	 * 
	 * @return [byte[] data-received, int port-data-came-from]
	 */
	public Object[] receive() {
		// Construct a DatagramPacket for receiving packets up
		// to 100 bytes long (the length of the byte array).

		byte[] longData = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(longData, longData.length);

		try {
			// Block until a datagram is received via sendReceiveSocket.
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		int len = receivePacket.getLength();

		byte[] data = new byte[len];
		for (int i = 0; i < len; ++i)
			data[i] = longData[i];

		if (verbose) {
			System.out.println("\n--------------------------\n");

			// Process the received datagram.
			System.out.println(role + ": Packet received\n");
			System.out.println("From source: " + receivePacket.getAddress());
			System.out.println("source port: " + receivePacket.getPort());
			System.out.println("Packet length: " + len);
			System.out.print("Containing (string): ");

			// Form a String from the byte array.
			System.out.println(Arrays.toString(data));

			System.out.println("--------------------------\n");
		}
		return new Object[] { data, receivePacket.getPort() };
	}

	/**
	 * Closes sendSocket and receiveSocket wherever available
	 */
	public void close() {
		// We're finished, so close the socket.
		if (sendSocket != null) {
			sendSocket.close();
		}
		if (receiveSocket != null) {
			receiveSocket.close();
		}
	}

	public int getReceivePort() {
		return receivePort == -1 ? receiveSocket.getLocalPort() : receivePort;
	}

	public int getSendPort() {
		return sendSocket.getLocalPort();
	}

	public void setDestinationRole(String destinationRole) {
		this.destinationRole = destinationRole;
	}

	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	/**
	 * Creates a single-use Transport to send data to a port on localhost
	 * 
	 * @param data
	 * @param sourceRole e.g. "Client"
	 * @param destRole   e.g. "Host"
	 * @param destPort   e.g. 23
	 */
	public static void SEND(byte[] data, String sourceRole, String destRole, int destPort) {
		Transport transport = new Transport(sourceRole);
		transport.send(data, destRole, destPort);
		transport.close();
	}

	public static void setVerbose(boolean verbose) {
		Transport.verbose = verbose;
	}

}
