package demo.soa.misc.common.server.resp;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import demo.soa.misc.common.Strings;
import demo.soa.misc.common.server.RestProcesser;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import demo.soa.misc.common.util.DateUtil;

public class Resp implements Serializable {
	
	private static final long serialVersionUID = 34543654757L;
	public short respCode = 200;
	protected transient List<String> warningList = null;
	public String content;
	
	public Resp() {
	}	
	
	public Resp(String content) {
		super();
		this.content = content;
	}
	
	public String contentType()
	{
		return RestProcesser.CONTENT_TYPE_JSON;
	}
	
	public ChannelBuffer body(){
		return ChannelBuffers.copiedBuffer(content, Charset.forName("UTF-8"));
	}

	public ChannelBuffer toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"ok\":true,\"now\":")
		.append("\""+DateUtil.getCurrentDateStr()+"\"")
		.append("}");
		return ChannelBuffers.copiedBuffer(sb.toString(), Charset.forName("UTF-8"));
	}
	
	public void addWarning(String s) {
		if (s == null || s.trim().equals("")) {
			return;
		}
		if (warningList == null) {
			warningList = new ArrayList<String>(4);
		}
		warningList.add(s);
	}


	protected void addWarnings(StringBuilder sb) {
		if (this.warningList != null && warningList.size() > 0) {
			Strings.quoteSafeJson(sb, "warnings");
			sb.append(":[");
			for (String s : warningList) {
				if (s != null) {
					Strings.quoteSafeJson(sb, s);
					sb.append(",");
				}
			}
			sb.setCharAt(sb.length() - 1, ']');
		}
	}

	@Override
	public String toString() {
		return this.toJson().toString(Charset.forName("UTF-8"));
	}
	
	public static void main(String[] args) {
		System.out.println(new Resp());
	}

}
