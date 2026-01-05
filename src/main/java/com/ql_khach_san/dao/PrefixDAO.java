package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrefixDAO {

    private String lastError;

    public String getLastError() { return lastError; }

    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT prefix_value FROM prefix ORDER BY prefix_value";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return list;
    }
}
