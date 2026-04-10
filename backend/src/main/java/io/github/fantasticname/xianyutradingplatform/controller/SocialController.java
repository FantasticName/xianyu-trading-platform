package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.service.SocialService;
import io.github.fantasticname.xianyutradingplatform.service.dto.FavoriteRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.FollowRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.PublishCommentRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.CommentVO;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author FantasticName
 */
public class SocialController {
    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    public void follow(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        FollowRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), FollowRequest.class);
        socialService.follow(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    public void unfollow(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        FollowRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), FollowRequest.class);
        socialService.unfollow(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    public void favorite(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        FavoriteRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), FavoriteRequest.class);
        socialService.favorite(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    public void unfavorite(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        FavoriteRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), FavoriteRequest.class);
        socialService.unfavorite(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    public void listComments(HttpServletRequest req, HttpServletResponse resp, String listingId) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 50);
        List<CommentVO> list = socialService.listComments(listingId, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void addComment(HttpServletRequest req, HttpServletResponse resp, String uid, String listingId) throws IOException {
        PublishCommentRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), PublishCommentRequest.class);
        socialService.addComment(uid, listingId, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    public void deleteComment(HttpServletRequest req, HttpServletResponse resp, String uid, String commentId) throws IOException {
        socialService.deleteComment(uid, commentId);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    private int parseInt(String v, int def) {
        try {
            if (v == null || v.isBlank()) {
                return def;
            }
            return Integer.parseInt(v);
        } catch (Exception e) {
            return def;
        }
    }

    private String traceId(HttpServletRequest req) {
        Object tid = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
        return tid == null ? null : String.valueOf(tid);
    }
}

