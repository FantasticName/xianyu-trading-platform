package io.github.fantasticname.xianyutradingplatform.dao;

import io.github.fantasticname.xianyutradingplatform.model.Message;
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
public class MessageDao {
    private final DataSource ds;

    public MessageDao(DataSource ds) {
        this.ds = ds;
    }

    public void insert(String conversationId, String senderId, String content) {
        String sql = "INSERT INTO messages(id, conversation_id, sender_id, content, created_at) VALUES(?,?,?,?,NOW())";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UuidUtil.newUuid());
            ps.setString(2, conversationId);
            ps.setString(3, senderId);
            ps.setString(4, content);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public List<Message> listByConversation(String conversationId, int limit) {
        String sql = "SELECT id, conversation_id, sender_id, content, created_at FROM messages WHERE conversation_id=? ORDER BY created_at ASC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conversationId);
            ps.setInt(2, limit);
            List<Message> out = new ArrayList<>();
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

    private Message map(ResultSet rs) throws Exception {
        Message m = new Message();
        m.setId(rs.getString("id"));
        m.setConversationId(rs.getString("conversation_id"));
        m.setSenderId(rs.getString("sender_id"));
        m.setContent(rs.getString("content"));
        Timestamp t = rs.getTimestamp("created_at");
        m.setCreatedAt(t == null ? null : t.toLocalDateTime());
        return m;
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

