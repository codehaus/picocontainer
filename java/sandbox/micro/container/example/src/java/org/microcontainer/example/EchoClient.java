package org.microcontainer.example;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Michael Ward
 */
public class EchoClient {
	private Socket socket;

	public EchoClient(int port) throws IOException {
		socket = new Socket("127.0.0.1", port);
	}

	public static void main(String[] args) throws Exception {
		EchoClient client = new EchoClient(8888);
		client.go();
	}

	public void go() throws IOException {
		while(true) {
			handleUserInput();
			handleServerResponse();
		}
	}

	protected void handleServerResponse() throws IOException {
		InputStream is = socket.getInputStream();

		while(true) {
			if(is.available() > 0) {
				StringBuffer buffer = new StringBuffer();
				while (is.available() > 0) {
					byte data[] = new byte[is.available()];
					is.read(data);
					buffer.append(new String(data));
				}

				System.out.println(buffer);
				break;
			}

			try {
				// no response... let's wait
				Thread.sleep(10);
			} catch (InterruptedException ignore) {
				// ignore
			}
		}
	}

	protected void handleUserInput() throws IOException {
		System.out.print("Send message: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String message = reader.readLine();

		// Send it...
		socket.getOutputStream().write(message.getBytes());
	}


}
