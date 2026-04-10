package io.github.fantasticname.xianyutradingplatform.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author FantasticName
 */
/**
 * 应用配置读取器：从类路径资源 {@code app.properties} 加载键值对配置，并提供类型安全的读取方法。
 *
 * <p>设计目标：</p>
 * - 项目不引入 Spring 等配置框架，因此用最小实现完成配置加载；<br>
 * - 对“必填配置”在启动阶段快速失败（Fail-Fast），避免运行期出现隐蔽错误；<br>
 * - 对“可选配置”提供默认值读取。
 *
 * <p>线程安全：</p>
 * - 配置在单例构造时一次性加载，之后只读访问 {@link Properties}，可安全并发读取。</p>
 *
 * @author FantasticName
 */
public final class AppConfig {
    private static final String FILE_NAME = "app.properties";
    private static final AppConfig INSTANCE = new AppConfig();
    private final Properties properties = new Properties();

    private AppConfig() {
        // 1) 从类加载器读取 app.properties；若缺失则直接抛出异常，避免使用默认/空配置继续运行。
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + FILE_NAME);
            }
            // 2) 加载到 Properties 中，后续通过 getString/getInt 读取。
            properties.load(in);
        } catch (IOException e) {
            // 读取/解析失败同样属于不可恢复的启动错误，直接抛出运行时异常阻止应用继续运行。
            throw new IllegalStateException("Failed to load " + FILE_NAME, e);
        }
    }

    /**
     * 获取单例实例。
     *
     * @return 全局唯一的 {@link AppConfig}
     */
    public static AppConfig getInstance() {
        return INSTANCE;
    }

    /**
     * 读取必填字符串配置。
     *
     * <p>用于数据库连接串、密钥等必须存在的配置：缺失时会抛出异常以便尽早暴露问题。</p>
     *
     * @param key 配置键
     * @return 去除首尾空白后的配置值
     * @throws IllegalStateException 当配置键不存在时抛出
     */
    public String getString(String key) {
        String v = properties.getProperty(key);
        if (v == null) {
            throw new IllegalStateException("Missing config key: " + key);
        }
        return v.trim();
    }

    /**
     * 读取可选字符串配置。
     *
     * @param key 配置键
     * @param defaultValue 默认值（当配置键缺失时返回）
     * @return 去除首尾空白后的配置值；若缺失则返回 defaultValue
     */
    public String getString(String key, String defaultValue) {
        String v = properties.getProperty(key);
        if (v == null) {
            return defaultValue;
        }
        return v.trim();
    }

    /**
     * 读取整数配置（可选）。
     *
     * <p>当键缺失或值为空白时返回默认值；否则尝试将字符串解析为 int。</p>
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 解析后的整数值或默认值
     * @throws NumberFormatException 当配置值存在但无法解析为整数时抛出
     */
    public int getInt(String key, int defaultValue) {
        String v = properties.getProperty(key);
        if (v == null || v.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(v.trim());
    }
}

