package io.github.fantasticname.xianyutradingplatform.controller;

import io.github.fantasticname.xianyutradingplatform.common.Result;
import io.github.fantasticname.xianyutradingplatform.filter.TraceIdFilter;
import io.github.fantasticname.xianyutradingplatform.service.ListingService;
import io.github.fantasticname.xianyutradingplatform.service.dto.CreateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingDetailVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author FantasticName
 */
public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    public void search(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        String category = req.getParameter("category");
        BigDecimal minPrice = parseDecimal(req.getParameter("minPrice"));
        BigDecimal maxPrice = parseDecimal(req.getParameter("maxPrice"));
        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 20);
        List<ListingVO> list = listingService.search(keyword, category, minPrice, maxPrice, page, pageSize);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void recommend(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int limit = parseInt(req.getParameter("limit"), 20);
        List<ListingVO> list = listingService.recommend(limit);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(list, traceId(req)));
    }

    public void create(HttpServletRequest req, HttpServletResponse resp, String uid) throws IOException {
        CreateListingRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), CreateListingRequest.class);
        ListingVO vo = listingService.create(uid, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(vo, traceId(req)));
    }

    public void getDetail(HttpServletRequest req, HttpServletResponse resp, String id) throws IOException {
        ListingDetailVO vo = listingService.getDetail(id);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(vo, traceId(req)));
    }

    public void update(HttpServletRequest req, HttpServletResponse resp, String uid, String id) throws IOException {
        UpdateListingRequest r = JsonUtil.fromJson(ApiDispatcherServlet.readBody(req), UpdateListingRequest.class);
        ListingVO vo = listingService.update(uid, id, r);
        ApiDispatcherServlet.writeJson(req, resp, 200, Result.ok(vo, traceId(req)));
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp, String uid, String id) throws IOException {
        listingService.delete(uid, id);
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

    private BigDecimal parseDecimal(String v) {
        try {
            if (v == null || v.isBlank()) {
                return null;
            }
            return new BigDecimal(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String traceId(HttpServletRequest req) {
        Object tid = req.getAttribute(TraceIdFilter.TRACE_ID_ATTR);
        return tid == null ? null : String.valueOf(tid);
    }
}

