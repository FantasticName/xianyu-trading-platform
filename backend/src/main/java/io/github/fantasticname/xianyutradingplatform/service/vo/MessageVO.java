package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * 消息简要信息 VO
 *
 * @author FantasticName
 */
public class MessageVO {
    /**
     * 消息 ID
     */
    private String id;
    /**
     * 所属会话 ID
     */
    private String conversationId;
    /**
     * 发送者用户 ID
     */
    private String senderId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 发送时间
     */
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

