package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 注册请求 DTO
 *
 * @author FantasticName
 */
public class RegisterRequest {
    /**
     * 注册账号
     */
    private String account;
    /**
     * 注册密码
     */
    private String password;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户角色（USER 或 ADMIN）
     */
    private String role;
    /**
     * 管理员邀请码（仅注册管理员时需要）
     */
    private String adminInviteCode;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdminInviteCode() {
        return adminInviteCode;
    }

    public void setAdminInviteCode(String adminInviteCode) {
        this.adminInviteCode = adminInviteCode;
    }
}

