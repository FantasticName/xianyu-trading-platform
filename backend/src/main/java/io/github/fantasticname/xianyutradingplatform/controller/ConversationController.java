package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.service.ConversationService;
import io.github.fantasticname.xianyutradingplatform.service.dto.CreateConversationRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.SendMessageRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ConversationVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.MessageVO;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author FantasticName
 */
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    public void getOrCreate(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        CreateConversationRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), CreateConversationRequest.class);
        ConversationVO vo = conversationService.getOrCreate(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(vo, traceId(req)));
    }

    public void list(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 30);
        List<ConversationVO> list = conversationService.list(uid, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void listMessages(HttpServletRequest req, HttpServletResponse resp, String uid, String conversationId) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 100);
        List<MessageVO> list = conversationService.listMessages(uid, conversationId, limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void sendMessage(HttpServletRequest req, HttpServletResponse resp, String uid, String conversationId) throws IOException {
        SendMessageRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), SendMessageRequest.class);
        conversationService.sendMessage(uid, conversationId, r);
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

