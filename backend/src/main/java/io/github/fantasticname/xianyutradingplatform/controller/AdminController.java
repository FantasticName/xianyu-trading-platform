package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author FantasticName
 */
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public void deleteListing(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        String listingId = req.getParameter("listingId");
        if (listingId == null || listingId.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "listingId 不能为空");
        }
        adminService.deleteListing(uid, listingId.trim());
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    public void deleteComment(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        String commentId = req.getParameter("commentId");
        if (commentId == null || commentId.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "commentId 不能为空");
        }
        adminService.deleteComment(uid, commentId.trim());
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(null, traceId(req)));
    }

    private String traceId(HttpServletRequest req) {
        Object tid = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
        return tid == null ? null : String.valueOf(tid);
    }
}

