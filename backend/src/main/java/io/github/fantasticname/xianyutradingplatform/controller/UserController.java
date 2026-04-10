package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.service.UserService;
import io.github.fantasticname.xianyutradingplatform.service.ListingService;
import io.github.fantasticname.xianyutradingplatform.service.SocialService;
import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateMeRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.MeVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author FantasticName
 */
public class UserController {
    private final UserService userService;
    private final ListingService listingService;
    private final SocialService socialService;

    public UserController(UserService userService, ListingService listingService, SocialService socialService) {
        this.userService = userService;
        this.listingService = listingService;
        this.socialService = socialService;
    }

    public void getMe(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        MeVO me = userService.getMe(uid);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(me, traceId(req)));
    }

    public void updateMe(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        UpdateMeRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), UpdateMeRequest.class);
        MeVO me = userService.updateMe(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(me, traceId(req)));
    }

    public void searchUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        int limit = parseInt(req.getParameter("limit"), 20);
        List<UserPublicVO> list = userService.searchUsers(keyword, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void listMyListings(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 50);
        List<ListingVO> list = listingService.listMine(uid, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void listMyFollows(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 50);
        List<UserPublicVO> list = socialService.listFollows(uid, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void listMyFavorites(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 50);
        List<ListingVO> list = socialService.listFavorites(uid, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
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

