package io.github.fantasticname.xianyutradingplatform.filter;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.util.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author FantasticName
 */
/**
 * JWT 鉴权过滤器：对 {@code /api/*} 请求执行登录态校验，并将当前用户 id 注入到请求上下文。
 *
 * <p>过滤规则：</p>
 * - 非 {@code /api/*} 路径：直接放行；<br>
 * - 公共 API：直接放行（如登录注册、商品浏览、用户搜索等）；<br>
 * - 受保护 API：要求请求头 {@code Authorization: Bearer <token>}，校验 token 并提取 uid。
 *
 * <p>通过校验后，本过滤器会把 uid 写入 request attribute（key 为 {@link #UID_ATTR}），
 * 供 {@code ApiDispatcherServlet} 在分发前提取并传入 Controller。</p>
 *
 * @author FantasticName
 */
public class JwtAuthFilter implements Filter {
    public static final String UID_ATTR = "uid";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1) 仅对 HTTP 请求做处理；非 HTTP 请求直接放行。
        if (!(request instanceof HttpServletRequest req)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) 计算“去掉 contextPath 的真实路径”，确保在不同部署路径下匹配规则一致。
        String path = req.getRequestURI();
        String ctx = req.getContextPath();
        if (ctx != null && !ctx.isBlank() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        // 3) 仅保护 /api/*，其他静态资源或页面请求不参与鉴权。
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // 4) 对公共接口放行：例如 auth 相关、GET listings 等无需登录即可访问的接口。
        String apiPath = path.substring("/api".length());
        if (isPublic(apiPath, req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 5) 读取并校验 Authorization 头：必须是 Bearer 方案。
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        // 6) 解析 token 并通过 JwtUtil 验签；提取 uid 作为当前用户身份。
        String token = auth.substring("Bearer ".length()).trim();
        String uid = JwtUtil.getInstance().verifyAndGetUserId(token);
        if (uid == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        // 7) 将 uid 注入请求上下文，供后续分发/控制器使用。
        request.setAttribute(UID_ATTR, uid);
        // 8) 继续过滤器链。
        chain.doFilter(request, response);
    }

    /**
     * 判断某个 API 是否为公共接口（无需登录即可访问）。
     *
     * <p>公共接口的定义应与 {@code ApiDispatcherServlet} 的路由保持一致；一旦新增/调整路由，
     * 需要同步更新该规则，否则可能出现“应当放行却被拦截”或“应当保护却被放行”的问题。</p>
     *
     * @param apiPath {@code /api} 之后的路径（以 {@code /} 开头）
     * @param method HTTP 方法
     * @return {@code true} 表示公共接口；{@code false} 表示需要鉴权
     */
    private boolean isPublic(String apiPath, String method) {
        if (apiPath.startsWith("/auth/")) {
            return true;
        }

        if ("GET".equalsIgnoreCase(method) && apiPath.equals("/users")) {
            return true;
        }

        if ("GET".equalsIgnoreCase(method) && (apiPath.equals("/listings") || apiPath.startsWith("/listings/") || apiPath.equals("/listings/recommend"))) {
            return true;
        }

        return false;
    }
}

