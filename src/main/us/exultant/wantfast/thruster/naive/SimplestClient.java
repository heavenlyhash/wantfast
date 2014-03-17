package us.exultant.wantfast.thruster.naive;

import java.io.*;
import java.net.*;
import org.apache.logging.log4j.*;
import us.exultant.wantfast.*;

public class SimplestClient implements us.exultant.wantfast.thruster.api.Client {
	public static final Logger log = LogManager.getLogger(SimplestClient.class);

	@Override
	public void configure(ThrusterConfig cfg) {
		port = cfg.get(ThrusterConfig.Options.PORT);
	}

	private int port;

	Socket sock;
	PrintWriter out;
	BufferedReader in;

	@Override
	public void connect() throws IOException {
		sock = new Socket("localhost", port);
		out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(sock.getOutputStream())), true);
		in = new BufferedReader(new InputStreamReader(new BufferedInputStream(sock.getInputStream())));
	}

	public String ping() throws IOException {
		out.println("{\"ping\":\"ping\"}");
		log.info("wrote ping to connection on client");
		return in.readLine();
	}
}
