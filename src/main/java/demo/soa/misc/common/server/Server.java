package demo.soa.misc.common.server;

public interface Server {
	
	public void init();

	public void start();

	public void stop();

	public String serverName();
}
