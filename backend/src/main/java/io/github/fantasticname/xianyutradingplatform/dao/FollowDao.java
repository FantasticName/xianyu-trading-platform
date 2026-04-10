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
public class FollowDao {
    private final DataSource ds;

    public FollowDao(DataSource ds) {
        this.ds = ds;
    }

    public boolean exists(String followerId, String sellerId) {
        String sql = "SELECT 1 FROM follows WHERE follower_id=? AND seller_id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, followerId);
            ps.setString(2, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public void insert(String followerId, String sellerId) {
        String sql = "INSERT INTO follows(id, follower_id, seller_id, created_at) VALUES(?,?,?,NOW())";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UuidUtil.newUuid());
            ps.setString(2, followerId);
            ps.setString(3, sellerId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public void delete(String followerId, String sellerId) {
        String sql = "DELETE FROM follows WHERE follower_id=? AND seller_id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, followerId);
            ps.setString(2, sellerId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public List<String> listSellerIdsByFollower(String followerId, int limit) {
        String sql = "SELECT seller_id FROM follows WHERE follower_id=? ORDER BY created_at DESC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, followerId);
            ps.setInt(2, limit);
            List<String> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getString("seller_id"));
                }
            }
            return out;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

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

        public Connection conn() {
            return conn;
        }

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

