package id.ac.unpas.Dao;

import id.ac.unpas.Model.Asisten;
import id.ac.unpas.Utils.KoneksiDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AsistenDao {

    private static final String TABLE = "asisten";

    public List<Asisten> findAll() throws SQLException {
        String sql = "SELECT id, nim, nama FROM " + TABLE + " ORDER BY id DESC";
        List<Asisten> result = new ArrayList<>();
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }
        return result;
    }

    public Asisten findById(long id) throws SQLException {
        String sql = "SELECT id, nim, nama FROM " + TABLE + " WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public long insert(Asisten a) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (nim, nama) VALUES (?, ?)";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNim());
            ps.setString(2, a.getNama());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert asisten gagal: tidak ada generated key");
    }

    public void update(Asisten a) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET nim = ?, nama = ? WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getNim());
            ps.setString(2, a.getNama());
            ps.setLong(3, a.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private static Asisten map(ResultSet rs) throws SQLException {
        return new Asisten(
                rs.getLong("id"),
                rs.getString("nim"),
                rs.getString("nama"));
    }
}