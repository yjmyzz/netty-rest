package demo.soa.misc.common;

import demo.soa.misc.common.exception.FrameworkException;

public class ApiException extends FrameworkException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6656039021953217817L;
	private String code="400";
	
	public String getCode() {
		return code;
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(String code, String message) {
		super(message);
		this.code = code;
	}

}
