package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> getAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT reservation_id, customer_id, room_id, booking_date, checkin_date, checkout_date, status FROM reservation";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDateTime booking = rs.getTimestamp("booking_date") != null ? rs.getTimestamp("booking_date").toLocalDateTime() : null;
                LocalDateTime checkin = rs.getTimestamp("checkin_date") != null ? rs.getTimestamp("checkin_date").toLocalDateTime() : null;
                LocalDateTime checkout = rs.getTimestamp("checkout_date") != null ? rs.getTimestamp("checkout_date").toLocalDateTime() : null;
                list.add(new Reservation(rs.getInt("reservation_id"), rs.getInt("customer_id"), rs.getInt("room_id"), booking, checkin, checkout, rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Reservation getById(int id) {
        String sql = "SELECT reservation_id, customer_id, room_id, booking_date, checkin_date, checkout_date, status FROM reservation WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime booking = rs.getTimestamp("booking_date") != null ? rs.getTimestamp("booking_date").toLocalDateTime() : null;
                    LocalDateTime checkin = rs.getTimestamp("checkin_date") != null ? rs.getTimestamp("checkin_date").toLocalDateTime() : null;
                    LocalDateTime checkout = rs.getTimestamp("checkout_date") != null ? rs.getTimestamp("checkout_date").toLocalDateTime() : null;
                    return new Reservation(rs.getInt("reservation_id"), rs.getInt("customer_id"), rs.getInt("room_id"), booking, checkin, checkout, rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Reservation r) {
        String sql = "INSERT INTO reservation(customer_id, room_id, booking_date, checkin_date, checkout_date, status) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getCustomerId());
            ps.setInt(2, r.getRoomId());
            if (r.getBookingDate() != null) ps.setTimestamp(3, Timestamp.valueOf(r.getBookingDate())); else ps.setTimestamp(3, null);
            if (r.getCheckinDate() != null) ps.setTimestamp(4, Timestamp.valueOf(r.getCheckinDate())); else ps.setTimestamp(4, null);
            if (r.getCheckoutDate() != null) ps.setTimestamp(5, Timestamp.valueOf(r.getCheckoutDate())); else ps.setTimestamp(5, null);
            ps.setString(6, r.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) { try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) r.setReservationId(keys.getInt(1)); } return true; }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservation SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
