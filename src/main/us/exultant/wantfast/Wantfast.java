package us.exultant.wantfast;

import java.io.*;
import org.apache.logging.log4j.*;
import us.exultant.wantfast.thruster.api.*;
import us.exultant.wantfast.thruster.naive.*;

public class Wantfast {
	public static final Logger log = LogManager.getLogger(Wantfast.class);

	public static void main(String... args) throws IOException {
		log.info("Hello, world.");

		ThrusterConfig cfg = new ThrusterConfig();
		cfg.put(ThrusterConfig.Options.PORT, 10010);

		Server server = new SimplestServer();
		server.configure(cfg);
		server.connect();
		((SimplestServer)server).serve();

		Client client = new SimplestClient();
		client.configure(cfg);
		client.connect();
		log.info(((SimplestClient)client).ping());
	}
}
