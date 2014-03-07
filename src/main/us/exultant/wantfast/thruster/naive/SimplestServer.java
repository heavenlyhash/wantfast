package us.exultant.wantfast.thruster.naive;

import java.io.*;
import java.net.*;

public class SimplestServer {

	ServerSocket sock;

	public SimplestServer connect() throws IOException {
		sock = new ServerSocket(10010);
		return this;
	}


	public SimplestServer serve() {
		Thread t = new Thread() {
			public void run() {
				while (true) {
					try {
						serveOne(sock.accept());
					} catch (IOException e) {
						throw new RuntimeException("Dead, jim: server's dead.", e);
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
		return this;
	}

	private static void serveOne(final Socket sock) throws IOException {
		Thread t = new Thread() {
			public void run() {
				try {
					final PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(sock.getOutputStream())), true);
					final BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(sock.getInputStream())));
					String msg;
					System.out.println("(connection accepted on server, preparing to read)");
					while ((msg = in.readLine()) != null) {
						System.out.println("(read message from connection on server)");
						out.println(msg);
						System.out.println("(wrote echo to connection on server)");
					}
				} catch (IOException e) {
					throw new RuntimeException("Dead, jim: a connection on is dead on the server.", e);
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
}
