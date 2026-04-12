package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * 会话简要信息 VO
 *
 * @author FantasticName
 */
public class ConversationVO {
    /**
     * 会话 ID
     */
    private String id;
    /**
     * 关联的商品 ID
     */
    private String listingId;
    /**
     * 商品标题
     */
    private String listingTitle;
    /**
     * 对方用户 ID
     */
    private String peerUserId;
    /**
     * 对方用户昵称
     */
    private String peerNickname;
    /**
     * 对方用户头像 URL
     */
    private String peerAvatarUrl;
    /**
     * 最后更新时间
     */
    private String updatedAt;

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

    public String getListingTitle() {
        return listingTitle;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public String getPeerUserId() {
        return peerUserId;
    }

    public void setPeerUserId(String peerUserId) {
        this.peerUserId = peerUserId;
    }

    public String getPeerNickname() {
        return peerNickname;
    }

    public void setPeerNickname(String peerNickname) {
        this.peerNickname = peerNickname;
    }

    public String getPeerAvatarUrl() {
        return peerAvatarUrl;
    }

    public void setPeerAvatarUrl(String peerAvatarUrl) {
        this.peerAvatarUrl = peerAvatarUrl;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

