package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.CreateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateListingRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingDetailVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务接口
 * 处理商品的发布、查询、搜索、推荐、修改及删除等核心业务。
 *
 * @author FantasticName
 */
public interface ListingService {
    /**
     * 发布新商品
     *
     * @param userId  发布者用户ID
     * @param request 商品发布请求信息DTO
     * @return 发布后的商品简要信息 VO
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果参数不合法
     */
    ListingVO create(String userId, CreateListingRequest request);

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 包含卖家信息、描述、图片列表等完整信息的 VO
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果商品不存在
     */
    ListingDetailVO getDetail(String id);

    /**
     * 搜索商品
     * 支持关键字、分类、价格范围过滤及分页。
     *
     * @param keyword  搜索关键字（标题模糊匹配）
     * @param category 分类过滤
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param page     页码（从1开始）
     * @param pageSize 每页条数
     * @return 匹配的商品列表
     */
    List<ListingVO> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, int page, int pageSize);

    /**
     * 推荐商品
     * 当前实现为纯随机推荐。
     *
     * @param limit 推荐数量
     * @return 推荐的商品列表
     */
    List<ListingVO> recommend(int limit);

    /**
     * 获取我发布的商品列表
     *
     * @param userId 当前用户ID
     * @param limit  最大返回数量
     * @return 商品列表
     */
    List<ListingVO> listMine(String userId, int limit);

    /**
     * 修改商品信息
     *
     * @param userId  当前操作用户ID（必须是卖家本人）
     * @param id      商品ID
     * @param request 商品更新请求信息（仅更新非空字段）
     * @return 更新后的商品简要信息 VO
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果商品不存在或非本人操作
     */
    ListingVO update(String userId, String id, UpdateListingRequest request);

    /**
     * 删除商品
     *
     * @param userId 当前操作用户ID（必须是卖家本人）
     * @param id      商品ID
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果商品不存在或非本人操作
     */
    void delete(String userId, String id);
}

