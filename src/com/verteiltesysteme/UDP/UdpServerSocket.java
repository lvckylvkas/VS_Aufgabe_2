package com.verteiltesysteme.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

class UdpServerSocket extends Thread {

	// private static final int bufferSize = 512;
	// Variablen deklarieren
	private static final int maxBufSize = 2048;
	private DatagramSocket socket;
	private DatagramPacket packetIn = new DatagramPacket(new byte[maxBufSize], maxBufSize);
	private DatagramPacket packetOut = new DatagramPacket(new byte[maxBufSize], maxBufSize);
	private static final String alternativ = "Die gesendete Nachricht war zu lang!";
	private byte[] buf = alternativ.getBytes();

	public UdpServerSocket(int port) throws SocketException {
		this.socket = new DatagramSocket(port);
	}

	@Override
	public void run() {
		// Server wartet auf Nachrichten
		while (true) {
			try {

				socket.receive(packetIn);
				if (packetIn.getLength() >= maxBufSize) {
					packetOut.setData(buf);
					packetOut.setLength(buf.length);
					// Setzen der Empfaenger Adresse
					packetOut.setSocketAddress(packetIn.getSocketAddress());
					socket.send(packetOut);
				} else {
					// Wenn ein Paket empfangen wurde wird der Port und die IP-Adresse vom Sender
					// ausgelesen
					System.out.println("Port des Senders:" + packetIn.getPort());
					System.out.println("IP-Adresse des Senders:" + packetIn.getAddress());
					// Hier wird die empfangene Nachricht ausgegeben
					System.out.println("Empfangene Nachricht: " + packetIn.getLength() + " bytes: "
							+ new String(packetIn.getData(), 0, packetIn.getLength()));

					packetOut.setData(packetIn.getData());
					packetOut.setLength(packetIn.getLength());
					// Setzen der Empfaenger Adresse
					packetOut.setSocketAddress(packetIn.getSocketAddress());
					socket.send(packetOut);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}