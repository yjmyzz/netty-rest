package demo.soa.misc.common.util;

public class CostTime {

	private transient long start;
	
	public void start(){
		this.start = System.currentTimeMillis();
	}
	
	public long cost(){
		return System.currentTimeMillis() - start;
	}
}
