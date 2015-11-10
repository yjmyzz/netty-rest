package demo.soa.misc.common.exception;

public class SCException extends RuntimeException{
	
	private static final long serialVersionUID = -8841821697381037598L;

	public SCException(){}
	
	public SCException(String message){
		super(message);
	}
	
	public SCException(String message, Throwable cause){
		super(message, cause);
	}
	
	@Override
	public String toString(){
		return getMessage();
	}
}
