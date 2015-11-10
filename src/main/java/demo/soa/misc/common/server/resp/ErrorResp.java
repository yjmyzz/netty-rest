package demo.soa.misc.common.server.resp;

import java.nio.charset.Charset;

import demo.soa.misc.common.Strings;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import demo.soa.misc.common.exception.FrameworkException;
import demo.soa.misc.common.util.DateUtil;

public class ErrorResp extends Resp {
	
	private static final long serialVersionUID = 34687954757L;
	public String errorMsg;
	
	@Override
	public ChannelBuffer toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"ok\":false,\"now\":\"").append(DateUtil.getCurrentDateStr())
				.append("\",\"msg\":")
				.append(errorMsg).append("}");
		return ChannelBuffers.copiedBuffer(sb.toString(), Charset.forName("UTF-8"));
	}

	public ErrorResp(String msg) {
		this.errorMsg = Strings.quote(msg);
		this.respCode = 502;
	}
	
	public ErrorResp(String msg, int errorCode) {
		this.errorMsg = Strings.quote(msg);
		this.respCode = (short) errorCode;
	}

	public ErrorResp(Throwable t) {
		this(Strings.throwableToString(t));
		if (t instanceof FrameworkException) {
			this.respCode = (short) ((FrameworkException) t).errorCode;
		}
	}

	public ErrorResp(String errors, Throwable t) {
		this(errors + "\n" + Strings.throwableToString(t));
		if (t instanceof FrameworkException) {
			this.respCode = (short) ((FrameworkException) t).errorCode;
		}
	}

	public ErrorResp(Throwable t, int errorCode) {
		this(Strings.throwableToString(t), errorCode);
	}

}
