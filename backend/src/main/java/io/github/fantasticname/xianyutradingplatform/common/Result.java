package io.github.fantasticname.xianyutradingplatform.common;

/**
 * @author FantasticName
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> ok(T data, String traceId) {
        Result<T> r = new Result<>();
        r.code = ErrorCode.OK.getCode();
        r.message = ErrorCode.OK.getDefaultMessage();
        r.data = data;
        r.traceId = traceId;
        return r;
    }

    public static <T> Result<T> fail(ErrorCode errorCode, String message, String traceId) {
        Result<T> r = new Result<>();
        r.code = errorCode.getCode();
        r.message = message == null || message.isBlank() ? errorCode.getDefaultMessage() : message;
        r.data = null;
        r.traceId = traceId;
        return r;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}

