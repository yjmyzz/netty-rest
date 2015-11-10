package demo.soa.misc.handler;

import java.util.Map;

public class Router implements IRouter {
	private Map<String, String> mapRouter;

	@Override
	public Map<String, String> getMapRouter() {
		return mapRouter;
	}

	public void setMapRouter(Map<String, String> mapRouter) {
		this.mapRouter = mapRouter;
	}
}
