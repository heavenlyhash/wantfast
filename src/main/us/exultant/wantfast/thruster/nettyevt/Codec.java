package us.exultant.wantfast.thruster.nettyevt;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;
import java.util.*;

public abstract class Codec {
	public static class Enframer extends MessageToByteEncoder<byte[]> {
		@Override
		protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
			// Write a message.
			// NOTE: this is arguably cheating.  netty is keeping an arbitrarily large buffer here; doing this with streaming will be much, much, much more ugly code.
			out.writeByte((byte) 'F'); // magic number
			out.writeInt(msg.length);  // data length
			out.writeBytes(msg);       // data
		}
	}



	public static class Deframer extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
			// Wait until the length prefix is available.
			if (in.readableBytes() < 5) { return; }

			in.markReaderIndex();

			// Check the magic number.
			int magicNumber = in.readUnsignedByte();
			if (magicNumber != 'F') {
				in.resetReaderIndex();
				throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
			}

			// Wait until the whole data is available.
			// NOTE: this is arguably cheating.  doing this with streaming will be much, much, much more ugly code.
			int dataLength = in.readInt();
			if (in.readableBytes() < dataLength) {
				in.resetReaderIndex();
				return;
			}

			// Receive the whole chunk.
			byte[] msg = new byte[dataLength];
			in.readBytes(msg);
			out.add(msg);
		}
	}
}
