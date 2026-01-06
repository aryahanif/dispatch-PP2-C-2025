package id.ac.unpas.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbInit {

    private DbInit() {
    }

    /**
     * Creates required tables if they don't exist yet.
     * This is safe to call multiple times.
     */
    public static void init() throws SQLException {
        try (Connection c = KoneksiDB.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS mata_praktikum (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "kode VARCHAR(32) NOT NULL, " +
                    "nama VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE KEY uk_mata_praktikum_kode (kode)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS asisten (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "nim VARCHAR(32) NOT NULL, " +
                    "nama VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE KEY uk_asisten_nim (nim)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS ruangan (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "kode VARCHAR(32) NOT NULL, " +
                    "nama VARCHAR(255) NOT NULL, " +
                    "kapasitas INT NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE KEY uk_ruangan_kode (kode)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            String createJadwalWithFk = "CREATE TABLE IF NOT EXISTS jadwal (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "mata_praktikum_id BIGINT NOT NULL, " +
                    "asisten_id BIGINT NOT NULL, " +
                    "ruangan_id BIGINT NOT NULL, " +
                    "hari VARCHAR(16) NOT NULL, " +
                    "jam_mulai TIME NOT NULL, " +
                    "jam_selesai TIME NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "KEY idx_jadwal_ruangan_hari (ruangan_id, hari), " +
                    "KEY idx_jadwal_asisten_hari (asisten_id, hari), " +
                    "CONSTRAINT fk_jadwal_mata_praktikum FOREIGN KEY (mata_praktikum_id) REFERENCES mata_praktikum(id) ON UPDATE CASCADE, "
                    +
                    "CONSTRAINT fk_jadwal_asisten FOREIGN KEY (asisten_id) REFERENCES asisten(id) ON UPDATE CASCADE, " +
                    "CONSTRAINT fk_jadwal_ruangan FOREIGN KEY (ruangan_id) REFERENCES ruangan(id) ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            String createJadwalNoFk = "CREATE TABLE IF NOT EXISTS jadwal (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, " +
                    "mata_praktikum_id BIGINT NOT NULL, " +
                    "asisten_id BIGINT NOT NULL, " +
                    "ruangan_id BIGINT NOT NULL, " +
                    "hari VARCHAR(16) NOT NULL, " +
                    "jam_mulai TIME NOT NULL, " +
                    "jam_selesai TIME NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "KEY idx_jadwal_ruangan_hari (ruangan_id, hari), " +
                    "KEY idx_jadwal_asisten_hari (asisten_id, hari)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

            try {
                st.executeUpdate(createJadwalWithFk);
            } catch (SQLException fkError) {
                // If existing master tables use different id types, FK creation can fail.
                // Fall back to a no-FK jadwal table so the app can still run.
                st.executeUpdate(createJadwalNoFk);
            }
        }
    }
}
