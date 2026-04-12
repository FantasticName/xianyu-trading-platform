package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.FavoriteRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.FollowRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.PublishCommentRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.CommentVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;

import java.util.List;

/**
 * 社交服务接口
 * 处理用户关注、商品收藏及评论相关的业务逻辑。
 *
 * @author FantasticName
 */
public interface SocialService {
    /**
     * 关注卖家
     *
     * @param userId  当前用户ID
     * @param request 包含目标卖家ID的请求对象
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果关注自己或卖家不存在
     */
    void follow(String userId, FollowRequest request);

    /**
     * 取消关注卖家
     *
     * @param userId  当前用户ID
     * @param request 包含目标卖家ID的请求对象
     */
    void unfollow(String userId, FollowRequest request);

    /**
     * 收藏商品
     *
     * @param userId  当前用户ID
     * @param request 包含目标商品ID的请求对象
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果商品不存在
     */
    void favorite(String userId, FavoriteRequest request);

    /**
     * 取消收藏商品
     *
     * @param userId  当前用户ID
     * @param request 包含目标商品ID的请求对象
     */
    void unfavorite(String userId, FavoriteRequest request);

    /**
     * 发表评论
     *
     * @param userId    当前用户ID
     * @param listingId 商品ID
     * @param request   包含评论内容的请求对象
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果商品不存在、评论为空或过长
     */
    void addComment(String userId, String listingId, PublishCommentRequest request);

    /**
     * 删除评论
     *
     * @param userId    当前用户ID（必须是评论者本人或管理员）
     * @param commentId 评论ID
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果评论不存在或无权删除
     */
    void deleteComment(String userId, String commentId);

    /**
     * 获取指定商品的评论列表
     *
     * @param listingId 商品ID
     * @param limit     返回评论的最大数量
     * @return 评论列表
     */
    List<CommentVO> listComments(String listingId, int limit);

    /**
     * 获取我关注的卖家列表
     *
     * @param userId 当前用户ID
     * @param limit  返回的最大数量
     * @return 关注的卖家列表
     */
    List<UserPublicVO> listFollows(String userId, int limit);

    /**
     * 获取我收藏的商品列表
     *
     * @param userId 当前用户ID
     * @param limit  返回的最大数量
     * @return 收藏的商品列表
     */
    List<ListingVO> listFavorites(String userId, int limit);
}

