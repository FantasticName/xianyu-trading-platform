package io.github.fantasticname.xianyutradingplatform.service.impl;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.model.Role;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.service.AuthService;
import io.github.fantasticname.xianyutradingplatform.service.dto.LoginRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.RegisterRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.AuthTokenVO;
import io.github.fantasticname.xianyutradingplatform.util.AppConfig;
import io.github.fantasticname.xianyutradingplatform.util.JwtUtil;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;
import io.github.fantasticname.xianyutradingplatform.util.UuidUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * @author FantasticName
 */
public class AuthServiceImpl implements AuthService {
    private final DataSource ds;
    private final UserDao userDao;
    private final AppConfig cfg;

    public AuthServiceImpl(DataSource ds, UserDao userDao) {
        this.ds = ds;
        this.userDao = userDao;
        this.cfg = AppConfig.getInstance();
    }

    @Override
    public AuthTokenVO register(RegisterRequest request) {
        validateRegister(request);
        return TxManager.executeInTransaction(ds, () -> {
            User existed = userDao.findByAccount(request.getAccount().trim());
            if (existed != null) {
                throw new BusinessException(ErrorCode.CONFLICT, "账号已存在");
            }

            Role role = Role.USER;
            if (request.getRole() != null && "ADMIN".equalsIgnoreCase(request.getRole().trim())) {
                String invite = request.getAdminInviteCode();
                String expected = cfg.getString("admin.inviteCode", "");
                if (expected.isBlank() || invite == null || !expected.equals(invite.trim())) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "管理员邀请码无效");
                }
                role = Role.ADMIN;
            }

            LocalDateTime now = LocalDateTime.now();
            User u = new User();
            u.setId(UuidUtil.newUuid());
            u.setAccount(request.getAccount().trim());
            u.setPasswordHash(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12)));
            u.setNickname(request.getNickname().trim());
            u.setAvatarUrl(null);
            u.setRole(role);
            u.setCreatedAt(now);
            u.setUpdatedAt(now);
            userDao.insert(u);
            return new AuthTokenVO(JwtUtil.getInstance().createToken(u.getId()));
        });
    }

    @Override
    public AuthTokenVO login(LoginRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        String account = request.getAccount();
        String password = request.getPassword();
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "账号或密码不能为空");
        }

        User u = userDao.findByAccount(account.trim());
        if (u == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        if (!BCrypt.checkpw(password, u.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        return new AuthTokenVO(JwtUtil.getInstance().createToken(u.getId()));
    }

    private void validateRegister(RegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        if (request.getAccount() == null || request.getAccount().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "账号不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "密码不能为空");
        }
        if (request.getNickname() == null || request.getNickname().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "昵称不能为空");
        }
        if (request.getPassword().length() < 6 || request.getPassword().length() > 32) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "密码长度需在 6~32 之间");
        }
        if (request.getNickname().length() > 20) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "昵称过长");
        }
        if (request.getAccount().length() > 255) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "账号过长");
        }
    }
}

