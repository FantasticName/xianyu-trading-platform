package io.github.fantasticname.xianyutradingplatform.service.vo;

/**
 * @author FantasticName
 */
public class AuthTokenVO {
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

