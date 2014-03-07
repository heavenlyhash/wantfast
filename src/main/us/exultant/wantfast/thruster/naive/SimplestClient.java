package us.exultant.wantfast.thruster.naive;

import java.io.*;
import java.net.*;

public class SimplestClient {

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
		System.out.println("(wrote ping to connection on client)");
		return in.readLine();
	}
}
