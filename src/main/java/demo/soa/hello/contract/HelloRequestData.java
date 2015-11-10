package demo.soa.hello.contract;

import javax.validation.constraints.NotNull;

/**
 * rest服务的request参数对象
 *
 * @since: 15/10/30.
 * @author: yangjunming
 */
public class HelloRequestData {

    @NotNull(message = "targetName不能为空")
    private String targetName;

    private String messageContent;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
