package io.github.fantasticname.xianyutradingplatform.dao;

import io.github.fantasticname.xianyutradingplatform.model.Conversation;
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
public class ConversationDao {
    private final DataSource ds;

    public ConversationDao(DataSource ds) {
        this.ds = ds;
    }

    public Conversation findById(String id) {
        String sql = "SELECT id, listing_id, buyer_id, seller_id, updated_at, created_at FROM conversations WHERE id=?";
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

    public Conversation findByUnique(String listingId, String buyerId, String sellerId) {
        String sql = "SELECT id, listing_id, buyer_id, seller_id, updated_at, created_at FROM conversations WHERE listing_id=? AND buyer_id=? AND seller_id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, listingId);
            ps.setString(2, buyerId);
            ps.setString(3, sellerId);
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

    public Conversation insert(String listingId, String buyerId, String sellerId) {
        String id = UuidUtil.newUuid();
        String sql = "INSERT INTO conversations(id, listing_id, buyer_id, seller_id, updated_at, created_at) VALUES(?,?,?,?,NOW(),NOW())";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, listingId);
            ps.setString(3, buyerId);
            ps.setString(4, sellerId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
        return findById(id);
    }

    public void touch(String id) {
        String sql = "UPDATE conversations SET updated_at=NOW() WHERE id=?";
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

    public List<Conversation> listByUser(String userId, int limit) {
        String sql = "SELECT id, listing_id, buyer_id, seller_id, updated_at, created_at FROM conversations WHERE buyer_id=? OR seller_id=? ORDER BY updated_at DESC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setInt(3, limit);
            List<Conversation> out = new ArrayList<>();
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

    private Conversation map(ResultSet rs) throws Exception {
        Conversation c = new Conversation();
        c.setId(rs.getString("id"));
        c.setListingId(rs.getString("listing_id"));
        c.setBuyerId(rs.getString("buyer_id"));
        c.setSellerId(rs.getString("seller_id"));
        Timestamp up = rs.getTimestamp("updated_at");
        Timestamp ct = rs.getTimestamp("created_at");
        c.setUpdatedAt(up == null ? null : up.toLocalDateTime());
        c.setCreatedAt(ct == null ? null : ct.toLocalDateTime());
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

