package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    private String lastError;

    public String getLastError() { return lastError; }


    public List<Room> getAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT room_id, room_number, type_id, floor_id, status FROM room";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room(rs.getInt("room_id"), rs.getString("room_number"), rs.getInt("type_id"), rs.getInt("floor_id"), rs.getString("status"), null);
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return list;
    }

    public Room getById(int id) {
        String sql = "SELECT room_id, room_number, type_id, floor_id, status FROM room WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Room(rs.getInt("room_id"), rs.getString("room_number"), rs.getInt("type_id"), rs.getInt("floor_id"), rs.getString("status"), null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return null;
    }

    public List<Room> getByType(int typeId) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT room_id, room_number, type_id, floor_id, status FROM room WHERE type_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Room(rs.getInt("room_id"), rs.getString("room_number"), rs.getInt("type_id"), rs.getInt("floor_id"), rs.getString("status"), null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return list;
    }

    public boolean insert(Room r) {
        String sql = "INSERT INTO room(room_number, type_id, floor_id, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getRoomNumber());
            ps.setInt(2, r.getTypeId());
            ps.setInt(3, r.getFloorId());
            ps.setString(4, r.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) r.setRoomId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    public boolean update(Room r) {
        String sql = "UPDATE room SET room_number = ?, type_id = ?, floor_id = ?, status = ? WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setInt(2, r.getTypeId());
            ps.setInt(3, r.getFloorId());
            ps.setString(4, r.getStatus());
            ps.setInt(5, r.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    public boolean updateStatus(int roomId, String status) {
        String sql = "UPDATE room SET status = ? WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByRoomNumber(String roomNumber) {
        String sql = "SELECT 1 FROM room WHERE room_number = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return false;
    }

    public boolean existsByRoomNumberExcludingId(String roomNumber, int excludeId) {
        String sql = "SELECT 1 FROM room WHERE room_number = ? AND room_id <> ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
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

    public List<String> getDistinctStatuses() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT status FROM room";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
            lastError = e.getMessage();
        }
        return list;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM room WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
