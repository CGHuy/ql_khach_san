package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Floor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FloorDAO {

    private String lastError;

    public String getLastError() { return lastError; }

    public List<Floor> getAll() {
        List<Floor> list = new ArrayList<>();
        String sql = "SELECT floor_id, floor_number, description FROM floor ORDER BY floor_number";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Floor f = new Floor(rs.getInt("floor_id"), rs.getInt("floor_number"), rs.getString("description"));
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return list;
    }
}
