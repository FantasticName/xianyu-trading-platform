package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.UpdateMeRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.MeVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.UserPublicVO;

import java.util.List;

/**
 * @author FantasticName
 */
public interface UserService {
    MeVO getMe(String userId);

    MeVO updateMe(String userId, UpdateMeRequest request);

    List<UserPublicVO> searchUsers(String keyword, int limit);
}

