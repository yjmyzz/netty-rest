package demo.soa.hello.controller.impl;

import demo.soa.hello.contract.HelloRequestData;
import demo.soa.hello.controller.IHelloController;
import demo.soa.misc.aspect.contract.Contract;
import demo.soa.misc.req.ServiceRequest;
import demo.soa.misc.resp.NormalReturn;
import org.springframework.stereotype.Controller;

/**
 * {type your description }
 *
 * @since: 15/10/30.
 * @author: yangjunming
 */
@Controller("helloController")
public class HelloController implements IHelloController {

    @Override
    @Contract(HelloRequestData.class)
    public NormalReturn hello(ServiceRequest request) {
        HelloRequestData requestData = request.getContract();
        requestData.setMessageContent("收到了!");
        return new NormalReturn(requestData);
    }
}
