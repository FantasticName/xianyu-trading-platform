package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * 用户公开信息 VO
 *
 * @author FantasticName
 */
public class UserPublicVO {
    /**
     * 用户 ID
     */
    private String id;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像 URL
     */
    private String avatarUrl;

    public UserPublicVO() {
    }

    public UserPublicVO(String id, String nickname, String avatarUrl) {
        this.id = id;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

