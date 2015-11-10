package demo.soa.misc.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTool {

    public static String get(String url, String encoding) throws IOException {
        String content = null;
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        if (encoding == null || encoding.length() == 0) {
            encoding = "UTF-8";
        }
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("Content-Type", "application/json;charset=" + encoding);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000)
                    .setSocketTimeout(30000).setConnectionRequestTimeout(30000).build();

            httpget.setConfig(requestConfig);

            HttpResponse response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                content = EntityUtils.toString(entity, encoding);
            }
            return content;
        } finally {
            httpclient.close();
        }
    }

    public static String post(String url, String encoding, String body) throws ClientProtocolException, IOException {
        String content = null;
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        if (encoding == null || encoding.length() == 0) {
            encoding = "UTF-8";
        }

        try {
            HttpPost httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000)
                    .setSocketTimeout(30000).setConnectionRequestTimeout(1000).build();

            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

            if (body != null) {
                StringEntity entity = new StringEntity(body, encoding);
                httpPost.setEntity(entity);
            }

            HttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity repEntity = response.getEntity();
                content = EntityUtils.toString(repEntity, encoding);
            }
            return content;
        } finally {
            httpclient.close();
        }
    }

    public static String postSoap(String url, String encoding, String body) throws ClientProtocolException, IOException {
        String content = null;
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        if (encoding == null || encoding.length() == 0) {
            encoding = "UTF-8";
        }

        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000)
                    .setSocketTimeout(30000).setConnectionRequestTimeout(1000).build();
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-Type", "application/soap+xml;charset=" + encoding);

            if (body != null) {
                StringEntity entity = new StringEntity(body, encoding);
                httpPost.setEntity(entity);
            }

            HttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity repEntity = response.getEntity();
                content = EntityUtils.toString(repEntity, encoding);
            }
            return content;
        } finally {
            httpclient.close();
        }
    }


    public static JSONObject postJson(String url, String encoding, Object data) throws ClientProtocolException, IOException {
        String json = data != null ? com.alibaba.fastjson.JSONObject.toJSONString(data, SerializerFeature.PrettyFormat) : null;
        String result = post(url, encoding, json);
        return result != null ? JSONObject.parseObject(result) : null;
    }

    public static JSONObject getJson(String url, String encoding) throws ClientProtocolException, IOException {
        String content = get(url, encoding);
        return content != null ? JSONObject.parseObject(content) : null;
    }

    public static String getRemortIP(HttpRequest request, Channel channel) {
        String ip = request.headers().get("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = ((InetSocketAddress) channel.getRemoteAddress()).getHostName();
        }
        return ip;
    }

    public static String getServerBaseUrl(HttpRequest request) {
        String serverUrl = "";
        if (request.headers().get("HTTP_X_FORWARDED_HOST") != null) {
            // explode the host list separated by comma and use the first host
            String[] hosts = request.headers().get("HTTP_X_FORWARDED_HOST").split(",");
            serverUrl = hosts[0];
        } else if (request.headers().get("HTTP_X_FORWARDED_SERVER") != null) {
            serverUrl = request.headers().get("HTTP_X_FORWARDED_SERVER");
        } else {

            if (request.headers().get("HTTP_HOST") == null) {
                serverUrl = request.headers().get("SERVER_NAME");
            } else {
                serverUrl = request.headers().get("HTTP_HOST");
            }
        }

        return serverUrl;
    }

    public static boolean isHttps(HttpRequest request) {
        return "on".equalsIgnoreCase(request.headers().get("HTTPS"));
    }
}
