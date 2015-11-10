package demo.soa.misc.common.exception;

public class ServiceException extends SCException {

	private static final long serialVersionUID = 2812193681069300746L;
	
	/** 成功 */
	public static final String API_OK				= "200";
	/** 部分成功 */
	public static final String API_PARTIAL_OK		= "206";
	/** 对象不存在 */
	public static final String NOT_EXIST			= "404";
	/** 服务器内部错误 */
	public static final String GENERAL_ERROR		= "500";
	/** 参数验证错误 */
	public static final String VALIDATE_ERROR		= "501";
	/** 接口已关闭 */
	public static final String NOT_IMPLEMENTED		= "502";
	/** APP CODE 不存在 */
	public static final String APPCODE_ERROR		= "600";
	/** 用户验证错误 */
	public static final String AUTHEN_ERROR			= "701";
	/** 用户授权错误 */
	public static final String AUTHOR_ERROR			= "702";
	/** 网络错误 */
	public static final String NETWORK_ERROR		= "801";
	/** 签名验证失败 */
	public static final String SIGN_VERIFY_ERROR	= "901";
		
	public static final ServiceException NOT_EXIST_EXCEPTION		= new ServiceException("对象不存在", NOT_EXIST);
	public static final ServiceException GENERAL_EXCEPTION			= new ServiceException("服务器内部错误", GENERAL_ERROR);
	public static final ServiceException VALIDATE_EXCEPTION			= new ServiceException("参数验证错误", VALIDATE_ERROR);
	public static final ServiceException NOT_IMPLEMENTED_EXCEPTION	= new ServiceException("接口未实现", NOT_IMPLEMENTED);
	public static final ServiceException APPCODE_EXCEPTION			= new ServiceException("应用程序编号已存在", APPCODE_ERROR);
	public static final ServiceException AUTHEN_EXCEPTION			= new ServiceException("用户认证失败", AUTHEN_ERROR);
	public static final ServiceException AUTHOR_EXCEPTION			= new ServiceException("授权验证失败", AUTHOR_ERROR);
	public static final ServiceException NETWORK_EXCEPTION			= new ServiceException("网络错误", NETWORK_ERROR);
	public static final ServiceException SIGN_VERIFY_EXCEPTION		= new ServiceException("签名验证失败", SIGN_VERIFY_ERROR);
	
	private String statusCode;
	
	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, String statusCode) {
		super(message);
		setStatusCode(statusCode);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message, String statusCode, Throwable cause) {
		super(message, cause);
		setStatusCode(statusCode);
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public static final ServiceException wrapServiceException(Throwable e)
	{
		return wrapServiceException(GENERAL_EXCEPTION.getMessage(), GENERAL_EXCEPTION.statusCode, e);
	}

	public static final ServiceException wrapServiceException(ServiceException se, Throwable e)
	{
		return wrapServiceException(se.getMessage(), se.getStatusCode(), e);
	}

	public static final ServiceException wrapServiceException(String message, String statusCode, Throwable e)
	{
		if(!(e instanceof ServiceException))
			e = new ServiceException(message, statusCode, e);
		
		return (ServiceException)e;
	}
	
	public static final void throwServiceException(Throwable e)
	{
		throw wrapServiceException(e);
	}
	
	public static final void throwServiceException(String message, String statusCode)
	{
		throw new ServiceException(message, statusCode);
	}
	
	public static final void throwServiceException(String message, String statusCode, Throwable e)
	{
		throw new ServiceException(message, statusCode, e);
	}

	public static final void throwServiceException(ServiceException e, Object ... args)
	{
		String message = String.format(e.getMessage(), args);
		throw new ServiceException(message, e.getStatusCode(), e.getCause());
	}

	public static final void throwValidateException(String message)
	{
		throw new ServiceException(message, VALIDATE_ERROR);
	}
	
}
