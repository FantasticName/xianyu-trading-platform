package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.FavoriteRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.FollowRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.PublishCommentRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.CommentVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;

import java.util.List;

/**
 * @author FantasticName
 */
public interface SocialService {
    void follow(String userId, FollowRequest request);

    void unfollow(String userId, FollowRequest request);

    void favorite(String userId, FavoriteRequest request);

    void unfavorite(String userId, FavoriteRequest request);

    void addComment(String userId, String listingId, PublishCommentRequest request);

    void deleteComment(String userId, String commentId);

    List<CommentVO> listComments(String listingId, int limit);

    List<UserPublicVO> listFollows(String userId, int limit);

    List<ListingVO> listFavorites(String userId, int limit);
}

