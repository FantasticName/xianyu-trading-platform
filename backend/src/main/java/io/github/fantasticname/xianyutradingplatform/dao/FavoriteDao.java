package io.github.fantasticname.xianyutradingplatform.dao;

import io.github.fantasticname.xianyutradingplatform.util.TxManager;
import io.github.fantasticname.xianyutradingplatform.util.UuidUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
/**
 * 收藏（favorites）表的数据访问对象：封装“是否已收藏/新增收藏/取消收藏/查询用户收藏列表”等 SQL 操作。
 *
 * <p>连接与事务协作：</p>
 * - 若当前线程存在事务连接（{@link TxManager#currentConnection()} 非空），则复用该连接并由事务管理器负责提交/回滚；<br>
 * - 否则从 {@link DataSource} 临时借用连接，并在方法结束时关闭，确保不会泄漏连接。
 *
 * <p>异常策略：</p>
 * - DAO 将底层 SQL/连接异常包装为 {@link IllegalStateException} 抛出，由上层统一异常处理转换为标准错误响应。</p>
 *
 * @author FantasticName
 */
public class FavoriteDao {
    private final DataSource ds;


    // 依赖的DataSource对象通过构造函数注入。这种设计使得 Dao 不负责创建连接池，只负责使用，符合单一职责原则。
    /**
     * 创建 FavoriteDao。
     *
     * @param ds 数据源（连接池），用于在无事务上下文时获取数据库连接
     */
    public FavoriteDao(DataSource ds) {
        this.ds = ds;
    }

    /**
     * 判断某用户是否已收藏某条商品。
     *
     * <p>执行步骤：</p>
     * 1) 通过 {@link #borrow()} 获取可用连接（优先复用事务连接）；<br>
     * 2) 执行 {@code SELECT 1 ...}，只要存在记录即可返回 {@code true}；<br>
     * 3) 若连接为临时借用，则在 finally 中关闭。
     *
     * @param userId 用户 id
     * @param listingId 商品/闲置 id
     * @return 已收藏返回 {@code true}；否则返回 {@code false}
     * @throws IllegalStateException 数据库操作失败
     */
    public boolean exists(String userId, String listingId) {

        // select 1 无需读取所有列数据, 性能稍快
        String sql = "SELECT 1 FROM favorites WHERE user_id=? AND listing_id=?";

        // 1) 获取连接（优先复用事务连接）。
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1) 绑定查询参数，避免 SQL 注入并复用预编译计划。
            ps.setString(1, userId);
            ps.setString(2, listingId);
            // 2) 只判断是否存在记录，不读取具体列。
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            // 3) 仅关闭“临时借用”的连接；事务连接由 TxManager 统一关闭。
            ch.closeIfBorrowed();
        }
    }

    /**
     * 新增收藏记录。
     *
     * <p>执行步骤：</p>
     * 1) 获取连接；<br>
     * 2) 生成收藏记录 id，并写入 userId/listingId；<br>
     * 3) 执行插入；<br>
     * 4) 若连接为临时借用则关闭。
     *
     * @param userId 用户 id
     * @param listingId 商品/闲置 id
     * @throws IllegalStateException 数据库操作失败
     */
    public void insert(String userId, String listingId) {
        String sql = "INSERT INTO favorites(id, user_id, listing_id, created_at) VALUES(?,?,?,NOW())";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1) 绑定插入参数：id 使用 UUID，created_at 在 SQL 中使用 NOW()。
            ps.setString(1, UuidUtil.newUuid());
            ps.setString(2, userId);
            ps.setString(3, listingId);
            // 2) 执行写操作。
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    /**
     * 取消收藏记录。
     *
     * <p>执行步骤：</p>
     * 1) 获取连接；<br>
     * 2) 绑定 userId/listingId；<br>
     * 3) 执行删除；<br>
     * 4) 清理临时连接。
     *
     * @param userId 用户 id
     * @param listingId 商品/闲置 id
     * @throws IllegalStateException 数据库操作失败
     */
    public void delete(String userId, String listingId) {
        String sql = "DELETE FROM favorites WHERE user_id=? AND listing_id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1) 绑定删除条件。
            ps.setString(1, userId);
            ps.setString(2, listingId);
            // 2) 执行删除（即使不存在记录也不会报错，影响行数为 0）。
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    /**
     * 查询某用户最近收藏的商品 id 列表。
     *
     * <p>执行步骤：</p>
     * 1) 按收藏时间倒序查询；<br>
     * 2) 只返回 listing_id 列，交由上层批量获取商品详情；<br>
     * 3) 返回结果数量最多为 limit。
     *
     * @param userId 用户 id
     * @param limit 最大返回条数
     * @return 商品 id 列表（按收藏时间倒序）
     * @throws IllegalStateException 数据库操作失败
     */
    public List<String> listListingIdsByUser(String userId, int limit) {
        String sql = "SELECT listing_id FROM favorites WHERE user_id=? ORDER BY created_at DESC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, limit);
            List<String> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                // 逐行读取 listing_id，保持输出顺序与 SQL 排序一致。
                while (rs.next()) {
                    out.add(rs.getString("listing_id"));
                }
            }
            return out;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    /**
     * 获取可用连接，并标记是否为“临时借用”。
     *
     * <p>优先复用事务连接：当 {@link TxManager} 已为当前线程绑定连接时，本 DAO 不应自行关闭连接。</p>
     *
     * @return ConnectionHolder 对象, 封装了 JDBC 连接和是否为临时借用的标志
     * @throws IllegalStateException 获取连接失败
     */
    private ConnectionHolder borrow() {
        Connection tx = TxManager.currentConnection();
        if (tx != null) {
            return new ConnectionHolder(tx, false);
        }
        try {
            return new ConnectionHolder(ds.getConnection(), true);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class ConnectionHolder {
        private final Connection conn;
        private final boolean borrowed;

        private ConnectionHolder(Connection conn, boolean borrowed) {
            this.conn = conn;
            this.borrowed = borrowed;
        }

        /**
         * 获取底层 JDBC 连接。
         *
         * @return JDBC 连接
         */
        public Connection conn() {
            return this.conn;
        }

        /**
         * 如果连接为当前 DAO 临时借用，则在调用结束后关闭连接以归还连接池。
         *
         * <p>若连接来自事务上下文，则由事务管理器负责关闭，本方法不会关闭。</p>
         */
        public void closeIfBorrowed() {
            if (!borrowed) {
                return;
            }
            try {
                conn.close();
            } catch (Exception ignored) {
            }
        }
    }
}

