package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.LoginRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.RegisterRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.AuthTokenVO;

/**
 * 认证服务接口
 * 处理用户注册、登录等身份验证相关的业务。
 *
 * @author FantasticName
 */
public interface AuthService {
    /**
     * 用户注册
     * 支持普通用户和管理员注册（管理员注册需要有效的邀请码）。
     *
     * @param request 注册请求信息，包含账号、密码、昵称及可选的角色和邀请码
     * @return 包含生成的 JWT 访问令牌的 VO 对象
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果账号已存在、参数无效或管理员邀请码错误
     */
    AuthTokenVO register(RegisterRequest request);

    /**
     * 用户登录
     * 验证用户账号和密码，并生成访问令牌。
     *
     * @param request 登录请求信息，包含账号和密码
     * @return 包含生成的 JWT 访问令牌的 VO 对象
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果账号或密码错误
     */
    AuthTokenVO login(LoginRequest request);
}

