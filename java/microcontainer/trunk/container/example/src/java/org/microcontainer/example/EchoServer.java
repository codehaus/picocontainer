package org.microcontainer.example;

import org.picocontainer.Startable;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * @author Michael Ward
 */
public class EchoServer implements Startable {

	private ServerSocket serverSocket = null;

	public EchoServer() throws IOException {
		serverSocket = new ServerSocket(8888);
		System.out.println("Starting Echo Server on port " + serverSocket.getLocalPort() + "...");
	}

	public void start() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						handleClientSocket(serverSocket.accept());
					} catch (IOException ignore) {
						break;
					}
				}
			}
		}.start();
	}

	public void handleClientSocket(final Socket socket) throws IOException {
		ClientSocketHandler clientSocketHandler = new ClientSocketHandler(socket);
		new Thread(clientSocketHandler).start();
	}

	public void stop() {
		try {
			System.out.println("Stopping Echo Server...");
			serverSocket.close();
		} catch (IOException e) {
			// ignore
		}
	}

}
