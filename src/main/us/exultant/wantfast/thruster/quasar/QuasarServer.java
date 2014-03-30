package us.exultant.wantfast.thruster.quasar;

import java.io.*;
import java.net.*;
import java.nio.*;
import co.paralleluniverse.fibers.*;
import co.paralleluniverse.fibers.io.*;
import co.paralleluniverse.strands.*;

public class QuasarServer {
	// see FibersMXBean and/or implmenting a FibersMonitor for inspection of schedulers.
	// see FiberExecutorScheduler for using existing/legacy executor pools instead of new FJPools.

	// https://github.com/puniverse/quasar/blob/master/quasar-core/src/test/java/co/paralleluniverse/fibers/io/FiberAsyncIOTest.java
	// https://github.com/puniverse/quasar/blob/master/quasar-core/src/test/java/co/paralleluniverse/strands/StrandsBenchmark.java

	FiberScheduler scheduler = new FiberForkJoinScheduler("test", 4, null, false);

	private static final int PORT = 1234;

	public static void main(String... args) throws Exception {
		new QuasarServer().testFiberAsyncSocket();
	}

	public void testFiberAsyncSocket() throws Exception {
		final Fiber<Void> server = new Fiber<Void>(scheduler, new SuspendableRunnable() {
			@Override
			public void run() throws SuspendExecution {
				try (
					FiberServerSocketChannel socket = FiberServerSocketChannel.open().bind(new InetSocketAddress(PORT));
					FiberSocketChannel ch = socket.accept()
				) {
					ByteBuffer headerBuf = ByteBuffer.allocateDirect(4);
					ByteBuffer msgBuf = ByteBuffer.allocateDirect(1024 * 1024);

					headerBuf.clear();
					int n = ch.read(headerBuf);
					if (n != 4) {
						throw new Error("damnit, quasar, what did i hire you for (server reading header)");
					}
					headerBuf.flip();
					int msgLen = headerBuf.getInt();

					msgBuf.clear();
					msgBuf.limit(msgLen);
					long n2 = ch.read(msgBuf);
					if (n2 != msgLen) {
						throw new Error("damnit, quasar, what did i hire you for (server reading body)");
					}
					msgBuf.flip();

					headerBuf.clear();
					headerBuf.putInt(msgLen);
					headerBuf.flip();
					n = ch.write(headerBuf);
					if (n != 4) {
						throw new Error("damnit, quasar, what did i hire you for (server writing header)");
					}
					msgBuf.limit(2);
					ch.write(msgBuf);

					Strand.sleep(500);
					msgBuf.limit(4);
					ch.write(msgBuf);
//					if (n != msgLen) {
//						throw new Error("damnit, quasar, what did i hire you for (server writing body)");
//					}
				} catch (IOException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		final QuasarClient client = new QuasarClient(scheduler);

		server.start();
		Thread.sleep(100);
		client.ping();

		client.join();
		server.join();
	}
}
