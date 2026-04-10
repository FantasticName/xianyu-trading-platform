package io.github.fantasticname.xianyutradingplatform.common;

/**
 * @author FantasticName
 */
public enum ErrorCode {
    OK(0, "OK", 200),
    BAD_REQUEST(40000, "参数错误", 400),
    UNAUTHORIZED(40100, "未登录或登录已过期", 401),
    FORBIDDEN(40300, "无权限", 403),
    NOT_FOUND(40400, "资源不存在", 404),
    CONFLICT(40900, "资源冲突", 409),
    INTERNAL_ERROR(50000, "服务器内部错误", 500);

    private final int code;
    private final String defaultMessage;
    private final int httpStatus;

    ErrorCode(int code, String defaultMessage, int httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

