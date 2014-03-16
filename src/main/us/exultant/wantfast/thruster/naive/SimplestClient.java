package us.exultant.wantfast.thruster.naive;

import java.io.*;
import java.net.*;
import org.apache.logging.log4j.*;

public class SimplestClient {
	public static final Logger log = LogManager.getLogger(SimplestClient.class);

	Socket sock;
	PrintWriter out;
	BufferedReader in;

	public SimplestClient connect() throws IOException {
		sock = new Socket("localhost", 10010);
		out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(sock.getOutputStream())), true);
		in = new BufferedReader(new InputStreamReader(new BufferedInputStream(sock.getInputStream())));
		return this;
	}

	public String ping() throws IOException {
		out.println("{\"ping\":\"ping\"}");
		log.info("wrote ping to connection on client");
		return in.readLine();
	}
}
