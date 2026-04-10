package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * @author FantasticName
 */
public class ConversationVO {
    private String id;
    private String listingId;
    private String listingTitle;
    private String peerUserId;
    private String peerNickname;
    private String peerAvatarUrl;
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

