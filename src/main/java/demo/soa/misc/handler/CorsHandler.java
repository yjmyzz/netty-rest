package demo.soa.misc.handler;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;

public class CorsHandler extends SimpleChannelHandler {
    protected static Logger log = LoggerFactory.getLogger(CorsHandler.class);

    private final CorsConfig config;

    private HttpRequest request;

    public CorsHandler(CorsConfig config) {
        this.config = config;
    }

    private void handlePreflight(final ChannelHandlerContext ctx,
                                 final HttpRequest request) {
        final HttpResponse response = new DefaultHttpResponse(
                request.getProtocolVersion(), OK);
        if (setOrigin(response)) {
            setAllowMethods(response);
            setAllowHeaders(response);
            setAllowCredentials(response);
            setMaxAge(response);
            setPreflightHeaders(response);
        }
        // ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        writeAndClose(ctx, response);
    }

    /**
     * This is a non CORS specification feature which enables the setting of
     * preflight response headers that might be required by intermediaries.
     *
     * @param response the HttpResponse to which the preflight response headers
     *                 should be added.
     */
    private void setPreflightHeaders(final HttpResponse response) {
        response.headers().add(config.preflightResponseHeaders());
    }

    private boolean setOrigin(final HttpResponse response) {
        final String origin = request.headers().get(ORIGIN);
        if (origin != null) {
            if ("null".equals(origin) && config.isNullOriginAllowed()) {
                setAnyOrigin(response);
                return true;
            }
            if (config.isAnyOriginSupported()) {
                if (config.isCredentialsAllowed()) {
                    echoRequestOrigin(response);
                    setVaryHeader(response);
                } else {
                    setAnyOrigin(response);
                }
                return true;
            }
            if (config.origins().contains(origin)) {
                setOrigin(response, origin);
                setVaryHeader(response);
                return true;
            }
        }
        return false;
    }

    private boolean validateOrigin() {
        if (config.isAnyOriginSupported()) {
            return true;
        }

        final String origin = request.headers().get(ORIGIN);
        if (origin == null) {
            // Not a CORS request so we cannot validate it. It may be a non CORS
            // request.
            return true;
        }

        if ("null".equals(origin) && config.isNullOriginAllowed()) {
            return true;
        }

        return config.origins().contains(origin);
    }

    private void echoRequestOrigin(final HttpResponse response) {
        setOrigin(response, request.headers().get(ORIGIN));
    }

    private static void setVaryHeader(final HttpResponse response) {
        //response.headers().add(VARY, ORIGIN);
        response.headers().add(VARY, ORIGIN);
    }

    private static void setAnyOrigin(final HttpResponse response) {
        setOrigin(response, "*");
    }

    private static void setOrigin(final HttpResponse response,
                                  final String origin) {
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    }

    private void setAllowCredentials(final HttpResponse response) {
        if (config.isCredentialsAllowed()) {
            response.headers().add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            //response.headers().add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
    }

    private static boolean isPreflightRequest(final HttpRequest request) {
        return request.getMethod().equals(HttpMethod.OPTIONS)
                && request.headers().contains(ORIGIN)
                && request.headers().contains(ACCESS_CONTROL_REQUEST_METHOD);

    }

    private void setExposeHeaders(final HttpResponse response) {
        if (!config.exposedHeaders().isEmpty()) {

            response.headers().add(ACCESS_CONTROL_EXPOSE_HEADERS,
                    config.exposedHeaders());
        }
    }

    private void setAllowMethods(final HttpResponse response) {
        response.headers().add(ACCESS_CONTROL_ALLOW_METHODS,
                config.allowedRequestMethods());
    }

    private void setAllowHeaders(final HttpResponse response) {
        response.headers().add(ACCESS_CONTROL_ALLOW_HEADERS,
                config.allowedRequestHeaders());
    }

    private void setMaxAge(final HttpResponse response) {
        response.headers().add(ACCESS_CONTROL_MAX_AGE, config.maxAge());
    }

    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable cause) throws Exception {
        // ctx.fireExceptionCaught(cause);
    }

    private static void forbidden(final ChannelHandlerContext ctx,
                                  final HttpRequest request) {
        writeAndClose(ctx, new DefaultHttpResponse(
                request.getProtocolVersion(), FORBIDDEN));
        // ctx.writeAndFlush(new
        // DefaultHttpResponse(request.getProtocolVersion(), FORBIDDEN))
        // .addListener(ChannelFutureListener.CLOSE);
    }

    private static void writeAndClose(final ChannelHandlerContext ctx,
                                      final HttpResponse response) {
        log.info("Cors handler start to writeAndClose.");
        Channel channel = ctx.getChannel();
        channel.write(response).addListener(ChannelFutureListener.CLOSE);
        log.info("Cors handler finish to writeAndClose.");

    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent event)
            throws Exception {

        if (event != null && event instanceof UpstreamMessageEvent) {
            UpstreamMessageEvent mEvent = (UpstreamMessageEvent) event;
            log.info("Cors handler start handle Upstream EVENT.");
            if (config.isCorsSupportEnabled()
                    && mEvent.getMessage() instanceof HttpRequest) {
                request = (HttpRequest) mEvent.getMessage();
                if (isPreflightRequest(request)) {
                    log.info("Cors handler start to process preflight request.");
                    handlePreflight(ctx, request);
                    log.info("Cors handler finish to process preflight request.");
                    return;
                }
                if (config.isShortCurcuit() && !validateOrigin()) {
                    forbidden(ctx, request);
                    return;
                }
            } else {
                log.info("Cors is disabled.");
            }
        }

        super.handleUpstream(ctx, event);

    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent event)
            throws Exception {

        final String origin = request.headers().get(ORIGIN);
        if (origin != null) {
            if (event != null && event instanceof DownstreamMessageEvent) {
                DownstreamMessageEvent mEvent = (DownstreamMessageEvent) event;

                HttpResponse rsp = (HttpResponse) mEvent.getMessage();

                rsp.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, config.origin());
            }
        }
        super.handleDownstream(ctx, event);
    }
}
