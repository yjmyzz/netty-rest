
package demo.soa.misc.common.util;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.List;

public class HttpExecutor {
    public static final int DEFAULT_MAX_CONNECTION_TOTAL = 100;
    public static final int DEFAULT_MAX_CONNECTION_PER_ROUTE = 30;
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 20 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    public static final String DEFAULT_ENCODING = "UTF-8";

    RequestConfig defaultRequestConfig;
    private HttpClientContext httpContext;
    private CloseableHttpClient httpClient;
    private HttpClientConnectionManager connManager;

    private boolean useCookie = true;
    private int maxConnectionTotal = DEFAULT_MAX_CONNECTION_TOTAL;
    private int maxConnectionPerRoute = DEFAULT_MAX_CONNECTION_PER_ROUTE;
    private int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    private int connectTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    private String encoding = DEFAULT_ENCODING;

    private HttpHost httpProxy;

    public HttpExecutor() {
        this(new PoolingHttpClientConnectionManager());
    }

    public HttpExecutor(HttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public HttpExecutor initialize() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    RequestConfig.Builder requestBuilder = RequestConfig.custom()
                            .setConnectionRequestTimeout(connectionRequestTimeout)
                            .setConnectTimeout(connectTimeout)
                            .setSocketTimeout(socketTimeout);

                    if (httpProxy != null)
                        requestBuilder.setProxy(httpProxy);

                    defaultRequestConfig = requestBuilder.build();

                    ConnectionConfig.Builder connBuilder = ConnectionConfig.custom()
                            .setCharset(Charset.forName(encoding));
                    ConnectionConfig connCfg = connBuilder.build();

                    if (connManager instanceof PoolingHttpClientConnectionManager) {
                        PoolingHttpClientConnectionManager cm = (PoolingHttpClientConnectionManager) connManager;
                        cm.setMaxTotal(maxConnectionTotal);
                        cm.setDefaultMaxPerRoute(maxConnectionPerRoute);
                    }

                    HttpClientBuilder clientBuilder = HttpClients.custom()
                            .setConnectionManager(connManager)
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .setDefaultConnectionConfig(connCfg);

                    httpContext = HttpClientContext.create();

                    if (useCookie) {

//						Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create()
//																	.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
//																	.register(CookieSpecs.BROWSER_COMPATIBILITY,
//																	new BrowserCompatSpecFactory()).build();

                        //
                        Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create()
                                .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                                .build();
                        httpContext.setCookieSpecRegistry(registry);
                        httpContext.setCookieStore(new BasicCookieStore());
                    } else
                        clientBuilder.disableCookieManagement();

                    httpClient = clientBuilder.build();
                }
            }
        }

        return this;
    }

    public final boolean hasInitialized() {
        return httpClient != null;
    }

    public final void shutdown() {
        if (connManager != null)
            connManager.shutdown();
    }

    @Override
    protected void finalize() {
        shutdown();
    }

    public Response executeGet(String uri) {
        return executeGet(uri, null, null);
    }

    public Response executeGet(String uri, RequestConfig requestConfig) {
        return executeGet(uri, requestConfig, null);
    }

    public Response executeGet(String uri, List<NameValuePair> headers) {
        return executeGet(uri, null, headers);
    }

    public Response executeGet(String uri, RequestConfig requestConfig, List<NameValuePair> headers) {
        HttpGet httpGet = new HttpGet(uri);
        return execute(httpGet, requestConfig, headers);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data) {
        return executeFormPost(uri, data, null, null);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data, RequestConfig requestConfig) {
        return executeFormPost(uri, data, requestConfig, null);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data, List<NameValuePair> headers) {
        return executeFormPost(uri, data, null, headers);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data, RequestConfig requestConfig, List<NameValuePair> headers) {
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createFormEntity(data);
        httpPost.setEntity(entity);

        return execute(httpPost, requestConfig, headers);
    }

    public Response executeStringPost(String uri, String data) {
        return executeStringPost(uri, data, null, null, null);
    }

    public Response executeStringPost(String uri, String data, String mimeType) {
        return executeStringPost(uri, data, mimeType, null, null);
    }

    public Response executeStringPost(String uri, String data, String mimeType, List<NameValuePair> headers) {
        return executeStringPost(uri, data, mimeType, null, headers);
    }

    public Response executeStringPost(String uri, String data, String mimeType, RequestConfig requestConfig) {
        return executeStringPost(uri, data, mimeType, requestConfig, null);
    }

    public Response executeStringPost(String uri, String data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers) {
        if (GeneralHelper.isStrEmpty(mimeType))
            mimeType = ContentType.APPLICATION_JSON.getMimeType();

        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createStringEntity(data, mimeType);
        httpPost.setEntity(entity);

        return execute(httpPost, requestConfig, headers);
    }

    private HttpEntity createFormEntity(List<NameValuePair> params) {
        try {
            return new UrlEncodedFormEntity(params, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity createStringEntity(String content, String mimeType) {
        ContentType contentType = ContentType.create(mimeType, Charset.forName(encoding));
        return new StringEntity(content, contentType);
    }

    private void setHttpRequestHeaders(HttpRequestBase request, List<NameValuePair> headers) {
        if (headers != null) {
            for (NameValuePair nv : headers) {
                if (!nv.getName().equals("Cookie"))
                    request.setHeader(nv.getName(), nv.getValue());
                else if (useCookie) {
                    String value = GeneralHelper.safeString(nv.getValue());
                    String[] cks = value.split("\\;");

                    for (String ck : cks) {
                        String[] c = ck.split("\\=", 2);
                        if (c.length == 2) {
                            Cookie cookie = new BasicClientCookie(c[0].trim(), c[1].trim());
                            getCookieStore().addCookie(cookie);
                        }
                    }
                }
            }
        }
    }

    private Response execute(HttpRequestBase request, RequestConfig requestConfig, List<NameValuePair> headers) {
        HttpEntity respEntity = null;
        Response response = new Response(encoding);

        if (requestConfig != null)
            request.setConfig(requestConfig);
        if (headers != null)
            setHttpRequestHeaders(request, headers);

        try {
            HttpResponse resp = httpClient.execute(request, httpContext);
            respEntity = resp.getEntity();
            int code = resp.getStatusLine().getStatusCode();

            response.setStatusCode(code);
            response.setHeaders(resp.getAllHeaders());

            if (code == HttpStatus.SC_OK) {
                InputStream is = respEntity.getContent();
                byte[] content = HttpHelper.readBytes(is);
                Header enc = resp.getEntity().getContentEncoding();

                if (enc != null && enc.getValue().equals("gzip"))
                    content = HttpHelper.unGZip(content);

                response.setResultCode(Response.Success);
                response.setContent(content);
            } else if (code == HttpStatus.SC_MOVED_PERMANENTLY || code == HttpStatus.SC_MOVED_TEMPORARILY) {
                response.setResultCode(Response.Redirect);
                response.setContent(resp.getFirstHeader("Location").getValue().getBytes());
            } else {
                response.setResultCode(Response.ServerError);
                response.setErrorInfo(resp.getStatusLine().getReasonPhrase());
            }
        } catch (ConnectionPoolTimeoutException pte) {
            response.setResultCode(Response.ConnectionPoolTimeoutException);
            response.setErrorInfo(pte.getMessage());
        } catch (ConnectTimeoutException cte) {
            response.setResultCode(Response.ConnectTimeoutException);
            response.setErrorInfo(cte.getMessage());
        } catch (SocketTimeoutException ste) {
            response.setResultCode(Response.SocketTimeoutException);
            response.setErrorInfo(ste.getMessage());
        } catch (ClientProtocolException e) {
            response.setResultCode(Response.ClientProtocolException);
            response.setErrorInfo(e.getMessage());
        } catch (IOException e) {
            response.setResultCode(Response.IOException);
            response.setErrorInfo(e.getMessage());
        } catch (Exception e) {
            response.setResultCode(Response.Exception);
            response.setErrorInfo(e.getMessage());
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
            if (respEntity != null) {
                try {
                    EntityUtils.consume(respEntity);
                } catch (IOException e) {
                }
            }
        }

        return response;
    }

    public CookieStore getCookieStore() {
        return httpContext.getCookieStore();
    }

    public void addCookie(Cookie cookie) {
        getCookieStore().addCookie(cookie);
    }

    public void addCookie(String name, String value) {
        getCookieStore().addCookie(new BasicClientCookie(name, value));
    }

    public void clearCookie() {
        getCookieStore().clear();
    }

    public RequestConfig getDefaultRequestConfig() {
        return defaultRequestConfig;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public HttpClientContext getHttpContext() {
        return httpContext;
    }

    public HttpClientConnectionManager getConnManager() {
        return connManager;
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public int getMaxConnectionTotal() {
        return maxConnectionTotal;
    }

    public void setMaxConnectionTotal(int maxConnectionTotal) {
        this.maxConnectionTotal = maxConnectionTotal;
    }

    public int getMaxConnectionPerRoute() {
        return maxConnectionPerRoute;
    }

    public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
        this.maxConnectionPerRoute = maxConnectionPerRoute;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public HttpHost getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(HttpHost httpProxy) {
        this.httpProxy = httpProxy;
    }

    public static class Response {
        public static final int Success = 0;
        public static final int Redirect = 1;
        public static final int ServerError = 2;
        public static final int ConnectionPoolTimeoutException = 3;
        public static final int ConnectTimeoutException = 4;
        public static final int SocketTimeoutException = 5;
        public static final int ClientProtocolException = 6;
        public static final int IOException = 7;
        public static final int Exception = 8;

        public String encoding;
        public int statusCode;
        public int resultCode;
        public String errorInfo;
        public byte[] content;
        public Header[] headers;

        public Response() {

        }

        public Response(String encoding) {
            this.encoding = encoding;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public String getErrorInfo() {
            return errorInfo;
        }

        public void setErrorInfo(String errorInfo) {
            this.errorInfo = errorInfo;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        @Override
        public String toString() {
            if (content == null)
                return "";

            return new String(content, Charset.forName(encoding));
        }

        public boolean isSuccess() {
            return resultCode == Response.Success;
        }

        public boolean isNoError() {
            return resultCode == Response.Success ||
                    resultCode == Response.Redirect;
        }

        public boolean isNoExecption() {
            return resultCode == Response.Success ||
                    resultCode == Response.Redirect ||
                    resultCode == Response.ServerError;
        }
    }

    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
            }
        }
    }

}
