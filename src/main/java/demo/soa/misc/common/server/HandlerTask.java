package demo.soa.misc.common.server;

import java.io.Serializable;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerTask implements Runnable, Serializable{
	private static final long serialVersionUID = 808668539395369496L;
	private ChannelHandlerContext ctx = null;
	private ChannelEvent event = null; 
	private static Logger log = LoggerFactory.getLogger(HandlerTask.class);
	private RestProcesser procosser = null; 
	
	public HandlerTask(RestProcesser procosser, ChannelHandlerContext context, ChannelEvent e){
		this.ctx   	   = context;
		this.event     = e;
		this.procosser = procosser;
	}
	
	@Override
	public void run() {
		try{
			if (event instanceof MessageEvent) {
				procosser.messageReceived(ctx, (MessageEvent)event);
	        } else {
	        	ctx.sendUpstream(event);
	        }
		}catch(Exception e){
			log.error("HandlerTask Error:", e);
		}
	}
}
