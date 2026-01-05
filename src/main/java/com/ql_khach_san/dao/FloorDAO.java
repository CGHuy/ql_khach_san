package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Floor;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FloorDAO {
    
    public List<Floor> getAll() {
        List<Floor> list = new ArrayList<>();
        String sql = "SELECT floor_id, floor_number, description FROM floor";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Floor(rs.getInt("floor_id"), rs.getInt("floor_number"), rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();

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

    public Floor getById(int id) {
        String sql = "SELECT floor_id, floor_number, description FROM floor WHERE floor_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Floor(rs.getInt("floor_id"), rs.getInt("floor_number"), rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Floor f) {
        String sql = "INSERT INTO floor(floor_number, description) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getFloor_number());
            ps.setString(2, f.getDescription());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) f.setFloor_id(keys.getInt(1)); }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Floor f) {
        String sql = "UPDATE floor SET floor_number = ?, description = ? WHERE floor_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getFloor_number());
            ps.setString(2, f.getDescription());
            ps.setInt(3, f.getFloor_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM floor WHERE floor_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // ===== Management UI Methods (với suffix _forMgmt) =====
    public List<Floor> getAll_forMgmt() { return getAll(); }
}
