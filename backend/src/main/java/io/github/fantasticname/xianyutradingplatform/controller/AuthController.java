package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.service.AuthService;
import io.github.fantasticname.xianyutradingplatform.service.dto.LoginRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.RegisterRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.AuthTokenVO;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author FantasticName
 */
/**
 * 认证 Controller：提供注册与登录接口的 HTTP 层适配。
 *
 * <p>职责边界：</p>
 * - 从 HTTP 请求中读取 JSON 请求体并反序列化为 DTO；<br>
 * - 调用 {@link AuthService} 执行核心业务逻辑；<br>
 * - 将业务结果包装为统一响应 {@link Result} 并写出为 JSON；<br>
 * - 透传 TraceId，方便前端排障与日志关联。
 *
 * <p>注意：异常不在本类捕获，由 {@code ExceptionHandlingFilter} 统一转换为错误响应。</p>
 *
 * @author FantasticName
 */
public class AuthController {
    private final AuthService authService;

    /**
     * 构造认证 Controller。
     *
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册。
     *
     * <p>处理步骤：</p>
     * 1) 从请求体读取 JSON，并反序列化为 {@link RegisterRequest}；<br>
     * 2) 调用 {@link AuthService#register(RegisterRequest)} 完成注册并生成 token；<br>
     * 3) 将 token 包装为 {@link Result#ok(Object, String)}，写出 200 JSON 响应。
     *
     * @param req HTTP 请求
     * @param resp HTTP 响应
     * @throws IOException 读取请求体或写响应失败
     */
    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1) 读取并解析请求体。
        RegisterRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), RegisterRequest.class);
        // 2) 执行业务注册逻辑，返回认证 token。
        AuthTokenVO token = authService.register(r);
        // 3) 写出统一 JSON 响应，并携带 TraceId。
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(token, traceId(req)));
    }

    /**
     * 用户登录。
     *
     * <p>处理步骤：</p>
     * 1) 从请求体读取 JSON，并反序列化为 {@link LoginRequest}；<br>
     * 2) 调用 {@link AuthService#login(LoginRequest)} 校验账号密码并生成 token；<br>
     * 3) 将 token 包装为统一响应并写出 200 JSON。
     *
     * @param req HTTP 请求
     * @param resp HTTP 响应
     * @throws IOException 读取请求体或写响应失败
     */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1) 读取并解析请求体。
        LoginRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), LoginRequest.class);
        // 2) 执行业务登录逻辑，返回认证 token。
        AuthTokenVO token = authService.login(r);
        // 3) 写出统一 JSON 响应，并携带 TraceId。
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(token, traceId(req)));
    }

    /**
     * 从请求上下文读取 TraceId。
     *
     * @param req HTTP 请求
     * @return TraceId；若链路未生成则返回 {@code null}
     */
    private String traceId(HttpServletRequest req) {
        Object tid = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
        return tid == null ? null : String.valueOf(tid);
    }
}

