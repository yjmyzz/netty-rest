package demo.soa.misc.common.server;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class NettyHttpRequest {

    private final HttpRequest request;
    private MessageEvent messageEvent;
    private final Map<String, String> params;
    private final String path;
    private final String remoteAddr;
    private static final Pattern commaPattern = Pattern.compile(",");

    public String getPath() {
        return path;
    }

    public NettyHttpRequest(MessageEvent event) {
        this.messageEvent = event;
        this.remoteAddr = getIpAddr(event);
        this.request = (HttpRequest) event.getMessage();
        this.params = new HashMap<String, String>();
        String uri = request.getUri();
        int pathEndPos = uri.indexOf('?');
        if (pathEndPos < 0) {
            this.path = uri;
        } else {
            this.path = uri.substring(0, pathEndPos);
            decodeQueryString(uri, pathEndPos + 1, params);
        }
    }

    private String getIpAddr(MessageEvent event) {
        HttpRequest request = (HttpRequest) event.getMessage();
        //String ip = request.getHeader("X-Forwarded-For");
        String ip = request.headers().get("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //ip = request.getHeader("REMOTE_ADDR");
            ip = request.headers().get("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0) {
            ip = ((InetSocketAddress) event.getChannel().getRemoteAddress()).getAddress().getHostAddress();
        }
        return ip;
    }

    public HttpMethod method() {
        return this.request.getMethod();
    }

    public static void decodeQueryString(String queryString, int fromIndex,
                                         Map<String, String> params) {
        if (fromIndex < 0) {
            return;
        }
        if (fromIndex >= queryString.length()) {
            return;
        }
        int toIndex;
        while ((toIndex = queryString.indexOf('&', fromIndex)) >= 0) {
            int idx = queryString.indexOf('=', fromIndex);
            if (idx < 0) {
                continue;
            }
            params.put(decodeComponent(queryString.substring(fromIndex, idx)),
                    decodeComponent(queryString.substring(idx + 1, toIndex)));
            fromIndex = toIndex + 1;
        }
        int idx = queryString.indexOf('=', fromIndex);
        if (idx < 0) {
            return;
        }
        params.put(decodeComponent(queryString.substring(fromIndex, idx)),
                decodeComponent(queryString.substring(idx + 1)));
    }

    private static String decodeComponent(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException("UTF8");
        }
    }

    public float paramAsFloat(String key, float defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }

        return Float.parseFloat(sValue);

    }

    public int paramAsInt(String key, int defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }

        return Integer.parseInt(sValue);

    }

    public boolean paramAsBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(param(key));
    }

    public Boolean paramAsBoolean(String key, Boolean defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }
        return !(sValue.equals("false") || sValue.equals("0") || sValue
                .equals("off"));
    }

    public String[] paramAsStringArray(String key, String[] defaultValue) {
        String value = param(key);
        if (value == null) {
            return defaultValue;
        }
        return commaPattern.split(value);
    }

    public String uri() {
        return request.getUri();
    }

    public String path() {
        return path;
    }

    public Map<String, String> params() {
        return params;
    }

    public boolean hasContent() {
        return request.getContent().readableBytes() > 0;
    }

    public int contentLength() {
        return request.getContent().readableBytes();
    }

    public boolean contentUnsafe() {
        return request.getContent().hasArray();
    }

    public int contentByteArrayOffset() {
        if (request.getContent().hasArray()) {
            // get the array offset, and the reader index offset within it
            return request.getContent().arrayOffset() + request.getContent().readerIndex();
        }
        return 0;
    }

    private static Charset UTF8 = Charset.forName("UTF-8");

    public String contentAsString() {
        return request.getContent().toString(UTF8);
    }

    public Set<String> headerNames() {
        //return request.getHeaderNames();
        return request.headers().names();
    }

    public String header(String name) {
        //return request.getHeader(name);
        return request.headers().get(name);
    }

    public List<String> headers(String name) {
        //return request.getHeaders(name);
        return request.headers().getAll(name);
    }

    public String cookie() {
        return request.headers().get(HttpHeaders.Names.COOKIE);
    }

    public boolean hasParam(String key) {
        return params.containsKey(key);
    }

    public String param(String key) {
        return params.get(key);
    }

    public String param(String key, String defaultValue) {
        String value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public MessageEvent getMessageEvent() {
        return messageEvent;
    }

    public void setMessageEvent(MessageEvent messageEvent) {
        this.messageEvent = messageEvent;
    }

    public String getRemoteAddr() {
        return this.remoteAddr;
    }

}
