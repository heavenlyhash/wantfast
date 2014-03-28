package us.exultant.wantfast.thruster.quasar;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;
import co.paralleluniverse.fibers.*;
import co.paralleluniverse.fibers.io.*;
import co.paralleluniverse.strands.*;

public class QuasarServer {
	// see FibersMXBean and/or implmenting a FibersMonitor for inspection of schedulers.
	// see FiberExecutorScheduler for using existing/legacy executor pools instead of new FJPools.

	// https://github.com/puniverse/quasar/blob/master/quasar-core/src/test/java/co/paralleluniverse/fibers/io/FiberAsyncIOTest.java
	// https://github.com/puniverse/quasar/blob/master/quasar-core/src/test/java/co/paralleluniverse/strands/StrandsBenchmark.java

	FiberServerSocketChannel sock;
	FiberScheduler scheduler = new FiberForkJoinScheduler("test", 4, null, false);

	private static final int PORT = 1234;
	private static final Charset charset = Charset.forName("UTF-8");
	private static final CharsetEncoder encoder = charset.newEncoder();
	private static final CharsetDecoder decoder = charset.newDecoder();

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

		final Fiber<Void> client = new Fiber<Void>(scheduler, new SuspendableRunnable() {
			@Override
			public void run() throws SuspendExecution {
				try (FiberSocketChannel ch = FiberSocketChannel.open(new InetSocketAddress(PORT))) {
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
		});

		server.start();
		Thread.sleep(100);
		client.start();

		client.join();
		server.join();
	}
}
