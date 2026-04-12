package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateMeRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.MeVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;

import java.util.List;

/**
 * 用户服务接口
 * 处理个人中心信息查询、个人资料修改及用户搜索。
 *
 * @author FantasticName
 */
public interface UserService {
    /**
     * 获取当前用户信息（个人中心）
     *
     * @param userId 当前登录用户ID
     * @return 包含账号、昵称、头像、角色等详细信息的 VO
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果用户不存在
     */
    MeVO getMe(String userId);

    /**
     * 修改个人资料
     *
     * @param userId  当前登录用户ID
     * @param request 包含新昵称和头像的请求对象
     * @return 修改后的用户信息 VO
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果参数无效
     */
    MeVO updateMe(String userId, UpdateMeRequest request);

    /**
     * 搜索用户
     * 根据昵称进行模糊查询。
     *
     * @param keyword 搜索关键字
     * @param limit   返回结果的最大数量
     * @return 匹配的用户公开信息列表
     */
    List<UserPublicVO> searchUsers(String keyword, int limit);
}

