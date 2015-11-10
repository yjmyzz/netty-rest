package demo.soa.misc.common.exception;


/**
 *  知道明确的错误原因的异常
 */
public class FrameworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public int errorCode = 502;
	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FrameworkException(String message,int errcode) {
		super(message);
		this.errorCode=errcode;
	}

	public FrameworkException(String message, Throwable cause,int errcode) {
		super(message, cause);
	    this.errorCode=errcode;
	}
}
