package io.github.fantasticname.xianyutradingplatform.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.dao.ListingDao;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.model.Listing;
import io.github.fantasticname.xianyutradingplatform.model.ListingCondition;
import io.github.fantasticname.xianyutradingplatform.model.ListingStatus;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.service.ListingService;
import io.github.fantasticname.xianyutradingplatform.service.dto.CreateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingDetailVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;
import io.github.fantasticname.xianyutradingplatform.util.JsonUtil;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;
import io.github.fantasticname.xianyutradingplatform.util.UuidUtil;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
public class ListingServiceImpl implements ListingService {
    private final DataSource ds;
    private final ListingDao listingDao;
    private final UserDao userDao;

    public ListingServiceImpl(DataSource ds, ListingDao listingDao, UserDao userDao) {
        this.ds = ds;
        this.listingDao = listingDao;
        this.userDao = userDao;
    }

    @Override
    public ListingVO create(String userId, CreateListingRequest request) {
        validateCreate(request);
        return TxManager.executeInTransaction(ds, () -> {
            LocalDateTime now = LocalDateTime.now();
            Listing l = new Listing();
            l.setId(UuidUtil.newUuid());
            l.setSellerId(userId);
            l.setTitle(request.getTitle().trim());
            l.setCategory(request.getCategory().trim());
            l.setPrice(request.getPrice());
            l.setCondition(parseCondition(request.getCondition()));
            l.setDescription(request.getDescription().trim());
            l.setStatus(ListingStatus.ACTIVE);
            List<String> images = request.getImageUrls() == null ? List.of() : request.getImageUrls();
            l.setImageUrlsJson(JsonUtil.toJson(images));
            l.setCoverUrl(images.isEmpty() ? null : images.get(0));
            l.setCreatedAt(now);
            l.setUpdatedAt(now);
            listingDao.insert(l);
            User seller = userDao.findById(userId);
            return toListingVO(l, seller);
        });
    }

    @Override
    public ListingDetailVO getDetail(String id) {
        Listing l = listingDao.findById(id);
        if (l == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        User seller = userDao.findById(l.getSellerId());
        ListingDetailVO vo = new ListingDetailVO();
        vo.setId(l.getId());
        vo.setSellerId(l.getSellerId());
        vo.setSellerNickname(seller == null ? null : seller.getNickname());
        vo.setSellerAvatarUrl(seller == null ? null : seller.getAvatarUrl());
        vo.setTitle(l.getTitle());
        vo.setPrice(l.getPrice());
        vo.setCondition(l.getCondition().name());
        vo.setCategory(l.getCategory());
        vo.setDescription(l.getDescription());
        vo.setCoverUrl(l.getCoverUrl());
        vo.setImageUrls(parseImageUrls(l.getImageUrlsJson()));
        vo.setStatus(l.getStatus().name());
        vo.setCreatedAt(l.getCreatedAt() == null ? null : l.getCreatedAt().toString());
        return vo;
    }

    @Override
    public List<ListingVO> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, int page, int pageSize) {
        int p = Math.max(page, 1);
        int ps = Math.min(Math.max(pageSize, 1), 50);
        int offset = (p - 1) * ps;
        List<Listing> listings = listingDao.search(keyword, category, minPrice, maxPrice, offset, ps);
        List<ListingVO> out = new ArrayList<>();
        for (Listing l : listings) {
            User seller = userDao.findById(l.getSellerId());
            out.add(toListingVO(l, seller));
        }
        return out;
    }

    @Override
    public List<ListingVO> recommend(int limit) {
        int l = Math.min(Math.max(limit, 1), 50);
        List<Listing> listings = listingDao.recommendRandom(l);
        List<ListingVO> out = new ArrayList<>();
        for (Listing it : listings) {
            User seller = userDao.findById(it.getSellerId());
            out.add(toListingVO(it, seller));
        }
        return out;
    }

    @Override
    public List<ListingVO> listMine(String userId, int limit) {
        int l = Math.min(Math.max(limit, 1), 100);
        List<Listing> list = listingDao.listBySeller(userId, l);
        User seller = userDao.findById(userId);
        List<ListingVO> out = new ArrayList<>();
        for (Listing it : list) {
            out.add(toListingVO(it, seller));
        }
        return out;
    }

    @Override
    public ListingVO update(String userId, String id, UpdateListingRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        return TxManager.executeInTransaction(ds, () -> {
            Listing existed = listingDao.findById(id);
            if (existed == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
            }
            if (!userId.equals(existed.getSellerId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改自己的商品");
            }
            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                existed.setTitle(request.getTitle().trim());
            }
            if (request.getCategory() != null && !request.getCategory().isBlank()) {
                existed.setCategory(request.getCategory().trim());
            }
            if (request.getPrice() != null) {
                existed.setPrice(request.getPrice());
            }
            if (request.getCondition() != null && !request.getCondition().isBlank()) {
                existed.setCondition(parseCondition(request.getCondition()));
            }
            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                existed.setDescription(request.getDescription().trim());
            }
            if (request.getImageUrls() != null) {
                existed.setImageUrlsJson(JsonUtil.toJson(request.getImageUrls()));
                existed.setCoverUrl(request.getImageUrls().isEmpty() ? null : request.getImageUrls().get(0));
            }
            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                existed.setStatus(ListingStatus.valueOf(request.getStatus().trim().toUpperCase()));
            }
            listingDao.update(existed);
            User seller = userDao.findById(userId);
            return toListingVO(existed, seller);
        });
    }

    @Override
    public void delete(String userId, String id) {
        TxManager.executeInTransaction(ds, () -> {
            Listing existed = listingDao.findById(id);
            if (existed == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
            }
            if (!userId.equals(existed.getSellerId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的商品");
            }
            listingDao.deleteById(id);
            return null;
        });
    }

    private void validateCreate(CreateListingRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "标题不能为空");
        }
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "分类不能为空");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "价格不合法");
        }
        if (request.getCondition() == null || request.getCondition().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "成色不能为空");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "描述不能为空");
        }
    }

    private ListingCondition parseCondition(String v) {
        String t = v.trim().toUpperCase();
        return ListingCondition.valueOf(t);
    }

    private List<String> parseImageUrls(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return JsonUtil.mapper().readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    private ListingVO toListingVO(Listing l, User seller) {
        ListingVO vo = new ListingVO();
        vo.setId(l.getId());
        vo.setSellerId(l.getSellerId());
        vo.setSellerNickname(seller == null ? null : seller.getNickname());
        vo.setTitle(l.getTitle());
        vo.setPrice(l.getPrice());
        vo.setCondition(l.getCondition().name());
        vo.setCategory(l.getCategory());
        vo.setCoverUrl(l.getCoverUrl());
        vo.setStatus(l.getStatus().name());
        vo.setCreatedAt(l.getCreatedAt() == null ? null : l.getCreatedAt().toString());
        return vo;
    }
}

