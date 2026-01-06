package id.ac.unpas.Dao;

import id.ac.unpas.Model.MataPraktikum;
import id.ac.unpas.Utils.KoneksiDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MataPraktikumDao {

    private static final String TABLE = "mata_praktikum";

    public List<MataPraktikum> findAll() throws SQLException {
        String sql = "SELECT id, kode, nama FROM " + TABLE + " ORDER BY id DESC";
        List<MataPraktikum> result = new ArrayList<>();
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }
        return result;
    }

    public MataPraktikum findById(long id) throws SQLException {
        String sql = "SELECT id, kode, nama FROM " + TABLE + " WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public long insert(MataPraktikum mp) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (kode, nama) VALUES (?, ?)";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mp.getKode());
            ps.setString(2, mp.getNama());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert mata_praktikum gagal: tidak ada generated key");
    }

    public void update(MataPraktikum mp) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET kode = ?, nama = ? WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mp.getKode());
            ps.setString(2, mp.getNama());
            ps.setLong(3, mp.getId());
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

    private static MataPraktikum map(ResultSet rs) throws SQLException {
        return new MataPraktikum(
                rs.getLong("id"),
                rs.getString("kode"),
                rs.getString("nama"));
    }
}