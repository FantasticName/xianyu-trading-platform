package io.github.fantasticname.xianyutradingplatform.filter;

import io.github.fantasticname.xianyutradingplatform.util.AppConfig;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author FantasticName
 */
/**
 * CORS 过滤器：为浏览器跨域请求设置响应头，并处理预检（OPTIONS）请求。
 *
 * <p>允许的 Origin 来源从配置 {@code cors.allowedOrigins} 读取，多个值用逗号分隔。</p>
 *
 * <p>行为说明：</p>
 * - 若请求携带 {@code Origin} 且在白名单中，则回显该 Origin（并设置 {@code Vary: Origin}）；<br>
 * - 固定声明允许的方法、请求头与预检缓存时间；<br>
 * - 对 OPTIONS 预检请求直接返回 204，避免进入业务链路。
 *
 * @author FantasticName
 */
public class CorsFilter implements Filter {
    private final Set<String> allowedOrigins = new HashSet<>();

    public CorsFilter() {
        // 1) 从配置读取允许的 Origin 列表，构建白名单集合以便 O(1) 判断。
        String v = AppConfig.getInstance().getString("cors.allowedOrigins", "");
        for (String s : v.split(",")) {
            String t = s.trim();
            if (!t.isBlank()) {
                allowedOrigins.add(t);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 2) 仅处理 HTTP 请求/响应；其他场景直接放行。
        if (!(request instanceof HttpServletRequest req) || !(response instanceof HttpServletResponse res)) {
            chain.doFilter(request, response);
            return;
        }

        // 3) 按白名单回显 Origin，允许浏览器携带凭证/发起跨域请求（此处为最小实现：只做白名单校验与回显）。
        String origin = req.getHeader("Origin");
        if (origin != null && allowedOrigins.contains(origin)) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Vary", "Origin");
        }
        // 4) 声明允许的方法、请求头与预检缓存时间。
        res.setHeader("Access-Control-Allow-Methods", "GET,POST,PATCH,PUT,DELETE,OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Trace-Id");
        res.setHeader("Access-Control-Max-Age", "86400");

        // 5) 预检请求不进入业务：直接返回 204（No Content）。
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(204);
            return;
        }

        // 6) 非预检请求继续后续链路。
        chain.doFilter(request, response);
    }
}

