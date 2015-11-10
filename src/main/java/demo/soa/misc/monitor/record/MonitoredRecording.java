package demo.soa.misc.monitor.record;

import java.util.concurrent.atomic.AtomicLong;


public class MonitoredRecording {

	private  long costTime = 0;
	private  long requestDelta = 0;
	private  long errorDelta = 0;
	private  AtomicLong requestCount = new AtomicLong();
	private  AtomicLong errorCount = new AtomicLong();
	private  long lastRequestCount = 0;
	private  long lastErrorCount = 0;
	public  synchronized void doTransaction(){
		requestDelta = requestCount.get() - lastRequestCount;
		errorDelta = errorCount.get() - lastErrorCount;
		lastRequestCount = requestCount.get();
		lastErrorCount = errorCount.get();
	}
	
	public  synchronized void finishTransaction() {
		costTime = 0;
	}
	
	public  long getRequestDelta() {
		return requestDelta;
	}

	public  long getErrorDelta() {
		return errorDelta;
	}
	
	public  long getCostTime() {
		return costTime;
	}

	public  synchronized void setCostTime(long costTime) {
		this.costTime = costTime>this.costTime?costTime:this.costTime;
	}

	public  void increaseRequestCount(){
		requestCount.incrementAndGet();
	}
	
	public  void increaseErrorCount(){
		errorCount.incrementAndGet();
	}

	
	
}

