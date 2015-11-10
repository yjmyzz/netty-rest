package demo.soa.misc.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;

@SuppressWarnings({"rawtypes","unchecked","unused"})
public class MyJSONUtil {
	/**
	 * 将字符串转换为Bean,通过json-lib
	 * 
	 * @param str
	 * @return
	 */
	public static Object String2Bean(String str) {
		Object result = null;
		JSONObject jsonObject = null;

		jsonObject = JSONObject.fromObject(str);
		result = JSONObject.toBean(jsonObject);
		jsonObject = null;
		return result;
	}

	/**
	 * 将字符串转换为Map,通过json-lib
	 * 
	 * @param str
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Map String2Map(String str) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Map result = null;
		Object bean = null;

		bean = String2Bean(str);
		result = PropertyUtils.describe(bean);

		return result;
	}

	public static String map2String(Map<String, Object> map) {
		JSONObject json = JSONObject.fromObject(map);
		// System.out.println( json );
		return json.toString();
	}

	public static Map StringToMap1(String str) {
		Map map=new HashMap();
		if(str != null && !"".equals(str)) {
			JSONObject json=JSONObject.fromObject(str);
			if(json != null) {
				Iterator keys=json.keys();
				while(keys.hasNext()){
					String key=(String) keys.next();
					String value=json.get(key).toString();
					map.put(key, value);
				}
			}
		}
		return map;
	}
	
	public static Map<String,Object> StringToMap(String str) {
		if(str == null){
			return null;
		}
		Map<String,Object> map=new HashMap<String,Object>();
		if(str != null && !"".equals(str)) {
			JSONObject json=JSONObject.fromObject(str);
			if(json != null) {
				Iterator keys=json.keys();
				while(keys.hasNext()){
					String key=(String) keys.next();
					String value=json.get(key).toString();
					if(value.startsWith("[")&&value.endsWith("]")) {
						value = value.substring(1, value.length()-1);
						String[] lvalue = value.split("},");
						List<Map<String,Object>> lmap = new ArrayList<Map<String,Object>>();
						for(String subValue : lvalue) {
							Map<String,Object> map0=new HashMap<String,Object>();
							if(subValue.startsWith("{") && !subValue.endsWith("}")){
								subValue += "}";
								map0.put(key, StringToMap(subValue));
							} else if(subValue.startsWith("{") && subValue.endsWith("}")) {
								map0.put(key, StringToMap(subValue));
							} else {
								map0.put(key, subValue);
							}
							lmap.add(map0);
						}
						map.put(key, lmap);
					} else
						map.put(key, value);
				}
			}
		}
		return map;
	}
	
	public static void print(Map<String,Object> map) {
		Set<String> set = map.keySet();
		Object obj = null;
		for(String key : set) {
			obj = map.get(key);
			if(obj != null) {
				if(obj.getClass() == String.class) {
					System.out.println(key + "    :    " + obj);
				} else if(obj.getClass() == List.class) {
					List<Map<String,Object>> lmap = (List<Map<String, Object>>) obj;
					for(Map<String,Object> m : lmap) 
						print(m);
				}
			}
		}
	}
	
	public static String beanToString(Object obj) {
		JSONObject json = JSONObject.fromObject(obj);
		return json.toString();
	}
	
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String jsonStr="{\"id\":1,\"nickName\":\"\u6211\u662f\u8c01\",\"realName\":\"\u674e\u6c5f\",\"nameVisible\":2," +
				"\"provinceId\":28,\"cityId\":29,\"areaId\":33,\"sex\":0,\"birthday\":\"2010-3-12\",\"dateVisible\":1,"+
				"\"school\":\"\u5929\u6d25\u5e02\u73af\u6e56\u4e2d\u5b66\",\"schoolId\":139,\"teacherInfos\":" +
				"[{\"courseName\":\"\u82f1\u8bed\",\"gradeName\":\"\u56db\u5e74\u7ea7\",\"className\":\"07\u73ed\"," +
				"\"isClassTeacher\":0},{\"courseName\":\"\u82f1\u8bed\",\"gradeName\":\"\u4e8c\u5e74\u7ea7\"," +
				"\"className\":\"07\u73ed\",\"isClassTeacher\":1}],\"qq\":\"5644754877\",\"qqVisible\":\"0\"," +
				"\"msn\":\"222222211111\",\"msnVisible\":0,\"introduction\":\"\u6211\u5c31\u662f\u7b28\u86cb\"," +
				"\"cardType\":1,\"cardNum\":\"123123123123457676\"}";
		
		Map<String,Object> map = StringToMap(jsonStr);
//		print(map);
		
//		System.out.println(map.get("teacherInfos"));
	}
}