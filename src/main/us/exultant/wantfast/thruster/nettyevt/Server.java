package us.exultant.wantfast.thruster.nettyevt;

import java.io.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import us.exultant.wantfast.*;

public class Server implements us.exultant.wantfast.thruster.api.Server {
	@Override
	public void configure(ThrusterConfig cfg) {
		port = cfg.get(ThrusterConfig.Options.PORT);
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup(cfg.get(ThrusterConfig.Options.IO_WORKER_POOL));
	}

	private int port;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@Override
	public void connect() throws IOException {
		// skipped until interface can disambiguate worker startups from connection.
	}

	public void run() throws Exception {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();

						// codec
						pipeline.addLast("deframer", new Codec.Deframer());
						pipeline.addLast("encoder", new Codec.Enframer());

						// handoff to business logic
						pipeline.addLast("handler", new Dispatch());
					}
				});

			b.bind(port).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
