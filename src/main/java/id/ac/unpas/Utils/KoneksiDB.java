package id.ac.unpas.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiDB {
	private static final String DEFAULT_DB_NAME = "dispatch";

	private static final String DEFAULT_URL =
			"jdbc:mysql://localhost:3306/" + DEFAULT_DB_NAME
					+ "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

	private static final String DEFAULT_USER = "root";
	private static final String DEFAULT_PASS = "";

	private KoneksiDB() {
		// utility
	}

	public static Connection getConnection() throws SQLException {
		String url = envOrDefault("DB_URL", DEFAULT_URL);
		String user = envOrDefault("DB_USER", DEFAULT_USER);
		String pass = envOrDefault("DB_PASS", DEFAULT_PASS);

		return DriverManager.getConnection(url, user, pass);
	}

	private static String envOrDefault(String key, String defaultValue) {
		String value = System.getenv(key);
		return (value == null || value.isBlank()) ? defaultValue : value.trim();
	}
}
