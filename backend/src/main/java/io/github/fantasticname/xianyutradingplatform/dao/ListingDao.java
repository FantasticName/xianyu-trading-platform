package io.github.fantasticname.xianyutradingplatform.dao;

import io.github.fantasticname.xianyutradingplatform.model.Listing;
import io.github.fantasticname.xianyutradingplatform.model.ListingCondition;
import io.github.fantasticname.xianyutradingplatform.model.ListingStatus;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
public class ListingDao {
    private final DataSource ds;

    public ListingDao(DataSource ds) {
        this.ds = ds;
    }

    public void insert(Listing l) {
        String sql = "INSERT INTO listings(id, seller_id, title, category, price, `condition`, description, cover_url, image_urls_json, status, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getId());
            ps.setString(2, l.getSellerId());
            ps.setString(3, l.getTitle());
            ps.setString(4, l.getCategory());
            ps.setBigDecimal(5, l.getPrice());
            ps.setString(6, l.getCondition().name());
            ps.setString(7, l.getDescription());
            ps.setString(8, l.getCoverUrl());
            ps.setString(9, l.getImageUrlsJson());
            ps.setString(10, l.getStatus().name());
            ps.setTimestamp(11, Timestamp.valueOf(l.getCreatedAt()));
            ps.setTimestamp(12, Timestamp.valueOf(l.getUpdatedAt()));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public Listing findById(String id) {
        String sql = "SELECT id, seller_id, title, category, price, `condition`, description, cover_url, image_urls_json, status, created_at, updated_at FROM listings WHERE id = ?";
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

    public void update(Listing l) {
        String sql = "UPDATE listings SET title=?, category=?, price=?, `condition`=?, description=?, cover_url=?, image_urls_json=?, status=?, updated_at=NOW() WHERE id=?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getTitle());
            ps.setString(2, l.getCategory());
            ps.setBigDecimal(3, l.getPrice());
            ps.setString(4, l.getCondition().name());
            ps.setString(5, l.getDescription());
            ps.setString(6, l.getCoverUrl());
            ps.setString(7, l.getImageUrlsJson());
            ps.setString(8, l.getStatus().name());
            ps.setString(9, l.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            ch.closeIfBorrowed();
        }
    }

    public void deleteById(String id) {
        String sql = "DELETE FROM listings WHERE id = ?";
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

    public List<Listing> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, int offset, int pageSize) {
        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sb.append("SELECT id, seller_id, title, category, price, `condition`, description, cover_url, image_urls_json, status, created_at, updated_at FROM listings WHERE status = 'ACTIVE'");
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (title LIKE ? OR description LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        if (category != null && !category.isBlank()) {
            sb.append(" AND category = ?");
            params.add(category.trim());
        }
        if (minPrice != null) {
            sb.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sb.append(" AND price <= ?");
            params.add(maxPrice);
        }
        sb.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            bind(ps, params);
            List<Listing> out = new ArrayList<>();
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

    public List<Listing> listBySeller(String sellerId, int limit) {
        String sql = "SELECT id, seller_id, title, category, price, `condition`, description, cover_url, image_urls_json, status, created_at, updated_at FROM listings WHERE seller_id=? ORDER BY updated_at DESC LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sellerId);
            ps.setInt(2, limit);
            List<Listing> out = new ArrayList<>();
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

    public List<Listing> recommendRandom(int limit) {
        String sql = "SELECT id, seller_id, title, category, price, `condition`, description, cover_url, image_urls_json, status, created_at, updated_at FROM listings WHERE status='ACTIVE' ORDER BY RAND() LIMIT ?";
        ConnectionHolder ch = borrow();
        Connection conn = ch.conn();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            List<Listing> out = new ArrayList<>();
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

    private void bind(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            Object v = params.get(i);
            int idx = i + 1;
            if (v instanceof String s) {
                ps.setString(idx, s);
            } else if (v instanceof Integer n) {
                ps.setInt(idx, n);
            } else if (v instanceof BigDecimal bd) {
                ps.setBigDecimal(idx, bd);
            } else {
                ps.setObject(idx, v);
            }
        }
    }

    private Listing map(ResultSet rs) throws Exception {
        Listing l = new Listing();
        l.setId(rs.getString("id"));
        l.setSellerId(rs.getString("seller_id"));
        l.setTitle(rs.getString("title"));
        l.setCategory(rs.getString("category"));
        l.setPrice(rs.getBigDecimal("price"));
        l.setCondition(ListingCondition.valueOf(rs.getString("condition")));
        l.setDescription(rs.getString("description"));
        l.setCoverUrl(rs.getString("cover_url"));
        l.setImageUrlsJson(rs.getString("image_urls_json"));
        l.setStatus(ListingStatus.valueOf(rs.getString("status")));
        Timestamp c = rs.getTimestamp("created_at");
        Timestamp up = rs.getTimestamp("updated_at");
        l.setCreatedAt(c == null ? null : c.toLocalDateTime());
        l.setUpdatedAt(up == null ? null : up.toLocalDateTime());
        return l;
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

