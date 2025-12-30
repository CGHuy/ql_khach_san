package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Checkin;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckinDAO {

    public List<Checkin> getAll() {
        List<Checkin> list = new ArrayList<>();
        String sql = "SELECT checkin_id, reservation_id, checkin_time, checkout_time FROM checkin";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDateTime in = rs.getTimestamp("checkin_time").toLocalDateTime();
                LocalDateTime out = rs.getTimestamp("checkout_time") != null ? rs.getTimestamp("checkout_time").toLocalDateTime() : null;
                list.add(new Checkin(rs.getInt("checkin_id"), rs.getInt("reservation_id"), in, out));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Checkin getById(int id) {
        String sql = "SELECT checkin_id, reservation_id, checkin_time, checkout_time FROM checkin WHERE checkin_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime in = rs.getTimestamp("checkin_time").toLocalDateTime();
                    LocalDateTime out = rs.getTimestamp("checkout_time") != null ? rs.getTimestamp("checkout_time").toLocalDateTime() : null;
                    return new Checkin(rs.getInt("checkin_id"), rs.getInt("reservation_id"), in, out);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Checkin getByReservationId(int reservationId) {
        String sql = "SELECT checkin_id, reservation_id, checkin_time, checkout_time FROM checkin WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime in = rs.getTimestamp("checkin_time").toLocalDateTime();
                    LocalDateTime out = rs.getTimestamp("checkout_time") != null ? rs.getTimestamp("checkout_time").toLocalDateTime() : null;
                    return new Checkin(rs.getInt("checkin_id"), rs.getInt("reservation_id"), in, out);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Checkin c) {
        String sql = "INSERT INTO checkin(reservation_id, checkin_time, checkout_time) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getReservationId());
            ps.setTimestamp(2, Timestamp.valueOf(c.getCheckinTime()));
            if (c.getCheckoutTime() != null) ps.setTimestamp(3, Timestamp.valueOf(c.getCheckoutTime())); else ps.setTimestamp(3, null);
            int affected = ps.executeUpdate();
            if (affected > 0) { try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) c.setCheckinId(keys.getInt(1)); } return true; }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkout(int checkinId, LocalDateTime checkoutTime) {
        String sql = "UPDATE checkin SET checkout_time = ? WHERE checkin_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(checkoutTime));
            ps.setInt(2, checkinId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM checkin WHERE checkin_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
