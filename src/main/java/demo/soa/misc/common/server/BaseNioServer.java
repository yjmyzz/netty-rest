package demo.soa.misc.common.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseNioServer implements Server {
	private final ChannelGroup allChannels = new DefaultChannelGroup("nio-server");
	protected Logger log;
	protected ServerBootstrap bootstrap;
	protected ChannelFactory channelFactory = null;
	private static final int defaultPort = 10005;

	public BaseNioServer() {
	}
	@Override
	public void init() {
		log = LoggerFactory.getLogger(this.serverName());
	}
	@Override
	public void start() {
		try{
			this.channelFactory = this.createChannelFactory();
			bootstrap = new ServerBootstrap(channelFactory);
			bootstrap.setPipelineFactory(getChannelPipelineFactory());
			SocketAddress sa = this.getSocketAddress();
			Channel serverChannel = bootstrap.bind(sa);
			allChannels.add(serverChannel);
			log.info("server started at/" + sa + " Running Success!");
		}catch(Exception e){
			log.error("Bind error:", e);
			System.exit(5);
		}
	}
	@Override
	public void stop() {
		log.info("shutting down netty unbind...");
		this.allChannels.unbind().awaitUninterruptibly(1000);
		try{
			Thread.sleep(3000);
		}catch(Exception e){
		}
		log.info("shutting down netty close...");
		this.allChannels.close().awaitUninterruptibly(1000);
//		log.info("shutting down netty disconnect...");
//		this.allChannels.disconnect().awaitUninterruptibly(2000);
		if (channelFactory != null){
			log.info("shutting down netty releaseExternalResources...");
			channelFactory.releaseExternalResources();
		}
	}
	
	protected int defaultPort(){
		return defaultPort;
	}
	
	protected ServerAddress getServerAddress() {
		String myip = "0.0.0.0";
		int myport = defaultPort();
		try {
			myip = NetworkUtils.getFirstNonLoopbackAddress(NetworkUtils.StackType.IPv4).getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return new ServerAddress(myip, myport);
	}
	
	protected final SocketAddress getSocketAddress() {
		ServerAddress sa = this.getServerAddress();
		if(sa.getHost() == null){
			return new InetSocketAddress(sa.getPort());
		}
		return new InetSocketAddress(sa.getHost(), sa.getPort());
	}

	protected ChannelFactory createChannelFactory() {
		return new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
	}
	
	protected ChannelPipelineFactory getChannelPipelineFactory() {
		return new ChannelPipelineFactory() {
			@SuppressWarnings("deprecation")
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ObjectEncoder(),new ObjectDecoder(), finalChannelUpstreamHandler());
			}
		};
	}

	protected abstract ChannelUpstreamHandler finalChannelUpstreamHandler();
	
}

