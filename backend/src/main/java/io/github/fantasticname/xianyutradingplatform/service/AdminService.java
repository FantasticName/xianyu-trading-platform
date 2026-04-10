package io.github.fantasticname.xianyutradingplatform.service;

/**
 * @author FantasticName
 */
public interface AdminService {
    void deleteListing(String adminUserId, String listingId);

    void deleteComment(String adminUserId, String commentId);
}

