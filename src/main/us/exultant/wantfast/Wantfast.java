package us.exultant.wantfast;

import java.io.*;
import org.apache.logging.log4j.*;
import us.exultant.wantfast.thruster.naive.*;

public class Wantfast {
	public static final Logger log = LogManager.getLogger(Wantfast.class);

	public static void main(String... args) throws IOException {
		log.info("Hello, world.");
		new SimplestServer().connect().serve();
		log.info(new SimplestClient().connect().ping());
	}
}
