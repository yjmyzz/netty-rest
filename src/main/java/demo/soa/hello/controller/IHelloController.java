package demo.soa.hello.controller;

import demo.soa.misc.controller.IController;
import demo.soa.misc.req.ServiceRequest;
import demo.soa.misc.resp.NormalReturn;

/**
 * {type your description }
 *
 * @since: 15/10/30.
 * @author: yangjunming
 */
public interface IHelloController extends IController {

    NormalReturn hello(ServiceRequest request);
}
