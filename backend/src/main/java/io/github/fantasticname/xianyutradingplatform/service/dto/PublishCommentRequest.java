package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 发布评论请求 DTO
 *
 * @author FantasticName
 */
public class PublishCommentRequest {
    /**
     * 评论内容
     */
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

