package demo.soa.misc.common.server;

import demo.soa.misc.common.server.resp.Resp;


public interface Handler {

	 public Resp handleRequest(NettyHttpRequest request);
	 
}
