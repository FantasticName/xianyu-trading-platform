package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.LoginRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.RegisterRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.AuthTokenVO;

/**
 * @author FantasticName
 */
public interface AuthService {
    AuthTokenVO register(RegisterRequest request);

    AuthTokenVO login(LoginRequest request);
}

