package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * @author FantasticName
 */
public class UserPublicVO {
    private String id;
    private String nickname;
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

