package demo.soa.misc.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ExecutorFactory {

	public static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	public static ExecutorService fixedExecutor = Executors.newFixedThreadPool(10);
	public static ExecutorService cachedExecutor = Executors.newCachedThreadPool();
	
}
