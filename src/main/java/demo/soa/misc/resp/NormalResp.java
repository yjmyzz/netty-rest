package demo.soa.misc.resp;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import demo.soa.misc.common.server.resp.Resp;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.nio.charset.Charset;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNonStringKeyAsString;

public class NormalResp extends Resp{
	public static final SerializerFeature[] JSON_SERIAL_FEATURES = {WriteMapNullValue, WriteNonStringKeyAsString};
	private static final long serialVersionUID = 1L;
	public NormalReturn sr;
	
	public NormalResp() {
	}

	public NormalResp(NormalReturn sr) {
		this.sr = sr;
	}

	@Override
	public ChannelBuffer toJson(){
		String json = JSONObject.toJSONString(sr, JSON_SERIAL_FEATURES);
		return ChannelBuffers.copiedBuffer(json, Charset.forName("UTF-8"));
	}
	
}
