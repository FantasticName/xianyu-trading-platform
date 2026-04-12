package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 登录请求 DTO
 *
 * @author FantasticName
 */
public class LoginRequest {
    /**
     * 登录账号
     */
    private String account;
    /**
     * 登录密码
     */
    private String password;

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
}

