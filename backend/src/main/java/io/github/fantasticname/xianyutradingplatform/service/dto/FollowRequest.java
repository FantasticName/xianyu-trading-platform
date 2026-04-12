package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 关注用户请求 DTO
 *
 * @author FantasticName
 */
public class FollowRequest {
    /**
     * 目标卖家 ID
     */
    private String sellerId;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}

