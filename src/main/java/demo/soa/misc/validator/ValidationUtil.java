package demo.soa.misc.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import demo.soa.misc.common.exception.ServiceException;
import demo.soa.misc.resp.NormalReturn;

public class ValidationUtil {
	public static <T> Map<String, ArrayList<String>> validator(T t, HashSet<String> skipFields){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();		
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
		if(constraintViolations != null && constraintViolations.size() > 0){
			Map<String, ArrayList<String>> mapErr = new HashMap<String, ArrayList<String>>();
		    for (ConstraintViolation<T> constraintViolation : constraintViolations) {  
		    	for(Path.Node node : constraintViolation.getPropertyPath()){
		    		String fieldName = node.getName();
		    		//跳过校验的属性，校验错误不加入错误列表
		    		if(skipFields == null || !skipFields.contains(fieldName)){
			    		ArrayList<String> lst = mapErr.get(fieldName);
			    		if(lst == null){
			    			lst = new ArrayList<String>();
			    		}
			    		lst.add(constraintViolation.getMessage());
			    		mapErr.put(node.getName(), lst);
		    		}
		    	}
		    }
		    return mapErr;
		}
		return null;
	}
	
	public static <T> Map<String, ArrayList<String>> validator(T t){
		return validator(t, null);
	}
	
	public static <T> NormalReturn validate(T t, HashSet<String> skipFields){
		Map<String, ArrayList<String>> validationErrors = ValidationUtil.validator(t, skipFields);
		NormalReturn nr = null;
		if(validationErrors != null){
			nr = new NormalReturn();
			nr.setValidationErrors(validationErrors);
			nr.setStatusCode(ServiceException.VALIDATE_ERROR);
			nr.setMsg("数据校验错误");
		}
		return nr;
	}
	
	public static <T> NormalReturn validate(T t){
		return validate(t, null);
	}
}
