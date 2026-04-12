package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.filter.JwtAuthFilter;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.util.AppContext;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * API 总入口 Servlet：承接所有 {@code /api/*} 请求，并基于 {@code pathInfo + HTTP method} 做路由分发。
 *
 * <p>该项目禁用 SSM/Spring MVC，因此这里承担了“控制器映射/参数提取/错误边界”这类框架职责：</p>
 * - 解析请求路径与方法，匹配到具体的 Controller 方法；<br>
 * - 对需要登录的接口提取 uid（由 {@link JwtAuthFilter} 注入到 request attribute）；<br>
 * - 通过 {@link #writeJson(HttpServletRequest, HttpServletResponse, int, Object)} 输出统一 JSON 响应格式；<br>
 * - 对未知路径抛出 {@link BusinessException} 触发统一异常处理链路。
 *
 * <p>线程模型：Servlet 是单例并发调用的；本类不在实例字段中保存请求级状态，保证线程安全。</p>
 *
 * @author FantasticName
 */
public class ApiDispatcherServlet extends HttpServlet {
    private transient AppContext app;

    @Override
    public void init() {
        // 容器启动时完成“手写依赖注入”容器的初始化/获取，后续每个请求复用同一个 AppContext。
        this.app = AppContext.getInstance();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1) 获取 /api/* 之后的 pathInfo（例如 /auth/login）。缺失则视为找不到资源。
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 2) 根据 HTTP 方法 + pathInfo 做硬编码路由分发。
        //    说明：为了保持实现简单，这里使用 if-else 链而不是引入路由框架。
        String method = req.getMethod();
        if ("/auth/register".equals(pathInfo) && "POST".equalsIgnoreCase(method)) {
            app.getAuthController().register(req, resp);
            return;
        }
        if ("/auth/login".equals(pathInfo) && "POST".equalsIgnoreCase(method)) {
            app.getAuthController().login(req, resp);
            return;
        }
        if ("/me".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getUserController().getMe(req, resp, requireUid(req));
            return;
        }
        if ("/me".equals(pathInfo) && "PATCH".equalsIgnoreCase(method)) {
            app.getUserController().updateMe(req, resp, requireUid(req));
            return;
        }

        if ("/me/listings".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getUserController().listMyListings(req, resp, requireUid(req));
            return;
        }

        if ("/me/follows".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getUserController().listMyFollows(req, resp, requireUid(req));
            return;
        }

        if ("/me/favorites".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getUserController().listMyFavorites(req, resp, requireUid(req));
            return;
        }

        if ("/users".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getUserController().searchUsers(req, resp);
            return;
        }

        if ("/listings".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getListingController().search(req, resp);
            return;
        }
        if ("/listings/recommend".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getListingController().recommend(req, resp);
            return;
        }
        if ("/listings".equals(pathInfo) && "POST".equalsIgnoreCase(method)) {
            app.getListingController().create(req, resp, requireUid(req));
            return;
        }
        if (pathInfo.startsWith("/listings/") && pathInfo.endsWith("/comments") && "GET".equalsIgnoreCase(method)) {
            String listingId = pathInfo.substring("/listings/".length(), pathInfo.length() - "/comments".length());
            app.getSocialController().listComments(req, resp, listingId);
            return;
        }
        if (pathInfo.startsWith("/listings/") && pathInfo.endsWith("/comments") && "POST".equalsIgnoreCase(method)) {
            String listingId = pathInfo.substring("/listings/".length(), pathInfo.length() - "/comments".length());
            app.getSocialController().addComment(req, resp, requireUid(req), listingId);
            return;
        }

        if (pathInfo.startsWith("/listings/") && "GET".equalsIgnoreCase(method)) {
            String id = pathInfo.substring("/listings/".length());
            app.getListingController().getDetail(req, resp, id);
            return;
        }
        if (pathInfo.startsWith("/listings/") && "PATCH".equalsIgnoreCase(method)) {
            String id = pathInfo.substring("/listings/".length());
            app.getListingController().update(req, resp, requireUid(req), id);
            return;
        }
        if (pathInfo.startsWith("/listings/") && "DELETE".equalsIgnoreCase(method)) {
            String id = pathInfo.substring("/listings/".length());
            app.getListingController().delete(req, resp, requireUid(req), id);
            return;
        }

        if ("/follows".equals(pathInfo) && "POST".equalsIgnoreCase(method)) {
            app.getSocialController().follow(req, resp, requireUid(req));
            return;
        }
        if ("/follows".equals(pathInfo) && "DELETE".equalsIgnoreCase(method)) {
            app.getSocialController().unfollow(req, resp, requireUid(req));
            return;
        }
        if ("/favorites".equals(pathInfo) && "POST".equalsIgnoreCase(method)) {
            app.getSocialController().favorite(req, resp, requireUid(req));
            return;
        }
        if ("/favorites".equals(pathInfo) && "DELETE".equalsIgnoreCase(method)) {
            app.getSocialController().unfavorite(req, resp, requireUid(req));
            return;
        }
        if (pathInfo.startsWith("/comments/") && "DELETE".equalsIgnoreCase(method)) {
            String commentId = pathInfo.substring("/comments/".length());
            app.getSocialController().deleteComment(req, resp, requireUid(req), commentId);
            return;
        }

        if ("/conversations".equals(pathInfo) && "POST".equalsIgnoreCase(method)) {
            app.getConversationController().getOrCreate(req, resp, requireUid(req));
            return;
        }
        if ("/conversations".equals(pathInfo) && "GET".equalsIgnoreCase(method)) {
            app.getConversationController().list(req, resp, requireUid(req));
            return;
        }
        if (pathInfo.startsWith("/conversations/") && pathInfo.endsWith("/messages") && "GET".equalsIgnoreCase(method)) {
            String conversationId = pathInfo.substring("/conversations/".length(), pathInfo.length() - "/messages".length());
            app.getConversationController().listMessages(req, resp, requireUid(req), conversationId);
            return;
        }
        if (pathInfo.startsWith("/conversations/") && pathInfo.endsWith("/messages") && "POST".equalsIgnoreCase(method)) {
            String conversationId = pathInfo.substring("/conversations/".length(), pathInfo.length() - "/messages".length());
            app.getConversationController().sendMessage(req, resp, requireUid(req), conversationId);
            return;
        }

        if ("/admin/listings".equals(pathInfo) && "DELETE".equalsIgnoreCase(method)) {
            app.getAdminController().deleteListing(req, resp, requireUid(req));
            return;
        }
        if ("/admin/comments".equals(pathInfo) && "DELETE".equalsIgnoreCase(method)) {
            app.getAdminController().deleteComment(req, resp, requireUid(req));
            return;
        }

        // 3) 未命中任何路由：统一视为 404，由异常处理链路转成标准 JSON 响应。
        throw new BusinessException(ErrorCode.NOT_FOUND);
    }

    /**
     * 从请求上下文中获取当前登录用户 id。
     *
     * <p>该值由 {@link JwtAuthFilter} 在鉴权通过后写入 {@link jakarta.servlet.ServletRequest#setAttribute}。</p>
     *
     * <p>使用方式：</p>
     * - 对“必须登录”的接口，在进入 Controller 前调用该方法；<br>
     * - 若不存在 uid，则抛出未登录异常，让异常处理链路返回 401。
     *
     * @param req HTTP 请求
     * @return 当前登录用户 id（字符串形式）
     * @throws BusinessException 当请求未携带有效登录态时抛出 {@link ErrorCode#UNAUTHORIZED}
     */
    private String requireUid(HttpServletRequest req) {
        Object v = req.getAttribute(JwtAuthFilter.UID_ATTR);
        if (v == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return String.valueOf(v);
    }

    /**
     * 读取请求体为字符串。
     *
     * <p>注意事项：</p>
     * - 该项目以 JSON 请求为主，Controller 通常会先读取 body 再反序列化；<br>
     * - 若请求未显式指定编码，则默认按 UTF-8 解析。
     *
     * @param req HTTP 请求
     * @return 请求体字符串（可能为空字符串）
     * @throws IOException 读取请求输入流失败
     */
    public static String readBody(HttpServletRequest req) throws IOException {
        return new String(req.getInputStream().readAllBytes(), req.getCharacterEncoding() == null ? "UTF-8" : req.getCharacterEncoding());
    }

    /**
     * 输出 JSON 响应，并附带 TraceId（若请求上下文中存在）。
     *
     * <p>执行步骤：</p>
     * 1) 从 request attribute 读取 TraceId（由 {@link TraceIdFilter} 生成）；若存在则写入响应头。<br>
     * 2) 设置 HTTP 状态码与 Content-Type。<br>
     * 3) 将 body 序列化为 JSON 并写入响应。
     *
     * @param req HTTP 请求，用于读取 TraceId
     * @param resp HTTP 响应
     * @param status HTTP 状态码
     * @param body 响应体对象（会被序列化为 JSON）
     * @throws IOException 写响应失败
     */
    public static void writeJson(HttpServletRequest req, HttpServletResponse resp, int status, Object body) throws IOException {
        Object tid = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
        if (tid != null) {
            resp.setHeader(TraceIdFilter.TRACE_ID_HEADER, String.valueOf(tid));
        }
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JsonUtil.toJson(body));
    }
}

