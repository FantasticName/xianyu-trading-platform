package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * 当前用户信息 VO
 *
 * @author FantasticName
 */
public class MeVO {
    /**
     * 用户 ID
     */
    private String id;
    /**
     * 用户账号
     */
    private String account;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像 URL
     */
    private String avatarUrl;
    /**
     * 用户角色
     */
    private String role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

