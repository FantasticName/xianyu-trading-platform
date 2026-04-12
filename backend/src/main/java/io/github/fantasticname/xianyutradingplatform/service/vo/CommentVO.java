package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * 评论简要信息 VO
 *
 * @author FantasticName
 */
public class CommentVO {
    /**
     * 评论 ID
     */
    private String id;
    /**
     * 关联的商品 ID
     */
    private String listingId;
    /**
     * 评论者用户 ID
     */
    private String userId;
    /**
     * 评论者昵称
     */
    private String userNickname;
    /**
     * 评论者头像 URL
     */
    private String userAvatarUrl;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 创建时间
     */
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
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

