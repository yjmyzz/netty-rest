package demo.soa.misc.handler;

import com.alibaba.fastjson.JSONObject;
import demo.soa.misc.common.exception.ServiceException;
import demo.soa.misc.common.server.Handler;
import demo.soa.misc.common.server.NettyHttpRequest;
import demo.soa.misc.common.server.resp.Resp;
import demo.soa.misc.common.util.CostTime;
import demo.soa.misc.controller.IController;
import demo.soa.misc.req.ServiceRequest;
import demo.soa.misc.resp.NormalResp;
import demo.soa.misc.resp.NormalReturn;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;

/**
 * request  body 为 json
 * response body 为 json
 *
 * @author huming
 */
public class BaseHandler implements Handler {
    protected IController adaptorController;

    protected static Logger log = LoggerFactory.getLogger(BaseHandler.class);

    public BaseHandler() {
        super();
    }

    public IController getAdaptorService() {
        return adaptorController;
    }

    @Override
    public Resp handleRequest(NettyHttpRequest request) {
        NormalResp resp = null;
        NormalReturn nr = new NormalReturn();
        CostTime costTime = new CostTime();
        costTime.start();
        try {
            ServiceRequest serviceReq = parseReq(request);
            JSONObject jsonObject = serviceReq.getJson();
            String body = null;
            if (jsonObject != null) {
                body = jsonObject.toJSONString();
            }

            if (log.isInfoEnabled())
                log.info(" [ REQUEST: {} ] -> {}", request.params(), body);

            nr = this.doAction(serviceReq, request);
        } catch (Exception e) {
            log.error("parse error:", e);
            nr.setMsg("服务器内部错误:" + e.getMessage());
            nr.setStatusCode("500");
        }

        nr.setCostTime(costTime.cost());

        if (log.isDebugEnabled()) {
            log.debug(" [ RESPONSE: {} ] -> {}", request.params(), JSONObject.toJSONString(nr, NormalResp.JSON_SERIAL_FEATURES));
        }

        resp = new NormalResp(nr);
        return resp;
    }

    protected ServiceRequest parseReq(NettyHttpRequest request) {
        ServiceRequest serviceReq = new ServiceRequest();
        String content = request.contentAsString();
        if (StringUtils.startsWith(content, "finddata=")) {
            content = StringUtils.removeStart(content, "finddata=");
            try {
                content = URLDecoder.decode(content, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.warn("", e);
            }
        }
        JSONObject json = JSONObject.parseObject(content);
        serviceReq.setJson(json);
        serviceReq.setRequest(request);
        serviceReq.setMethodName(request.param("method"));
        return serviceReq;
    }

    protected NormalReturn doAction(ServiceRequest serviceReq, NettyHttpRequest request) {
        NormalReturn nr = null;
        Method method = null;
        try {
            method = adaptorController.getClass().getMethod(serviceReq.getMethodName(), ServiceRequest.class);
            if (method != null) {
                nr = (NormalReturn) method.invoke(adaptorController, serviceReq);
            } else {
                nr = new NormalReturn();
                nr.setStatusCode(ServiceException.GENERAL_ERROR);
                nr.setMsg(String.format("方法%s未找到", serviceReq.getMethodName()));
            }
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                Exception cause = (Exception) e.getCause();
                if (cause != null) e = cause;
            }
            if (e instanceof ServiceException) {
                nr = new NormalReturn();
                nr.setStatusCode(((ServiceException) e).getStatusCode());
                nr.setMsg(e.getMessage());
            } else {
                log.error(method.toString(), e);
                throw new RuntimeException("API General Error: " + e.getMessage(), e);
            }
        }
        return nr;
    }
}
