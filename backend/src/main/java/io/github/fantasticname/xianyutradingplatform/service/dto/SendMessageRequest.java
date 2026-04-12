package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 发送消息请求 DTO
 *
 * @author FantasticName
 */
public class SendMessageRequest {
    /**
     * 消息内容
     */
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

