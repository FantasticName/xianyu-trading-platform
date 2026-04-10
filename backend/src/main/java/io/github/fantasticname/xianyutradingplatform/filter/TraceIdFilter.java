package io.github.fantasticname.xianyutradingplatform.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * @author FantasticName
 */
/**
 * TraceId 过滤器：为每个请求生成全局唯一的追踪标识，用于日志与前后端排障对齐。
 *
 * <p>生成的 TraceId 会：</p>
 * - 写入 request attribute（key 为 {@link #TRACE_ID_ATTR}），供后续组件读取；<br>
 * - 写入响应头（header 为 {@link #TRACE_ID_HEADER}），便于前端/调用方回传与定位问题。
 *
 * <p>该过滤器应尽量放在链路最前端，以确保后续异常处理与日志记录都能获取到 TraceId。</p>
 *
 * @author FantasticName
 */
public class TraceIdFilter implements Filter {
    public static final String TRACE_ID_ATTR = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1) 生成随机 TraceId（去掉 '-' 以缩短长度），并写入请求上下文。
        String traceId = UUID.randomUUID().toString().replace("-", "");
        request.setAttribute(TRACE_ID_ATTR, traceId);
        // 2) 若响应为 HTTP，则同时把 TraceId 写入响应头，便于调用方关联本次请求。
        if (response instanceof HttpServletResponse r) {
            r.setHeader(TRACE_ID_HEADER, traceId);
        }
        // 3) 继续过滤器链。
        chain.doFilter(request, response);
    }
}

