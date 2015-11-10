package demo.soa.misc.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.soa.misc.common.LRUCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DtoUtil {
	
	private static Logger log = LoggerFactory.getLogger(DtoUtil.class);
	
	private static final LRUCache<String, HashMap<String, Field>> modelCache = new LRUCache<String, HashMap<String, Field>>(50);

	public static String[] getFiledName(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	String[] fieldNames=new String[fields.length];
    	for(int i=0;i<fields.length;i++){
    		fieldNames[i]=fields[i].getName();
    	}
    	return fieldNames;
    }
	
	public static void setFieldValueByName(String fieldName, Object o, Object value, Class<?> type) {  
        try {
        	String firstLetter = fieldName.substring(0, 1);
        	firstLetter = firstLetter.toUpperCase();
              
            String setter = "set" + firstLetter + fieldName.substring(1);  
            Method method = o.getClass().getMethod(setter, type);  
            method.invoke(o, value);   
        } catch (Exception e) {  
//            log.info("属性不存在");
        }  
    }
	
	public static Object setDto(Object o, JSONObject jsonObject){
		String[] filedNames = getFiledName(o);
		for(int i=0; i<filedNames.length; i++){
			if(jsonObject.get(filedNames[i]) instanceof Integer){
				Integer name = (Integer)jsonObject.get(filedNames[i]);
				setFieldValueByName(filedNames[i],o,name,Integer.class);
			}else if(jsonObject.get(filedNames[i]) instanceof String){
				String name = (String)jsonObject.get(filedNames[i]);
				setFieldValueByName(filedNames[i],o,name,String.class);
			}
		}
		return o;
	}
	
	public static <T> List<T> jsonArrayToList(JSONArray array, Class<T> clazz){
		List<T> list = null;
		if(array != null){
			list = new ArrayList<T>();
			for(Object o : array){
				T e = JSONObject.toJavaObject((JSONObject)o, clazz);
				list.add(e);
			}
		}
		return list;
	}
	
	public static <T> List<T> jsonArrayToBeanList(JSONArray array, Class<T> clazz){
		List<T> list = null;
		if(array != null){
			list = new ArrayList<T>();
			for(Object o : array){
				T e = jsonToBean((JSONObject)o, clazz);
				list.add(e);
			}
		}
		return list;
	}
	
	public static <T> T jsonToBean(JSONObject json, Class<T> clazz){
		try{
			Field[] fields = clazz.getDeclaredFields();
			String jsonFieldName;
			T object = clazz.newInstance();
			for(Field field : fields){
				try{
					jsonFieldName = WordUtil.underscore(field.getName());
					if(field.getType().getName().equals(Integer.class.getName())){
						Integer name = json.getInteger(jsonFieldName);
						setFieldValueByName(field.getName(), object, name, Integer.class);
					}else if(field.getType().getName().equals(String.class.getName())){
						String name = json.getString(jsonFieldName);
						setFieldValueByName(field.getName(), object, name,String.class);
					}else if(field.getType().getName().equals(Date.class.getName())){
						Date name = json.getDate(jsonFieldName);
						setFieldValueByName(field.getName(), object, name,String.class);
					}else if(field.getType().getName().equals(Byte.class.getName())){
						Byte name = json.getByte(jsonFieldName);
						setFieldValueByName(field.getName(), object, name,String.class);
					}else if(field.getType().getName().equals(Boolean.class.getName())){
						Boolean name = json.getBooleanValue(jsonFieldName);
						setFieldValueByName(field.getName(), object, name, Boolean.class);
					}
				}catch(Exception e){
					log.error(e.getMessage(), e);
				}
			}
			return object;
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz){
		try{
			Field[] fields = clazz.getDeclaredFields();
			String mapFieldName;
			T object = clazz.newInstance();
			for(Field field : fields){
				try{
//					mapFieldName = WordUtil.underscore(field.getName());
					mapFieldName = field.getName();
					if(field.getType().getName().equals(Integer.class.getName())){
						Integer val = Utilities.parseInt((String)map.get(mapFieldName));
						setFieldValueByName(field.getName(), object, val, Integer.class);
					}else if(field.getType().getName().equals(String.class.getName())){
						String val = (String)map.get(mapFieldName);
						setFieldValueByName(field.getName(), object, val,String.class);
					}else if(field.getType().getName().equals(Byte.class.getName())){
						Byte val = Utilities.parseByte((String)map.get(mapFieldName));
						setFieldValueByName(field.getName(), object, val,String.class);
					}else if(field.getType().getName().equals(Boolean.class.getName())){
						Boolean val = Utilities.parseBoolean(((String)map.get(mapFieldName)));
						setFieldValueByName(field.getName(), object, val, Boolean.class);
					}else if(field.getType().getName().equals(Short.class.getName())){
						Short val = Utilities.parseShort(((String)map.get(mapFieldName)));
						setFieldValueByName(field.getName(), object, val, Short.class);
					}
				}catch(Exception e){
					log.error(e.getMessage(), e);
				}
			}
			return object;
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static <T> Map<String, Object> bean2map(T obj){
		Field[] fields = obj.getClass().getDeclaredFields();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		for(Field field : fields){
			try{
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj));
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
		
		return map;
	}
	
	public static <T> T contract2Model(Object contract, Class<T> clazz) throws InstantiationException, IllegalAccessException{
		Field[] fields = contract.getClass().getDeclaredFields();
		T object = clazz.newInstance();
		HashMap<String, Field> modelFields = getModelFields(clazz);
				
		for(Field field : fields){
			try{
				field.setAccessible(true);
				Object value = field.get(contract);
				Field modelField = modelFields.get(field.getName());
				if(modelField != null){
					modelField.setAccessible(true);
					modelField.set(object, value);
				}
			}catch(IllegalArgumentException e){
				
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
		return object;
	}
	
	public static void convert(Object from, Object to) throws InstantiationException, IllegalAccessException{
		Field[] fields = from.getClass().getDeclaredFields();
		Class clazz = to.getClass();
		HashMap<String, Field> modelFields = getModelFields(clazz);
				
		for(Field field : fields){
			try{
				field.setAccessible(true);
				Object value = field.get(from);
				Field modelField = modelFields.get(field.getName());
				if(modelField != null){
					modelField.setAccessible(true);
					modelField.set(to, value);
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
	}
	
	private static HashMap<String, Field> getModelFields(Class<?> clazz){
		HashMap<String, Field> modelFields = modelCache.get(clazz.getName());
		if(modelFields == null){
			modelFields = new HashMap<String, Field>();
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				modelFields.put(field.getName(), field);
			}
			modelCache.put(clazz.getName(), modelFields);
		}
		
		return modelFields;
	}
	
	public static Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException, SecurityException{
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if(method.getDeclaringClass().isInterface()){
			method = joinPoint.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
		}
		return method;
	}
}
