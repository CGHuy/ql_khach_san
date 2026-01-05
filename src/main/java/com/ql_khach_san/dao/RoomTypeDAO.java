package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.RoomType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    private String lastError;

    public String getLastError() { return lastError; }


    public List<RoomType> getAll() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT type_id, type_name, price, description FROM room_type";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RoomType rt = new RoomType(rs.getInt("type_id"), rs.getString("type_name"), rs.getDouble("price"), rs.getString("description"));
                list.add(rt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return list;
    }

    public RoomType getById(int id) {
        String sql = "SELECT type_id, type_name, price, description FROM room_type WHERE type_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RoomType(rs.getInt("type_id"), rs.getString("type_name"), rs.getDouble("price"), rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return null;
    }

    public boolean insert(RoomType rt) {
        String sql = "INSERT INTO room_type(type_name, price, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rt.getTypeName());
            ps.setDouble(2, rt.getPrice());
            ps.setString(3, rt.getDescription());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        rt.setTypeId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    public boolean update(RoomType rt) {
        String sql = "UPDATE room_type SET type_name = ?, price = ?, description = ? WHERE type_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rt.getTypeName());
            ps.setDouble(2, rt.getPrice());
            ps.setString(3, rt.getDescription());
            ps.setInt(4, rt.getTypeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM room_type WHERE type_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByTypeName(String typeName) {
        String sql = "SELECT 1 FROM room_type WHERE type_name = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    public boolean existsByTypeNameExcludingId(String typeName, int excludeId) {
        String sql = "SELECT 1 FROM room_type WHERE type_name = ? AND type_id <> ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeName);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    // ===== Management UI Methods (với suffix _forMgmt) =====
    public List<RoomType> getAll_forMgmt() { return getAll(); }
    public RoomType getById_forMgmt(int id) { return getById(id); }
    public boolean insert_forMgmt(RoomType rt) { return insert(rt); }
    public boolean update_forMgmt(RoomType rt) { return update(rt); }
    public boolean delete_forMgmt(int id) { return delete(id); }
    public boolean existsByTypeName_forMgmt(String typeName) { return existsByTypeName(typeName); }
    public boolean existsByTypeNameExcludingId_forMgmt(String typeName, int excludeId) { return existsByTypeNameExcludingId(typeName, excludeId); }
}
