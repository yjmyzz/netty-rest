package demo.soa.common.service.impl;

import demo.soa.common.service.IAsyncService;
import demo.soa.misc.common.exception.ServiceException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service("asyncService")
public class AsyncService implements IAsyncService
{
	@Async
	@Override
	public void runAsync(Runnable task)
	{
		task.run();
	}

	@Async
	@Override
	public void invokeAsync(Object obj, Method method, Object ... args)
	{
		try
		{
			method.invoke(obj, args);
		}
		catch(Exception e)
		{
			throw ServiceException.wrapServiceException(e);
		}
	}

}
