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

	private static boolean verbose = true;

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
	 * @return 
	 */
	public Object[] send(byte[] data) {
		try {
			return send(data, destinationRole, destinationPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Send data to a destination on localhost using this transport's default send
	 * destinationPort
	 * 
	 * @param data
	 * @param destRole e.g. "Host"
	 */
	public void send(byte[] data, String destRole) {
		try {
			send(data, destRole, destinationPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send data to a destination on localhost
	 * 
	 * @param data
	 * @param destRole e.g. "Host"
	 * @param destPort e.g. 23
	 * @throws Exception 
	 */
	public Object[] send(byte[] data, String destRole, int destPort) throws Exception {
		// Prepare a DatagramPacket and send it via sendReceiveSocket
		// to port 5000 on the destination host.

		if (verbose) {
			Printer.print("\n--------------------------\n");

			Printer.print(role + ": sending a packet\n");
		}

		int len = data.length;

		DatagramPacket sendPacket = new DatagramPacket(data, len, address, destPort);

		if (verbose) {
			Printer.print("To " + destRole + ": " + sendPacket.getAddress());
			Printer.print("\tPort: " + sendPacket.getPort());
			Printer.print("Packet length:\t" + len);
			System.out.print("Packet contents (string): ");
			Printer.print(Arrays.toString(data)); // or could print "s"
		}

		// Send the datagram packet to the server via the send/receive socket.

		try {
//			Printer.print("null? " + sendSocket + " (" + (sendSocket == null) + ")");
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception();
			//System.exit(1);
		}

		if (verbose) {
			Printer.print("\n" + role + ": Packet sent.\n--------------------------\n");
		}
		return new Object[] { data, sendPacket.getPort() };
	}

	/**
	 * Waits for data on the Transport's receiveSocket
	 * 
	 * @return [byte[] data-received, int port-data-came-from]
	 * @throws Exception 
	 */
	public Object[] receive() throws Exception {
		// Construct a DatagramPacket for receiving packets up
		// to 100 bytes long (the length of the byte array).

		byte[] longData = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(longData, longData.length);

		try {
			// Block until a datagram is received via sendReceiveSocket.
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception();
		//	System.exit(1);
		}

		int len = receivePacket.getLength();

		byte[] data = new byte[len];
		for (int i = 0; i < len; ++i)
			data[i] = longData[i];

		if (verbose) {
			Printer.print("\n--------------------------\n");

			// Process the received datagram.
			Printer.print(role + ": Packet received\n");
			Printer.print("From source: " + receivePacket.getAddress());
			Printer.print("source port: " + receivePacket.getPort());
			Printer.print("Packet length: " + len);
			System.out.print("Containing (string): ");

			// Form a String from the byte array.
			Printer.print(Arrays.toString(data));

			Printer.print("--------------------------\n");
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
		return receiveSocket.getLocalPort();
	}

	public int getSendPort() {
		return sendSocket.getLocalPort();
	}
	
	public int getDestinationPort() {
		return destinationPort;
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
		try {
			transport.send(data, destRole, destPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transport.close();
	}

	public static void setVerbose(boolean verbose) {
		Transport.verbose = verbose;
	}

}
