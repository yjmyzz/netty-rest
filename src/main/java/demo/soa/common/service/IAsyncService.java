package demo.soa.common.service;

import java.lang.reflect.Method;

public interface IAsyncService
{
	void runAsync(Runnable task);
	void invokeAsync(Object obj, Method method, Object ... args);
}
