package com.ql_khach_san.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    
    // Read DB config from environment variables (allow overrides) for easier local setup.
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/db_ql_khach_san?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "123";

    private static Connection connection;

    // Hàm Connection
    public static Connection getConnection() {
        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        String password = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    System.err.println("MySQL JDBC driver not found. Add mysql-connector-java to pom.xml or classpath.");
                    throw ex;
                }
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối Database -> URL=" + url + " user=" + user + " (password hidden)");
            e.printStackTrace();
        }
        return connection;
    }
    
    
    // Hàm đóng Connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
