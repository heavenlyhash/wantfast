package us.exultant.wantfast.thruster.quasar;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import co.paralleluniverse.fibers.io.*;

/**
 * Proxies a {@link FiberSocketChannel} to behave with all the same interfaces, except all
 * read and write methods block the fiber until the entire requested volume of bytes has
 * been moved (which in many cases is technically a break of contract with the interfaces
 * implemented).
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class PatientFiberSocketChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, NetworkChannel {
	public PatientFiberSocketChannel(FiberSocketChannel proxy) {
		this.proxy = proxy;
	}

	private FiberSocketChannel proxy;

	public int read(ByteBuffer dst) throws IOException {
		int n = 0;
		while (dst.remaining() > 0) {
			int n2 = proxy.read(dst);
			if (n2 == -1) {
				return (n == 0) ? -1 : n;
			}
			n += n2;
		}
		return n;
	}

	public int write(ByteBuffer src) throws IOException {
		int n = 0;
		System.out.println("seriously not a null 1: "+src);
		System.out.println("seriously not a null 2: "+proxy);
		while (src.remaining() > 0) {
			n += proxy.write(src);
		}
		return n;
	}

	public boolean isOpen() {
		return proxy.isOpen();
	}

	public void close() throws IOException {
		proxy.close();
	}

	public NetworkChannel bind(SocketAddress local) throws IOException {
		return proxy.bind(local);
	}

	public SocketAddress getLocalAddress() throws IOException {
		return proxy.getLocalAddress();
	}

	public <T> NetworkChannel setOption(SocketOption<T> name, T value) throws IOException {
		return proxy.setOption(name, value);
	}

	public <T> T getOption(SocketOption<T> name) throws IOException {
		return proxy.getOption(name);
	}

	public Set<SocketOption<?>> supportedOptions() {
		return proxy.supportedOptions();
	}

	public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
		long n = 0;
		ByteBuffer last = srcs[srcs.length-1];
		while (last.remaining() > 0) {
			proxy.write(srcs, offset, length);
		}
		return n;
	}

	public long write(ByteBuffer[] srcs) throws IOException {
		long n = 0;
		ByteBuffer last = srcs[srcs.length-1];
		while (last.remaining() > 0) {
			proxy.write(srcs);
		}
		return n;
	}

	public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
		int n = 0;
		ByteBuffer last = dsts[dsts.length-1];
		while (last.remaining() > 0) {
			long n2 = proxy.read(dsts, offset, length);
			if (n2 == -1) {
				return (n == 0) ? -1 : n;
			}
			n += n2;
		}
		return n;
	}

	public long read(ByteBuffer[] dsts) throws IOException {
		int n = 0;
		ByteBuffer last = dsts[dsts.length-1];
		while (last.remaining() > 0) {
			long n2 = proxy.read(dsts);
			if (n2 == -1) {
				return (n == 0) ? -1 : n;
			}
			n += n2;
		}
		return n;
	}

}
