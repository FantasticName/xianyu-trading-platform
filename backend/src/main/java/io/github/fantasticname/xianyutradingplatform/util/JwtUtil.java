package io.github.fantasticname.xianyutradingplatform.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;

/**
 * @author FantasticName
 */
public final class JwtUtil {
    private static final String CLAIM_UID = "uid";
    private static final JwtUtil INSTANCE = new JwtUtil();

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expireSeconds;

    private JwtUtil() {
        AppConfig cfg = AppConfig.getInstance();
        this.algorithm = Algorithm.HMAC256(cfg.getString("jwt.secret"));
        this.verifier = JWT.require(algorithm).build();
        this.expireSeconds = cfg.getInt("jwt.expireSeconds", 7 * 24 * 3600);
    }

    public static JwtUtil getInstance() {
        return INSTANCE;
    }

    public String createToken(String userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expireSeconds);
        return JWT.create()
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withClaim(CLAIM_UID, userId)
                .sign(algorithm);
    }

    public String verifyAndGetUserId(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            String uid = jwt.getClaim(CLAIM_UID).asString();
            if (uid == null || uid.isBlank()) {
                throw new JWTVerificationException("Missing uid");
            }
            return uid;
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}

