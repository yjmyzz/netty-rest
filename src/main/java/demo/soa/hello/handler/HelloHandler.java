package demo.soa.hello.handler;

import demo.soa.misc.controller.IController;
import demo.soa.misc.handler.BaseHandler;
import demo.soa.misc.server.MyApplicationContext;
import demo.soa.hello.controller.IHelloController;

/**
 * {type your description }
 *
 * @since: 15/10/30.
 * @author: yangjunming
 */
public class HelloHandler extends BaseHandler {

    public HelloHandler(){
        this.adaptorController = (IController) MyApplicationContext.getInstance()
                .getBean("helloController", IHelloController.class);
    }
}
