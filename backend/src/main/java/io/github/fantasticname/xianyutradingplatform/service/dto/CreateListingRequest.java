package io.github.fantasticname.xianyutradingplatform.service.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发布商品请求 DTO
 *
 * @author FantasticName
 */
public class CreateListingRequest {
    /**
     * 商品标题
     */
    private String title;
    /**
     * 商品分类
     */
    private String category;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品成色（如：全新、九成新等）
     */
    private String condition;
    /**
     * 商品详细描述
     */
    private String description;
    /**
     * 商品图片 URL 列表
     */
    private List<String> imageUrls;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}

