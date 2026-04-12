package io.github.fantasticname.xianyutradingplatform.service.vo;

import java.math.BigDecimal;

/**
 * 商品简要信息 VO
 *
 * @author FantasticName
 */
public class ListingVO {
    /**
     * 商品 ID
     */
    private String id;
    /**
     * 卖家用户 ID
     */
    private String sellerId;
    /**
     * 卖家昵称
     */
    private String sellerNickname;
    /**
     * 商品标题
     */
    private String title;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品成色
     */
    private String condition;
    /**
     * 商品分类
     */
    private String category;
    /**
     * 商品封面图 URL
     */
    private String coverUrl;
    /**
     * 商品状态
     */
    private String status;
    /**
     * 发布时间
     */
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerNickname() {
        return sellerNickname;
    }

    public void setSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

