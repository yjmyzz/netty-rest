package demo.soa.misc.common;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JSONBase {

	public static List<StrIntBag> childrenStrIntBag(JSONObject jso) {
		List<StrIntBag> r = new ArrayList<StrIntBag>();
		for (Object o : jso.keySet()) {
			StrIntBag sp = new StrIntBag();
			sp._str = o.toString();
			sp._int = jso.getInt(o.toString());

			r.add(sp);
		}
		return r;
	}

	public static List<StringPair> childrenStringPair(JSONObject jso) {
		List<StringPair> r = new ArrayList<StringPair>();
		for (Object o : jso.keySet()) {
			String name=o.toString();
			Object v=jso.get(name);
			if( v==null || v.equals("")){
				continue;
			}
			StringPair sp = new StringPair(name,v.toString());
			r.add(sp);
		}
		return r;
	}

	public static String[] getStrArray(JSONObject jso, String name) {
		if (jso.get(name) == null) {
			return null;
		}
		Object obj = jso.get(name);
		if(obj instanceof JSONObject || obj instanceof String){
			return new String[]{(String)obj};
		}else if(obj instanceof JSONArray){
			JSONArray jsa = jso.getJSONArray(name);
			if (jsa == null) {
				return null;
			}
			String[] ss = new String[jsa.size()];
			for (int i = 0; i < ss.length; i++) {
				ss[i] = jsa.get(i).toString();
			}
			return ss;
		}else{
			return null;
		}
	}
	
	public static int[] getIntArray(JSONObject jso, String name) {
		if (jso.get(name) == null) {
			return null;
		}
		JSONArray jsa = jso.getJSONArray(name);
		if (jsa == null) {
			return null;
		}
		int[] ss = new int[jsa.size()];
		for (int i = 0; i < ss.length; i++) {
			ss[i] = jsa.getInt(i);
		}
		return ss;
	}

	public static int getInt(JSONObject jso, String name, int defaultv) {
		if (jso.get(name) == null)
			return defaultv;
		return jso.getInt(name);

	}

	public static String getStr(JSONObject jso, String name, String defaultv) {
		if (jso.get(name) == null)
			return defaultv;
		return jso.getString(name);

	}

	public static boolean getBoolean(JSONObject jso, String name,
			boolean defaultv) {
		if (jso.get(name) == null)
			return defaultv;
		return jso.getBoolean(name);

	}

	public static String getOnlyKey(JSONObject jso) {
		return jso.keys().next().toString();
	}
}
