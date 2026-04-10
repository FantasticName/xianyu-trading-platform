package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.CreateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingDetailVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author FantasticName
 */
public interface ListingService {
    ListingVO create(String userId, CreateListingRequest request);

    ListingDetailVO getDetail(String id);

    List<ListingVO> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, int page, int pageSize);

    List<ListingVO> recommend(int limit);

    List<ListingVO> listMine(String userId, int limit);

    ListingVO update(String userId, String id, UpdateListingRequest request);

    void delete(String userId, String id);
}

