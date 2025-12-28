package com.ql_khach_san.config;

import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("Ket noi DB thanh cong");
        } else {
            System.out.println("Ket noi DB that bai");
        }
    }
}
