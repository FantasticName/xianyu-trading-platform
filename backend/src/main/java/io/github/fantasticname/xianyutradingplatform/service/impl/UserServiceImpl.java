package io.github.fantasticname.xianyutradingplatform.service.impl;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.service.UserService;
import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateMeRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.MeVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 * 负责用户基本资料的管理及用户检索逻辑。
 *
 * @author FantasticName
 */
public class UserServiceImpl implements UserService {
    private final DataSource ds;
    private final UserDao userDao;

    public UserServiceImpl(DataSource ds, UserDao userDao) {
        this.ds = ds;
        this.userDao = userDao;
    }

    @Override
    public MeVO getMe(String userId) {
        User u = userDao.findById(userId);
        if (u == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return toMe(u);
    }

    @Override
    public MeVO updateMe(String userId, UpdateMeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String nickname = request.getNickname();
        String avatarUrl = request.getAvatarUrl();
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "昵称不能为空");
        }
        if (nickname.length() > 20) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "昵称过长");
        }

        return TxManager.executeInTransaction(ds, () -> {
            userDao.updateProfile(userId, nickname.trim(), avatarUrl);
            User updated = userDao.findById(userId);
            return toMe(updated);
        });
    }

    @Override
    public List<UserPublicVO> searchUsers(String keyword, int limit) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        int l = Math.min(Math.max(limit, 1), 50);
        List<User> users = userDao.searchByNickname(keyword.trim(), l);
        List<UserPublicVO> out = new ArrayList<>();
        for (User u : users) {
            out.add(new UserPublicVO(u.getId(), u.getNickname(), u.getAvatarUrl()));
        }
        return out;
    }

    private MeVO toMe(User u) {
        MeVO vo = new MeVO();
        vo.setId(u.getId());
        vo.setAccount(u.getAccount());
        vo.setNickname(u.getNickname());
        vo.setAvatarUrl(u.getAvatarUrl());
        vo.setRole(u.getRole().name());
        return vo;
    }
}

