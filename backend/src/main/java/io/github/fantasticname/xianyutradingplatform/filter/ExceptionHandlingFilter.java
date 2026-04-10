package io.github.fantasticname.xianyutradingplatform.filter;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author FantasticName
 */
/**
 * 统一异常处理过滤器：将后续链路抛出的异常转换为统一的 JSON 错误响应。
 *
 * <p>该过滤器通常位于路由分发（Servlet）之前或之后，用于兜底处理：</p>
 * - {@link BusinessException}：业务可预期错误，按 {@link ErrorCode} 映射的 HTTP 状态码返回；<br>
 * - {@link IllegalArgumentException}：参数不合法，映射为 400；<br>
 * - 其他异常：记录日志并返回 500，避免异常堆栈泄露给前端。
 *
 * <p>响应体结构使用 {@link Result}，并携带 TraceId（若 {@link TraceIdFilter} 已生成）。</p>
 *
 * @author FantasticName
 */
public class ExceptionHandlingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1) 仅处理 HTTP 请求/响应；非 HTTP 场景直接交给后续链路。
        if (!(request instanceof HttpServletRequest req) || !(response instanceof HttpServletResponse res)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 2) 执行后续过滤器链与业务处理。
            chain.doFilter(request, response);
        } catch (BusinessException e) {
            // 3) 业务异常：按错误码决定 HTTP 状态，并输出标准错误响应。
            writeError(req, res, e.getErrorCode(), e.getMessage(), null);
        } catch (IllegalArgumentException e) {
            // 4) 参数校验失败：统一作为 400 返回。
            writeError(req, res, ErrorCode.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            // 5) 兜底异常：记录堆栈，避免静默失败；返回 500。
            log.error("Unhandled exception", e);
            writeError(req, res, ErrorCode.INTERNAL_ERROR, null, null);
        }
    }

    /**
     * 写入标准错误响应。
     *
     * <p>执行步骤：</p>
     * 1) 从请求上下文读取 TraceId（若存在），用于前后端/日志的链路对齐；<br>
     * 2) 设置 HTTP 状态码与 JSON Content-Type；<br>
     * 3) 将 {@link Result#fail(ErrorCode, String, String)} 序列化并写入响应。
     *
     * @param req HTTP 请求（用于读取 TraceId）
     * @param res HTTP 响应
     * @param errorCode 业务错误码（包含对应的 HTTP 状态）
     * @param message 可选的错误信息（为 {@code null} 时由前端按错误码展示默认文案）
     * @param data 预留字段（当前未使用）
     * @throws IOException 写响应失败
     */
    private void writeError(HttpServletRequest req, HttpServletResponse res, ErrorCode errorCode, String message, Object data) throws IOException {
        Object tid = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
        String traceId = tid == null ? null : String.valueOf(tid);
        res.setStatus(errorCode.getHttpStatus());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(JsonUtil.toJson(Result.fail(errorCode, message, traceId)));
    }
}

