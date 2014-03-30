package us.exultant.wantfast;

import java.io.*;
import org.apache.logging.log4j.*;
import us.exultant.wantfast.thruster.api.*;
import us.exultant.wantfast.thruster.naive.*;

public class Wantfast {
	public static final Logger log = LogManager.getLogger(Wantfast.class);

	public static final ThrusterConfig default_cfg = new ThrusterConfig();
	static {
		default_cfg.put(ThrusterConfig.Options.PORT, 10010);
	}

	public static void main(String... args) throws Exception {
		log.info("Hello, world.");

		us.exultant.wantfast.thruster.quasar.Main.main(args);

//		Server server = new NaiveBlockingServer();
//		server.configure(cfg);
//		server.connect();
//		((NaiveBlockingServer)server).serve();
//
//		Client client = new NaiveBlockingClient();
//		client.configure(cfg);
//		client.connect();
//		log.info(((NaiveBlockingClient)client).ping());
	}
}
