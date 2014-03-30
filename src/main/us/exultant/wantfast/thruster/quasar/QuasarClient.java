package us.exultant.wantfast.thruster.quasar;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.logging.log4j.*;
import us.exultant.wantfast.*;
import co.paralleluniverse.fibers.*;
import co.paralleluniverse.fibers.io.*;
import co.paralleluniverse.strands.*;

public class QuasarClient implements us.exultant.wantfast.thruster.api.Client {
	public static final Logger log = LogManager.getLogger(QuasarClient.class);

	public QuasarClient(FiberScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void configure(ThrusterConfig cfg) {
		port = cfg.get(ThrusterConfig.Options.PORT);
	}

	private final FiberScheduler scheduler;
	private int port;

	private Fiber<Void> fiber;

	public void connect() throws IOException {
		// yeahhhhh... so actually let's fix the Client interface, because try-with-resources Is Better
	}

	public void ping() {
		fiber = new Fiber<Void>(scheduler, new SuspendableRunnable() {
			@Override
			public void run() throws SuspendExecution {
				try (FiberSocketChannel ch = FiberSocketChannel.open(new InetSocketAddress(port))) {
					ByteBuffer headerBuf = ByteBuffer.allocateDirect(4);
					ByteBuffer msgBuf = ByteBuffer.allocateDirect(1024 * 1024);

					byte[] msg = new byte[] { 0x63, 0x63, 0x63, 0x64, };
					int msgLen = msg.length;

					headerBuf.clear();
					headerBuf.putInt(msgLen);
					headerBuf.flip();
					int n = ch.write(headerBuf);
					if (n != 4) {
						throw new Error("damnit, quasar, what did i hire you for (client writing header)");
					}
					msgBuf.clear();
					msgBuf.put(msg);
					msgBuf.flip();
					n = ch.write(msgBuf);
					if (n != msgLen) {
						throw new Error("damnit, quasar, what did i hire you for (client writing body)");
					}


					headerBuf.clear();
					n = ch.read(headerBuf);
					if (n != 4) {
						throw new Error("damnit, quasar, what did i hire you for (client reading header)");
					}
					headerBuf.flip();
					msgLen = headerBuf.getInt();

					msgBuf.clear();
					msgBuf.limit(msgLen);
					long n2 = ch.read(msgBuf);
					if (n2 != msgLen) {
						throw new Error("damnit, quasar, what did i hire you for (client reading body)");
					}
					msgBuf.flip();

					if (msgLen != msg.length)
						throw new Error("damnit, quasar: expected echo msglen "+msg.length+", got "+msgLen);
					byte[] msg2 = new byte[msgLen];
					msgBuf.get(msg2);
					if (!Arrays.equals(msg, msg2))
						throw new Error("damnit, quasar: expected echo message equality, wanted "+Arrays.toString(msg)+", got "+Arrays.toString(msg2));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	public void join() throws ExecutionException, InterruptedException {
		fiber.join();
	}
}
