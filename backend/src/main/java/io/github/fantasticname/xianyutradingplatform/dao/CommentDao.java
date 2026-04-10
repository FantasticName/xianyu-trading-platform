package io.github.fantasticname.xianyutradingplatform.dao;

import io.github.fantasticname.xianyutradingplatform.model.Comment;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;
import io.github.fantasticname.xianyutradingplatform.util.UuidUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
public class CommentDao {
    private final DataSource ds;

    public CommentDao(DataSource ds) {
        this.ds = ds;
    }

    public void insert(String listingId, String userId, String content) {
        String sql = "INSERT INTO comments(id, listing_id, user_id, content, created_at) VALUES(?,?,?,?,NOW())";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UuidUtil.newUuid());
            ps.setString(2, listingId);
            ps.setString(3, userId);
            ps.setString(4, content);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public Comment findById(String id) {
        String sql = "SELECT id, listing_id, user_id, content, created_at FROM comments WHERE id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
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

    public void deleteById(String id) {
        String sql = "DELETE FROM comments WHERE id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public List<Comment> listByListing(String listingId, int limit) {
        String sql = "SELECT id, listing_id, user_id, content, created_at FROM comments WHERE listing_id=? ORDER BY created_at DESC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, listingId);
            ps.setInt(2, limit);
            List<Comment> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
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

    private Comment map(ResultSet rs) throws Exception {
        Comment c = new Comment();
        c.setId(rs.getString("id"));
        c.setListingId(rs.getString("listing_id"));
        c.setUserId(rs.getString("user_id"));
        c.setContent(rs.getString("content"));
        Timestamp t = rs.getTimestamp("created_at");
        c.setCreatedAt(t == null ? null : t.toLocalDateTime());
        return c;
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

