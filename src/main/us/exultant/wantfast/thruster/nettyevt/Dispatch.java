package us.exultant.wantfast.thruster.nettyevt;

import io.netty.channel.*;
import org.apache.logging.log4j.*;

public class Dispatch extends SimpleChannelInboundHandler<byte[]> {
	private static final Logger log = LogManager.getLogger(Dispatch.class);

	// you can keep state here

	@Override
	public void messageReceived(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		ctx.writeAndFlush(new byte[] {'{'});
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("Channel inactive");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.warn("Unexpected exception from downstream.", cause);
		//TODO: collect counts of the ways in which we eat shit
		ctx.close();
	}
}
