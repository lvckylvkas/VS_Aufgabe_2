package com.verteiltesysteme.TCP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {
	public static void main(String[] args) {
		String host = "0.0.0.0";
		int port = 1024;

		try (Socket socket = new Socket(host, port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {

			// Begrüßung vom Server empfangen und auf Konsole ausgeben
			String msg = in.readLine();
			System.out.println(msg);

			// Zeile von Konsole einlesen, an Server senden und Antwort von
			// Server auf Konsole ausgeben, bis eingegebene Zeile == "q"
			while (true) {
				System.out.print(">> ");
				String line = stdin.readLine();
				if ("q".equals(line)) {
					break;
				}
				out.println(line);
				System.out.println(in.readLine());
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
