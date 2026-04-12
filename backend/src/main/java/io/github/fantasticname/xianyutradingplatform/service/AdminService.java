package io.github.fantasticname.xianyutradingplatform.service;

/**
 * 管理员服务接口
 * 提供管理员相关的业务操作，如删除违规商品、删除违规评论等。
 *
 * @author FantasticName
 */
public interface AdminService {
    /**
     * 删除违规商品
     *
     * @param adminUserId 执行操作的管理员用户ID
     * @param listingId   要删除的商品ID
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果用户不是管理员或操作失败
     */
    void deleteListing(String adminUserId, String listingId);

    /**
     * 删除违规评论
     *
     * @param adminUserId 执行操作的管理员用户ID
     * @param commentId   要删除的评论ID
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果用户不是管理员或操作失败
     */
    void deleteComment(String adminUserId, String commentId);
}

