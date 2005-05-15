package org.microcontainer.example;

import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Michael Ward
 */
public class ClientSocketHandler implements Runnable {
	private Socket socket;

	public ClientSocketHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		StringBuffer buffer = null;
		try {
			InputStream is = socket.getInputStream();

			while (true) {
				if (is.available() > 0) {
					buffer = new StringBuffer();

					while (is.available() > 0) {
						byte data[] = new byte[is.available()];
						is.read(data);
						buffer.append(new String(data));
					}

					System.out.println("Received: " + buffer.toString());

					String echo = "ECHO: " + buffer;
					socket.getOutputStream().write(echo.getBytes());
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
