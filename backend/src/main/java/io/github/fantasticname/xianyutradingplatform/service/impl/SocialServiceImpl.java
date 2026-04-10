package io.github.fantasticname.xianyutradingplatform.service.impl;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.dao.CommentDao;
import io.github.fantasticname.xianyutradingplatform.dao.FavoriteDao;
import io.github.fantasticname.xianyutradingplatform.dao.FollowDao;
import io.github.fantasticname.xianyutradingplatform.dao.ListingDao;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.model.Comment;
import io.github.fantasticname.xianyutradingplatform.model.Listing;
import io.github.fantasticname.xianyutradingplatform.model.Role;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.service.SocialService;
import io.github.fantasticname.xianyutradingplatform.service.dto.FavoriteRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.FollowRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.PublishCommentRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.CommentVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.ListingVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
public class SocialServiceImpl implements SocialService {
    private final DataSource ds;
    private final FollowDao followDao;
    private final FavoriteDao favoriteDao;
    private final CommentDao commentDao;
    private final ListingDao listingDao;
    private final UserDao userDao;

    public SocialServiceImpl(DataSource ds, FollowDao followDao, FavoriteDao favoriteDao, CommentDao commentDao, ListingDao listingDao, UserDao userDao) {
        this.ds = ds;
        this.followDao = followDao;
        this.favoriteDao = favoriteDao;
        this.commentDao = commentDao;
        this.listingDao = listingDao;
        this.userDao = userDao;
    }

    @Override
    public void follow(String userId, FollowRequest request) {
        if (request == null || request.getSellerId() == null || request.getSellerId().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        String sellerId = request.getSellerId().trim();
        if (sellerId.equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能关注自己");
        }
        User seller = userDao.findById(sellerId);
        if (seller == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "卖家不存在");
        }
        TxManager.executeInTransaction(ds, () -> {
            if (!followDao.exists(userId, sellerId)) {
                followDao.insert(userId, sellerId);
            }
            return null;
        });
    }

    @Override
    public void unfollow(String userId, FollowRequest request) {
        if (request == null || request.getSellerId() == null || request.getSellerId().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        String sellerId = request.getSellerId().trim();
        TxManager.executeInTransaction(ds, () -> {
            followDao.delete(userId, sellerId);
            return null;
        });
    }

    @Override
    public void favorite(String userId, FavoriteRequest request) {
        if (request == null || request.getListingId() == null || request.getListingId().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        String listingId = request.getListingId().trim();
        Listing l = listingDao.findById(listingId);
        if (l == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        TxManager.executeInTransaction(ds, () -> {
            if (!favoriteDao.exists(userId, listingId)) {
                favoriteDao.insert(userId, listingId);
            }
            return null;
        });
    }

    @Override
    public void unfavorite(String userId, FavoriteRequest request) {
        if (request == null || request.getListingId() == null || request.getListingId().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        String listingId = request.getListingId().trim();
        TxManager.executeInTransaction(ds, () -> {
            favoriteDao.delete(userId, listingId);
            return null;
        });
    }

    @Override
    public void addComment(String userId, String listingId, PublishCommentRequest request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "评论不能为空");
        }
        Listing l = listingDao.findById(listingId);
        if (l == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        String content = request.getContent().trim();
        if (content.length() > 500) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "评论过长");
        }
        TxManager.executeInTransaction(ds, () -> {
            commentDao.insert(listingId, userId, content);
            return null;
        });
    }

    @Override
    public void deleteComment(String userId, String commentId) {
        Comment c = commentDao.findById(commentId);
        if (c == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        if (!userId.equals(c.getUserId())) {
            User me = userDao.findById(userId);
            if (me == null || me.getRole() != Role.ADMIN) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的评论");
            }
        }
        TxManager.executeInTransaction(ds, () -> {
            commentDao.deleteById(commentId);
            return null;
        });
    }

    @Override
    public List<CommentVO> listComments(String listingId, int limit) {
        int l = Math.min(Math.max(limit, 1), 100);
        List<Comment> list = commentDao.listByListing(listingId, l);
        List<CommentVO> out = new ArrayList<>();
        for (Comment c : list) {
            User u = userDao.findById(c.getUserId());
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setListingId(c.getListingId());
            vo.setUserId(c.getUserId());
            vo.setUserNickname(u == null ? null : u.getNickname());
            vo.setUserAvatarUrl(u == null ? null : u.getAvatarUrl());
            vo.setContent(c.getContent());
            vo.setCreatedAt(c.getCreatedAt() == null ? null : c.getCreatedAt().toString());
            out.add(vo);
        }
        return out;
    }

    @Override
    public List<UserPublicVO> listFollows(String userId, int limit) {
        int l = Math.min(Math.max(limit, 1), 100);
        List<String> sellerIds = followDao.listSellerIdsByFollower(userId, l);
        List<UserPublicVO> out = new ArrayList<>();
        for (String sellerId : sellerIds) {
            User seller = userDao.findById(sellerId);
            if (seller != null) {
                out.add(new UserPublicVO(seller.getId(), seller.getNickname(), seller.getAvatarUrl()));
            }
        }
        return out;
    }

    @Override
    public List<ListingVO> listFavorites(String userId, int limit) {
        int l = Math.min(Math.max(limit, 1), 100);
        List<String> listingIds = favoriteDao.listListingIdsByUser(userId, l);
        List<ListingVO> out = new ArrayList<>();
        for (String id : listingIds) {
            Listing listing = listingDao.findById(id);
            if (listing == null) {
                continue;
            }
            User seller = userDao.findById(listing.getSellerId());
            ListingVO vo = new ListingVO();
            vo.setId(listing.getId());
            vo.setSellerId(listing.getSellerId());
            vo.setSellerNickname(seller == null ? null : seller.getNickname());
            vo.setTitle(listing.getTitle());
            vo.setPrice(listing.getPrice());
            vo.setCondition(listing.getCondition().name());
            vo.setCategory(listing.getCategory());
            vo.setCoverUrl(listing.getCoverUrl());
            vo.setStatus(listing.getStatus().name());
            vo.setCreatedAt(listing.getCreatedAt() == null ? null : listing.getCreatedAt().toString());
            out.add(vo);
        }
        return out;
    }
}

