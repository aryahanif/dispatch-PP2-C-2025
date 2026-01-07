package id.ac.unpas.Dao;

import id.ac.unpas.Model.Hari;
import id.ac.unpas.Model.Jadwal;
import id.ac.unpas.Utils.KoneksiDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class JadwalDao {

    private static final String TABLE = "jadwal";

    public List<Jadwal> findAll() throws SQLException {
        String sql = "SELECT id, mata_praktikum_id, asisten_id, ruangan_id, hari, jam_mulai, jam_selesai FROM " + TABLE
                + " ORDER BY id DESC";
        List<Jadwal> result = new ArrayList<>();
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }
        return result;
    }

    public Jadwal findById(long id) throws SQLException {
        String sql = "SELECT id, mata_praktikum_id, asisten_id, ruangan_id, hari, jam_mulai, jam_selesai FROM " + TABLE
                + " WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public long insert(Jadwal j) throws SQLException {
        String sql = "INSERT INTO " + TABLE
                + " (mata_praktikum_id, asisten_id, ruangan_id, hari, jam_mulai, jam_selesai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, j.getMataPraktikumId());
            ps.setLong(2, j.getAsistenId());
            ps.setLong(3, j.getRuanganId());
            ps.setString(4, j.getHari().name());
            ps.setTime(5, Time.valueOf(j.getJamMulai()));
            ps.setTime(6, Time.valueOf(j.getJamSelesai()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert jadwal gagal: tidak ada generated key");
    }

    public void update(Jadwal j) throws SQLException {
        String sql = "UPDATE " + TABLE
                + " SET mata_praktikum_id = ?, asisten_id = ?, ruangan_id = ?, hari = ?, jam_mulai = ?, jam_selesai = ? WHERE id = ?";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, j.getMataPraktikumId());
            ps.setLong(2, j.getAsistenId());
            ps.setLong(3, j.getRuanganId());
            ps.setString(4, j.getHari().name());
            ps.setTime(5, Time.valueOf(j.getJamMulai()));
            ps.setTime(6, Time.valueOf(j.getJamSelesai()));
            ps.setLong(7, j.getId());
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

    public boolean existsBentrokRuangan(long ruanganId, Hari hari, java.time.LocalTime mulai,
            java.time.LocalTime selesai,
            Long excludeId) throws SQLException {
        String sql = "SELECT 1 FROM " + TABLE
                + " WHERE ruangan_id = ? AND hari = ? AND (? < jam_selesai) AND (jam_mulai < ?)";
        if (excludeId != null) {
            sql += " AND id <> ?";
        }
        sql += " LIMIT 1";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, ruanganId);
            ps.setString(i++, hari.name());
            ps.setTime(i++, Time.valueOf(mulai));
            ps.setTime(i++, Time.valueOf(selesai));
            if (excludeId != null) {
                ps.setLong(i++, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsBentrokAsisten(long asistenId, Hari hari, java.time.LocalTime mulai,
            java.time.LocalTime selesai,
            Long excludeId) throws SQLException {
        String sql = "SELECT 1 FROM " + TABLE
                + " WHERE asisten_id = ? AND hari = ? AND (? < jam_selesai) AND (jam_mulai < ?)";
        if (excludeId != null) {
            sql += " AND id <> ?";
        }
        sql += " LIMIT 1";
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, asistenId);
            ps.setString(i++, hari.name());
            ps.setTime(i++, Time.valueOf(mulai));
            ps.setTime(i++, Time.valueOf(selesai));
            if (excludeId != null) {
                ps.setLong(i++, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Returns rows suitable for JadwalView JTable:
     * [id, mata_praktikum, asisten, ruangan, hari, mulai(HH:mm), selesai(HH:mm)]
     */
    public List<Object[]> findAllForTable() throws SQLException {
        return findForTable(null, null);
    }

    /**
     * Returns rows suitable for JadwalView JTable with optional filters.
     *
     * @param asistenId optional; when provided, only rows with that asisten
     * @param ruanganId optional; when provided, only rows with that ruangan
     */
    public List<Object[]> findForTable(Long asistenId, Long ruanganId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT j.id, ")
                .append("CONCAT(mp.kode, ' - ', mp.nama) AS mp_label, ")
                .append("CONCAT(a.nim, ' - ', a.nama) AS asisten_label, ")
                .append("CONCAT(r.kode, ' - ', r.nama) AS ruangan_label, ")
                .append("j.hari, ")
                .append("DATE_FORMAT(j.jam_mulai, '%H:%i') AS mulai, ")
                .append("DATE_FORMAT(j.jam_selesai, '%H:%i') AS selesai ")
                .append("FROM ").append(TABLE).append(" j ")
                .append("JOIN mata_praktikum mp ON mp.id = j.mata_praktikum_id ")
                .append("JOIN asisten a ON a.id = j.asisten_id ")
                .append("JOIN ruangan r ON r.id = j.ruangan_id ")
                .append("WHERE 1=1 ");

        if (asistenId != null) {
            sql.append("AND j.asisten_id = ? ");
        }
        if (ruanganId != null) {
            sql.append("AND j.ruangan_id = ? ");
        }

        sql.append("ORDER BY j.id DESC");

        List<Object[]> rows = new ArrayList<>();
        try (Connection c = KoneksiDB.getConnection();
                PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            if (asistenId != null) {
                ps.setLong(i++, asistenId);
            }
            if (ruanganId != null) {
                ps.setLong(i++, ruanganId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[] {
                            rs.getLong("id"),
                            rs.getString("mp_label"),
                            rs.getString("asisten_label"),
                            rs.getString("ruangan_label"),
                            rs.getString("hari"),
                            rs.getString("mulai"),
                            rs.getString("selesai")
                    });
                }
            }
        }
        return rows;
    }

    private static Jadwal map(ResultSet rs) throws SQLException {
        String hariRaw = rs.getString("hari");
        Hari hari = (hariRaw == null) ? null : Hari.valueOf(hariRaw);
        Time mulai = rs.getTime("jam_mulai");
        Time selesai = rs.getTime("jam_selesai");
        return new Jadwal(
                rs.getLong("id"),
                rs.getLong("mata_praktikum_id"),
                rs.getLong("asisten_id"),
                rs.getLong("ruangan_id"),
                hari,
                (mulai == null) ? null : mulai.toLocalTime(),
                (selesai == null) ? null : selesai.toLocalTime());
    }
}