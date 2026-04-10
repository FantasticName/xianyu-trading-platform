package io.github.fantasticname.xianyutradingplatform.service.impl;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.dao.CommentDao;
import io.github.fantasticname.xianyutradingplatform.dao.ListingDao;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.model.Role;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.service.AdminService;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;

import javax.sql.DataSource;

/**
 * @author FantasticName
 */
public class AdminServiceImpl implements AdminService {
    private final DataSource ds;
    private final UserDao userDao;
    private final ListingDao listingDao;
    private final CommentDao commentDao;

    public AdminServiceImpl(DataSource ds, UserDao userDao, ListingDao listingDao, CommentDao commentDao) {
        this.ds = ds;
        this.userDao = userDao;
        this.listingDao = listingDao;
        this.commentDao = commentDao;
    }

    @Override
    public void deleteListing(String adminUserId, String listingId) {
        requireAdmin(adminUserId);
        TxManager.executeInTransaction(ds, () -> {
            listingDao.deleteById(listingId);
            return null;
        });
    }

    @Override
    public void deleteComment(String adminUserId, String commentId) {
        requireAdmin(adminUserId);
        TxManager.executeInTransaction(ds, () -> {
            commentDao.deleteById(commentId);
            return null;
        });
    }

    private void requireAdmin(String userId) {
        User u = userDao.findById(userId);
        if (u == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (u.getRole() != Role.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅管理员可操作");
        }
    }
}

