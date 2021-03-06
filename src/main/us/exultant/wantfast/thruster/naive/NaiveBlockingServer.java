package us.exultant.wantfast.thruster.naive;

import java.io.*;
import java.net.*;
import org.apache.logging.log4j.*;
import us.exultant.wantfast.*;

public class NaiveBlockingServer implements us.exultant.wantfast.thruster.api.Server {
	public static final Logger log = LogManager.getLogger(NaiveBlockingServer.class);

	@Override
	public void configure(ThrusterConfig cfg) {
		port = cfg.get(ThrusterConfig.Options.PORT);
	}

	private int port;

	ServerSocket sock;

	@Override
	public void connect() throws IOException {
		sock = new ServerSocket(port);
	}


	public NaiveBlockingServer serve() {
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
					log.info("connection accepted on server, preparing to read");
					while ((msg = in.readLine()) != null) {
						log.info("read message from connection on server");
						out.println(msg);
						log.info("wrote echo to connection on server");
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
