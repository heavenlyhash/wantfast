package us.exultant.wantfast.thruster.quasar;

import java.util.concurrent.*;
import us.exultant.wantfast.*;
import co.paralleluniverse.fibers.*;

public class Main {
	final static FiberScheduler scheduler = new FiberForkJoinScheduler("test", 4, null, false);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ThrusterConfig cfg = Wantfast.default_cfg;

		final QuasarServer server = new QuasarServer(scheduler);
		server.configure(cfg);
		final QuasarClient client = new QuasarClient(scheduler);
		client.configure(cfg);

		server.serve();
		Thread.sleep(100);
		client.ping();

		client.join();
		server.join();
	}

}
