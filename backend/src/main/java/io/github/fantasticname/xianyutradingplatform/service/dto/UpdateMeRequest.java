package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 更新个人资料请求 DTO
 *
 * @author FantasticName
 */
public class UpdateMeRequest {
    /**
     * 新昵称
     */
    private String nickname;
    /**
     * 新头像 URL
     */
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

