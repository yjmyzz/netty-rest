package demo.soa.misc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.soa.misc.common.server.BaseNioServer;

public class ShutdownThread extends Thread {
	
	private BaseNioServer server;
	private static Logger log = LoggerFactory.getLogger(ShutdownThread.class);
	
	public ShutdownThread(BaseNioServer server){
		this.server = server;
	}
	
	@Override
	public void run() {
		try{
			server.stop();
			MyApplicationContext.getInstance().destroy();
		}catch(Exception e){
			log.info(e.getMessage(), e);
		}
	}
}
