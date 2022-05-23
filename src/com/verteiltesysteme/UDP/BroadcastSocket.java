package com.verteiltesysteme.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class BroadcastSocket extends Thread {

	private final int bufferSize = 2048;

	private InetAddress ip;

	private DatagramSocket broadcastSocket;
	private DatagramPacket packetInBroad = new DatagramPacket(new byte[bufferSize], bufferSize);

	public BroadcastSocket(int port, InetAddress ip) throws SocketException {
		this.broadcastSocket = new DatagramSocket(port);	
		this.ip = ip;
	}

	@Override
	public void run() {
		while (true) {

			try {
				broadcastSocket.receive(packetInBroad);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				broadcastSocket.send(new DatagramPacket("".getBytes(), "".getBytes().length,
						packetInBroad.getAddress(), packetInBroad.getPort()));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}