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
     // 定义JWT中存储用户ID的声明（Claim）键名常量
    private static final String CLAIM_UID = "uid";

    // 饿汉式单例模式，类加载时即创建唯一实例
    private static final JwtUtil INSTANCE = new JwtUtil(); 


    // 声明不可变的签名算法实例，用于Token签名和验证
    private final Algorithm algorithm; 

    // 声明不可变的验证器实例，用于快速验证Token
    private final JWTVerifier verifier; 

    // 声明不可变的Token过期时长（秒），从配置读取
    private final long expireSeconds; 


    // 私有构造方法，防止外部实例化
    private JwtUtil() { 
        AppConfig cfg = AppConfig.getInstance(); 
        this.algorithm = Algorithm.HMAC256(cfg.getString("jwt.secret")); // 使用HMAC256算法，密钥从配置中读取
        this.verifier = JWT.require(algorithm).build(); // 基于算法构建默认的JWT验证器，用于后续验证
        this.expireSeconds = cfg.getInt("jwt.expireSeconds", 7 * 24 * 3600); // 读取配置中的过期时间，默认7天（单位秒）
    }

    public static JwtUtil getInstance() { // 提供全局访问单例的静态方法
        return INSTANCE; // 返回唯一的实例对象
    }

    public String createToken(String userId) { // 根据用户ID生成JWT Token
        Instant now = Instant.now(); // 获取当前UTC时间点
        Instant exp = now.plusSeconds(expireSeconds); // 计算过期时间点 = 当前时间 + 过期秒数
        return JWT.create() // 开始构建JWT Token
                .withIssuedAt(Date.from(now)) // 设置Token签发时间（转为Date类型，兼容JWT库）
                .withExpiresAt(Date.from(exp)) // 设置Token过期时间
                .withClaim(CLAIM_UID, userId) // 在Payload中添加自定义声明，键为"uid"，值为传入的用户ID
                .sign(algorithm); // 使用配置的HMAC256算法对Header和Payload进行签名，生成最终Token字符串
    }

    public String verifyAndGetUserId(String token) { // 验证Token并从中提取用户ID，验证失败返回null
        try {
            DecodedJWT jwt = verifier.verify(token); // 使用预构建的验证器校验Token签名、时效等，若失败会抛出异常
            String uid = jwt.getClaim(CLAIM_UID).asString(); // 从解码后的JWT中获取"uid"声明并转为字符串
            if (uid == null || uid.isBlank()) { // 检查uid是否为空或仅含空白字符
                throw new JWTVerificationException("Missing uid"); // 如果缺失有效uid，主动抛出验证异常
            }
            return uid; // 验证通过，返回用户ID
        } catch (JWTVerificationException e) { // 捕获任何JWT验证异常（签名错误、过期、格式不对等）
            return null; // 出现异常时返回null，表示验证失败
        }
    }
}