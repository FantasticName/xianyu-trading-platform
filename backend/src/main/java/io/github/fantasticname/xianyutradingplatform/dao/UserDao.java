package io.github.fantasticname.xianyutradingplatform.dao;

import io.github.fantasticname.xianyutradingplatform.model.Role;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
/**
 * 用户（users）表的数据访问对象：提供按 id/账号查询、昵称搜索、创建用户、更新个人资料等操作。
 *
 * <p>连接与事务协作：</p>
 * - 若 {@link TxManager#currentConnection()} 存在，则复用同一连接参与事务；<br>
 * - 否则从 {@link DataSource} 借用连接并在调用结束后关闭。
 *
 * <p>该类只做数据库访问，不做参数合法性校验与业务规则判断；这些应由 Service 层负责。</p>
 *
 * @author FantasticName
 */
public class UserDao {
    private final DataSource ds;

    /**
     * 创建 UserDao。
     *
     * @param ds 数据源（连接池）
     */
    public UserDao(DataSource ds) {
        this.ds = ds;
    }

    /**
     * 按用户 id 查询用户。
     *
     * <p>执行步骤：</p>
     * 1) 获取连接；<br>
     * 2) 执行按 id 精确查询；<br>
     * 3) 若存在记录则映射为 {@link User}，否则返回 {@code null}；<br>
     * 4) 归还临时连接（如有）。
     *
     * @param id 用户 id
     * @return 用户对象；不存在则返回 {@code null}
     * @throws IllegalStateException 数据库操作失败
     */
    public User findById(String id) {
        String sql = "SELECT id, account, password_hash, nickname, avatar_url, role, created_at, updated_at FROM users WHERE id = ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1) 绑定查询参数（预编译 + 防注入）。
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                // 2) 最多返回一条记录：无记录则返回 null。
                if (!rs.next()) {
                    return null;
                }
                // 3) 将 ResultSet 映射为领域模型。
                return map(rs);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            // 4) 仅关闭临时借用的连接。
            ch.closeIfBorrowed();
        }
    }

    /**
     * 按账号查询用户。
     *
     * <p>用于登录场景：先查询用户，再由上层校验密码哈希。</p>
     *
     * @param account 登录账号（唯一）
     * @return 用户对象；不存在则返回 {@code null}
     * @throws IllegalStateException 数据库操作失败
     */
    public User findByAccount(String account) {
        String sql = "SELECT id, account, password_hash, nickname, avatar_url, role, created_at, updated_at FROM users WHERE account = ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return map(rs);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    /**
     * 按昵称关键字搜索用户（模糊匹配）。
     *
     * <p>执行步骤：</p>
     * 1) 组装 LIKE 条件（在两侧追加 {@code %}）；<br>
     * 2) 按最近更新时间倒序；<br>
     * 3) 限制返回条数，避免一次性返回过多数据。
     *
     * @param keyword 昵称关键字
     * @param limit 最大返回条数
     * @return 用户列表（可能为空）
     * @throws IllegalStateException 数据库操作失败
     */
    public List<User> searchByNickname(String keyword, int limit) {
        String sql = "SELECT id, account, password_hash, nickname, avatar_url, role, created_at, updated_at FROM users WHERE nickname LIKE ? ORDER BY updated_at DESC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1) 使用 LIKE 进行模糊匹配；此处由 DAO 负责拼接通配符。
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, limit);
            List<User> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                // 2) 逐行映射为 User 列表。
                while (rs.next()) {
                    out.add(map(rs));
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
     * 插入新用户记录。
     *
     * <p>时间字段由上层填充：{@link User#getCreatedAt()} 与 {@link User#getUpdatedAt()}。</p>
     *
     * @param u 用户对象（需包含 id、account、passwordHash 等必填字段）
     * @throws IllegalStateException 数据库操作失败
     */
    public void insert(User u) {
        String sql = "INSERT INTO users(id, account, password_hash, nickname, avatar_url, role, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?)";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1) 绑定插入字段（角色以枚举 name 存储）。
            ps.setString(1, u.getId());
            ps.setString(2, u.getAccount());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getNickname());
            ps.setString(5, u.getAvatarUrl());
            ps.setString(6, u.getRole().name());
            ps.setTimestamp(7, Timestamp.valueOf(u.getCreatedAt()));
            ps.setTimestamp(8, Timestamp.valueOf(u.getUpdatedAt()));
            // 2) 执行写操作。
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    /**
     * 更新用户个人资料（昵称与头像）。
     *
     * <p>该方法只更新可编辑字段，并由 SQL 将 {@code updated_at} 刷新为当前时间。</p>
     *
     * @param id 用户 id
     * @param nickname 新昵称
     * @param avatarUrl 新头像 URL
     * @throws IllegalStateException 数据库操作失败
     */
    public void updateProfile(String id, String nickname, String avatarUrl) {
        String sql = "UPDATE users SET nickname = ?, avatar_url = ?, updated_at = NOW() WHERE id = ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setString(2, avatarUrl);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    /**
     * 将当前行 ResultSet 映射为 {@link User}。
     *
     * <p>映射要点：</p>
     * - role 字段按 {@link Role} 枚举解析；<br>
     * - created_at/updated_at 可能为 null，需要做空值保护并转换为 {@link LocalDateTime}。
     *
     * @param rs 查询结果集（已指向某一行）
     * @return 映射后的用户对象
     * @throws Exception ResultSet 读取失败或枚举解析失败
     */
    private User map(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getString("id"));
        u.setAccount(rs.getString("account"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNickname(rs.getString("nickname"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setRole(Role.valueOf(rs.getString("role")));
        Timestamp c = rs.getTimestamp("created_at");
        Timestamp up = rs.getTimestamp("updated_at");
        u.setCreatedAt(c == null ? null : c.toLocalDateTime());
        u.setUpdatedAt(up == null ? null : up.toLocalDateTime());
        return u;
    }

    /**
     * 获取可用连接，并标记是否为“临时借用”。
     *
     * @return 连接持有器
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
            return conn;
        }

        /**
         * 若连接为当前 DAO 临时借用，则关闭以归还连接池。
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

