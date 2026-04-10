package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * @author FantasticName
 */
public class RegisterRequest {
    private String account;
    private String password;
    private String nickname;
    private String role;
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

