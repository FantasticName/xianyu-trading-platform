package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * 认证令牌 VO
 *
 * @author FantasticName
 */
public class AuthTokenVO {
    /**
     * JWT 访问令牌
     */
    private String token;

    public AuthTokenVO() {
    }

    public AuthTokenVO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

