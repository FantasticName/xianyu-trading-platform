package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * @author FantasticName
 */
public class UpdateMeRequest {
    private String nickname;
    private String avatarUrl;

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

