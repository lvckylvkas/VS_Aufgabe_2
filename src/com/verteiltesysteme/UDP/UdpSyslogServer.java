package com.verteiltesysteme.UDP;

import java.io.IOException;
import java.net.InetAddress;

class UdpSyslogServer {

	private static final int portNumber = 514;
	private static final int portNumberBroadcast = 8888;
	// private static final int bufferSize = 512;

	public static void main(final String[] args) {

		try {

			InetAddress ip = InetAddress.getLocalHost();
			// Sockets einrichten
			UdpServerSocket serverSocket = new UdpServerSocket(portNumber);
			BroadcastSocket broadcastSocket = new BroadcastSocket(portNumberBroadcast, ip);

			// Threads Starten
			serverSocket.start();
			broadcastSocket.start();

			System.out.println("Server wurde gestartet");

			// Threads synchronisieren
			serverSocket.join();
			broadcastSocket.join();

		} catch (IOException e) {
			System.err.println(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
