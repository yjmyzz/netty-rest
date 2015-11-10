package demo.soa.misc.common.server;

import java.io.IOException;
import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestChannelHandler extends ExecutionHandler {
	private static Logger log = LoggerFactory.getLogger(RestChannelHandler.class);
	private final RestProcesser restProcosser = new RestProcesser();
	
	public RestChannelHandler(Executor executor) {
		super(executor);
	}

	public RestProcesser getRestProcosser() {
		return restProcosser;
	}
	
	@Override
	public void handleUpstream(ChannelHandlerContext context, ChannelEvent e) throws Exception {
		if (e instanceof ExceptionEvent) {
            exceptionCaught(context, (ExceptionEvent) e);
        }else{
        	getExecutor().execute(new HandlerTask(restProcosser, context, e));
        }
    }

//	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable exception = e.getCause();
		if(exception != null && !(exception instanceof IOException)){
			if (log.isTraceEnabled())
				log.trace("Connection exceptionCaught:{}", e.getCause().toString());
			log.error("Connection exceptionCaught:{}", e.getCause().toString());
		}
		e.getChannel().close();
	}
}
