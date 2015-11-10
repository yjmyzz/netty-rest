package demo.soa.misc.common.server;

import demo.soa.misc.common.server.resp.ErrorResp;
import net.sf.json.JSONException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.soa.misc.common.server.resp.Resp;

public class RestProcesser extends Object {

    private static Logger log = LoggerFactory.getLogger(RestProcesser.class);

    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    public static final String CONTENT_TYPE_XML = "application/xml;charset=utf-8";

    private final PathTrie<Handler> getHandlers = new PathTrie<Handler>();
    private final PathTrie<Handler> postHandlers = new PathTrie<Handler>();
    private final PathTrie<Handler> putHandlers = new PathTrie<Handler>();
    private final PathTrie<Handler> deleteHandlers = new PathTrie<Handler>();

    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent me) {
        final HttpRequest hr = (HttpRequest) me.getMessage();
        Channel channel = me.getChannel();
        try {
            NettyHttpRequest request = new NettyHttpRequest(me);
            final Handler handler = getHandler(request);
            if (handler == null) {
                ErrorResp resp = new ErrorResp("No handler found for uri [" + request.uri() + "] and method [" + request.method() + "]", 404);
                doHttpResp(request, resp, hr, channel);
            } else {
                try {
                    Resp resp = handler.handleRequest(request);
                    doHttpResp(request, resp, hr, channel);
                } catch (JSONException e) {
                    ErrorResp resp = new ErrorResp(e, 400);
                    doHttpResp(request, resp, hr, channel);
                } catch (Exception e) {
                    ErrorResp resp = new ErrorResp(e);
                    doHttpResp(request, resp, hr, channel);
                }
            }
        } catch (Exception e) {
            log.error("messageReceived error:", e);
            doHttpResp(null, new ErrorResp(e, 400), hr, channel);
        }
    }

    public void registerHandler(HttpMethod method, String path, Handler handler) {
        if (method == HttpMethod.GET) {
            getHandlers.insert(path, handler);
        } else if (method == HttpMethod.POST) {
            postHandlers.insert(path, handler);
        } else if (method == HttpMethod.PUT) {
            putHandlers.insert(path, handler);
        } else if (method == HttpMethod.DELETE) {
            deleteHandlers.insert(path, handler);
        } else {
            throw new RuntimeException("HttpMethod is not supported");
        }
    }

    private final void doHttpResp(NettyHttpRequest req, Resp response, final HttpRequest request, final Channel channel) {
        doHttpResp(req, response, request, channel, response.contentType());
    }

    private final void doHttpResp(NettyHttpRequest req, Resp response, final HttpRequest request, final Channel channel, String contentType) {
        DefaultHttpResponse defaultResponse = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.valueOf(response.respCode));
        defaultResponse.setContent(response.toJson());

        //defaultResponse.setHeader("Content-Type", contentType);
        defaultResponse.headers().add("Content-Type", contentType);
        //defaultResponse.setHeader("Content-Length", defaultResponse.getContent().readableBytes());
        defaultResponse.headers().add("Content-Length", defaultResponse.getContent().readableBytes());
        boolean close = !HttpHeaders.isKeepAlive(request);
//		close = true;
        //defaultResponse.setHeader(HttpHeaders.Names.CONNECTION, close ? HttpHeaders.Values.CLOSE : HttpHeaders.Values.KEEP_ALIVE);
        defaultResponse.headers().add(HttpHeaders.Names.CONNECTION, close ? HttpHeaders.Values.CLOSE : HttpHeaders.Values.KEEP_ALIVE);
        ChannelFuture cf = channel.write(defaultResponse);
        String remoteAddress = channel.getRemoteAddress().toString();
        String uri = request.getUri();
        String method = request.getMethod().getName();
        log.info("{},{},{}", req.getRemoteAddr(), uri, method);
        if (close) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private Handler getHandler(NettyHttpRequest request) {
        String path = request.path();
        HttpMethod method = request.method();
        if (method == HttpMethod.GET) {
            return getHandlers.retrieve(path, request.params());
        } else if (method == HttpMethod.POST) {
            return postHandlers.retrieve(path, request.params());
        } else if (method == HttpMethod.PUT) {
            return putHandlers.retrieve(path, request.params());
        } else if (method == HttpMethod.DELETE) {
            return deleteHandlers.retrieve(path, request.params());
        } else {
            return null;
        }
    }
}
