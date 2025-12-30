package com.ql_khach_san.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/db_ql_khach_san?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123";
    
    private static Connection connection;
    
    // Hàm Connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy MySQL Driver");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối Database");
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
