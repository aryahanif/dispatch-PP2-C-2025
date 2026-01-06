package id.ac.unpas.Dao;

import id.ac.unpas.Model.Ruangan;
import id.ac.unpas.Utils.KoneksiDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RuanganDao {

    private static final String TABLE = "ruangan";

    public List<Ruangan> findAll() throws SQLException {
        String sql = "SELECT id, kode, nama, kapasitas FROM " + TABLE + " ORDER BY id DESC";
        List<Ruangan> result = new ArrayList<>();
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }
        return result;
    }

    public Ruangan findById(long id) throws SQLException {
        String sql = "SELECT id, kode, nama, kapasitas FROM " + TABLE + " WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public long insert(Ruangan r) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (kode, nama, kapasitas) VALUES (?, ?, ?)";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getKode());
            ps.setString(2, r.getNama());
            ps.setInt(3, r.getKapasitas());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert ruangan gagal: tidak ada generated key");
    }

    public void update(Ruangan r) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET kode = ?, nama = ?, kapasitas = ? WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getKode());
            ps.setString(2, r.getNama());
            ps.setInt(3, r.getKapasitas());
            ps.setLong(4, r.getId());
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

    private static Ruangan map(ResultSet rs) throws SQLException {
        return new Ruangan(
                rs.getLong("id"),
                rs.getString("kode"),
                rs.getString("nama"),
                rs.getInt("kapasitas"));
    }
}
