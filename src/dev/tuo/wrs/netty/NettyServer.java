package dev.tuo.wrs.netty;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class NettyServer implements Runnable {
	ChannelGroup mChannelGroup;
	ServerBootstrap mServerBootstrap;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		mChannelGroup = new DefaultChannelGroup();
		Channel mChannel;
		InetSocketAddress local = new InetSocketAddress(8848);
		NioServerSocketChannelFactory factory = null;
		ExecutorService threadPool = Executors.newCachedThreadPool();
		factory = new NioServerSocketChannelFactory(threadPool, threadPool);
		mServerBootstrap = new ServerBootstrap(factory);
		ChannelPipeline pipeline = mServerBootstrap.getPipeline();
		MyChannelHandler channelHandler = new MyChannelHandler();
		pipeline.addLast("encode", new StringEncoder());
		pipeline.addLast("decode", new StringDecoder());
		pipeline.addLast("servercnfactory", channelHandler);
		mChannel = mServerBootstrap.bind(local);
		mChannelGroup.add(mChannel);

	}

	public void shutdown() {
		mChannelGroup.close().awaitUninterruptibly();
		mServerBootstrap.releaseExternalResources();
	}

	private String getCurrentDatetime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String now = sdf.format(System.currentTimeMillis());
		return now;
	}

	class MyChannelHandler extends SimpleChannelHandler {

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			String request = (String) e.getMessage();
			String response = "";
			String from = e.getChannel().getRemoteAddress().toString();
			String info = from + " request: " + request + " at " + getCurrentDatetime();
			if ("ping".equals(request.trim())) {
				response = "success";
				e.getChannel().write("success");
			}
			if ("play".equals(request.trim())) {
				String cmd = "C:\\Program Files\\Windows Media Player\\wmplayer.exe";
				String url = "http://www.w3schools.com/html/movie.mp4";
				Runtime.getRuntime().exec(cmd + " " + url + " "+"/fullscreen");

			}
			System.out.println(info);

			System.err.println("response: is" + response);
			// e.getChannel().close();
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			String clientIP = e.getChannel().getRemoteAddress().toString();
			System.out.println(clientIP + " at " + getCurrentDatetime() + " connected");
			super.channelConnected(ctx, e);
		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			String clientIP = e.getChannel().getRemoteAddress().toString();
			System.out.println(clientIP + " at " + getCurrentDatetime() + " closed");
			super.channelClosed(ctx, e);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			// TODO Auto-generated method stub

			String clientIP = e.getChannel().getRemoteAddress().toString();
			System.out.println(clientIP + " at " + getCurrentDatetime() + " exceptionCaught");
			// exceptionTableModel.addRow(new String[] { getCurrentDatetime(), e.getCause().getLocalizedMessage(),
			// clientIP });
			e.getChannel().write("sorry,exception...");
			e.getChannel().close();
			super.exceptionCaught(ctx, e);
		}

		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			// TODO Auto-generated method stub
			mChannelGroup.add(e.getChannel());
			super.channelOpen(ctx, e);
		}

	}

}
