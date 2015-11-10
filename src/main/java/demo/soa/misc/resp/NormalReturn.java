package demo.soa.misc.resp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import net.sf.json.JSONObject;

import com.alibaba.fastjson.annotation.JSONField;

public class NormalReturn implements Serializable {
	private static final long serialVersionUID = -5219724691798725987L;

	private String statusCode="200";

	private String msg="ok";
	
	@JSONField(name = "result")
	private Object result;	
	
	private long costTime;
	
	@JSONField(name = "validationErrors")
	private Map<String, ArrayList<String>> validationErrors;
	
	public NormalReturn(){
		
	}
	
	public NormalReturn(Object result){
		this.result = result;
	}
	
	public NormalReturn(String msg, String statusCode){
		this.msg = msg;
		this.statusCode = statusCode;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getCostTime() {
		return costTime;
	}
	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}
	
	public Map<String, ArrayList<String>> getValidationErrors() {
		return validationErrors;
	}
	public void setValidationErrors(Map<String, ArrayList<String>> validationErrors) {
		this.validationErrors = validationErrors;
	}

	public static NormalReturn check(JSONObject jso, String... params){
		for(String param: params){
			if(!jso.containsKey(param)){
				NormalReturn nr = new NormalReturn();
				nr.setStatusCode("400");
				nr.setMsg(param+" is null");
				return nr;
			}else if(jso.getString(param).isEmpty()){
				NormalReturn nr = new NormalReturn();
				nr.setStatusCode("400");
				nr.setMsg(param+" is empty");				
				return nr;
			}
		}
		return null;
	}
	
}
