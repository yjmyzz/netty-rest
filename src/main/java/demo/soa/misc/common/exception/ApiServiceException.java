package demo.soa.misc.common.exception;

public class ApiServiceException extends ServiceException {
	private static final long serialVersionUID = 6892638476280164270L;
	
	public ApiServiceException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public ApiServiceException(String message) {
		super(message);
	}
}
