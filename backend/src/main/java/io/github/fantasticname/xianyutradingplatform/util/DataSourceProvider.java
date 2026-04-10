package io.github.fantasticname.xianyutradingplatform.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * 提供数据库连接池的工具类, 单例模式 . 数据源/连接池提供者：集中创建并暴露全局唯一的 {@link DataSource} 实例。
 * @author FantasticName
 *
 *
 * <p>本项目不使用框架的配置与依赖注入能力，因此通过单例方式在应用启动阶段完成连接池初始化。</p>
 *
 * <p>实现细节：</p>
 * - 连接池实现使用 HikariCP（{@link HikariDataSource}）。<br>
 * - 配置来源为类路径下的 {@code app.properties}，由 {@link AppConfig} 负责加载。<br>
 * - 本类不关心 SQL/事务逻辑，仅负责连接池的创建与复用。
 *
 * <p>配置键：</p>
 * - {@code db.jdbcUrl}：JDBC 连接串（必填）<br>
 * - {@code db.username}：数据库用户名（必填）<br>
 * - {@code db.password}：数据库密码（必填）<br>
 * - {@code db.maximumPoolSize}：最大连接数（可选，默认 10）
 *
 * @author FantasticName
 */
public final class DataSourceProvider {
    private static final DataSourceProvider INSTANCE = new DataSourceProvider();
    private final HikariDataSource dataSource;

    private DataSourceProvider() {
        // 1) 读取数据库配置；缺失关键配置会在 AppConfig 中直接抛出异常，避免运行期静默失败。
        AppConfig cfg = AppConfig.getInstance();
        // 2) 构造 HikariCP 配置，并写入连接信息与池参数。
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(cfg.getString("db.jdbcUrl"));
        hc.setUsername(cfg.getString("db.username"));
        hc.setPassword(cfg.getString("db.password"));
        hc.setMaximumPoolSize(cfg.getInt("db.maximumPoolSize", 10));
        hc.setPoolName("xianyu-hikari");
        // 3) 创建连接池；后续 DAO/Service 通过 DataSource 获取连接。
        this.dataSource = new HikariDataSource(hc);
    }

    /**
     * 获取单例实例。
     *
     * @return 全局唯一的 {@link DataSourceProvider}
     */
    public static DataSourceProvider getInstance() {
        return INSTANCE;
    }

    /**
     * 获取数据源（连接池）。
     *
     * <p>返回值通常会被注入到 DAO/Service 中；事务管理通过 {@link TxManager} 协调连接的复用与提交/回滚。</p>
     *
     * @return 连接池数据源
     */
    public DataSource getDataSource() {
        return dataSource;
    }
}

