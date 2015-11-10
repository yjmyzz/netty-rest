package demo.soa.misc.aspect.contract;

import com.alibaba.fastjson.JSONObject;
import demo.soa.misc.common.util.DtoUtil;
import demo.soa.misc.req.ServiceRequest;
import demo.soa.misc.resp.NormalReturn;
import demo.soa.misc.validator.ValidationUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Order(1)
@Component
public class ContractManager {
	
	@Pointcut("@annotation (demo.soa.misc.aspect.contract.Contract)")
	private void contract(){};

	@Pointcut("@annotation (demo.soa.misc.aspect.contract.Model)")
	private void model(){};
	
	@Around("contract()")
	public Object contract(ProceedingJoinPoint joinPoint) throws Throwable{
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Object[] args = joinPoint.getArgs();
		
		if(args != null && args.length > 0 && args[0] instanceof ServiceRequest
				&& signature.getReturnType().getName().equals("demo.soa.misc.resp.NormalReturn")){
			ServiceRequest req = (ServiceRequest)args[0];
			Method method = DtoUtil.getMethod(joinPoint);
			Contract annotation = method.getAnnotation(Contract.class);
			if(annotation != null){
				Class<?> clazz = annotation.value();
				if(clazz != null){
					Object o = JSONObject.toJavaObject(req.getJson(), clazz);
					NormalReturn nr = ValidationUtil.validate(o);
					if(nr != null){
						return nr;
					}
					req.setContract(o);
				}
			}
		}
		return joinPoint.proceed();
	}
	
	@Around("model()")
	public Object model(ProceedingJoinPoint joinPoint) throws Throwable{
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Object[] args = joinPoint.getArgs();
		
		if(args != null && args.length > 0 && args[0] instanceof ServiceRequest
				&& signature.getReturnType().getName().equals("demo.soa.misc.resp.NormalReturn")){
			ServiceRequest req = (ServiceRequest)args[0];
			Method method = DtoUtil.getMethod(joinPoint);
			Model annotation = method.getAnnotation(Model.class);
			if(annotation != null){
				Class<?> clazzContract = annotation.contract();
				Class<?> clazzModel = annotation.model();
				if(clazzContract != null && clazzModel != null){
					Object o = JSONObject.toJavaObject(req.getJson(), clazzContract);
					NormalReturn nr = ValidationUtil.validate(o);
					if(nr != null){
						return nr;
					}
					req.setContract(o);
					req.setModel(DtoUtil.contract2Model(o, clazzModel));
				}
			}
		}
		return joinPoint.proceed();
	}
}
