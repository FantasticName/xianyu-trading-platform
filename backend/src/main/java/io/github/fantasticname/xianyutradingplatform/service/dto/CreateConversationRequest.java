package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 创建会话请求 DTO
 *
 * @author FantasticName
 */
public class CreateConversationRequest {
    /**
     * 关联的商品 ID
     */
    private String listingId;

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }
}

