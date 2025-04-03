package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    public static final JSONObject jsonConfig;

    private static final String SSH_URL;
    private static final int SSH_PORT;
    private static final String SSH_USER;
    private static final String SSH_KEY_FILE;

    private static final String DATABASE_URL;
    private static final int DATABASE_PORT;
    private static final String DATABASE_USER;
    private static final String DATABASE_PASSWORD;
    private static final String DATABASE_NAME;

    public static final int LOCAL_PORT = 3307;

    public static Session session = null;
    public static HikariDataSource dataSource = null;

    static {
        try {
            String content = new String(Files.readAllBytes(Paths.get("Database/login.json")));
            jsonConfig = new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SSH_URL = jsonConfig.getString("SSH_URL");
        SSH_PORT = jsonConfig.getInt("SSH_PORT");
        SSH_USER = jsonConfig.getString("SSH_USER");
        SSH_KEY_FILE = jsonConfig.getString("SSH_KEY_FILE");

        DATABASE_URL = jsonConfig.getString("DATABASE_URL");
        DATABASE_PORT = jsonConfig.getInt("DATABASE_PORT");
        DATABASE_USER = jsonConfig.getString("DATABASE_USER");
        DATABASE_PASSWORD = jsonConfig.getString("DATABASE_PASSWORD");
        DATABASE_NAME = jsonConfig.getString("DATABASE_NAME");
    }

    public static void connect_to_database(boolean ssh) {
        String jdbcUrl;
        if (ssh) {
            try {
                JSch jsch = new JSch();
                jsch.addIdentity(SSH_KEY_FILE);
                session = jsch.getSession(SSH_USER, SSH_URL, SSH_PORT);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                session.setPortForwardingL(LOCAL_PORT, DATABASE_URL, DATABASE_PORT);
                System.out.println("SSH Tunnel established...");
            } catch (JSchException e) {
                throw new RuntimeException(e);
            }
            jdbcUrl = "jdbc:mysql://localhost:" + LOCAL_PORT + "/" + DATABASE_NAME;
        } else {
            jdbcUrl = "jdbc:mysql://" + DATABASE_URL + DATABASE_PORT;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(DATABASE_USER);
        config.setPassword(DATABASE_PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000); // 30 second idle timeout
        config.setMaxLifetime(1800000); // 30 minute max connection lifetime
        dataSource = new HikariDataSource(config);
        System.out.println("Connection pool established.");

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConnection::close_resources));
    }

    public static Connection get_connection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close_resources() {
        System.out.println("Clearing resources");
        if (dataSource != null) dataSource.close();
        if (session != null) session.disconnect();
    }
}
