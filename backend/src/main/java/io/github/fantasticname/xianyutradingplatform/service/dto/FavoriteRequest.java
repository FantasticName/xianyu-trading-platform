package io.github.fantasticname.xianyutradingplatform.service.dto;

/**
 * 收藏商品请求 DTO
 *
 * @author FantasticName
 */
public class FavoriteRequest {
    /**
     * 商品 ID
     */
    private String listingId;

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }
}

