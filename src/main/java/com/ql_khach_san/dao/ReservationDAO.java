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
    
    public boolean createReservationTransaction(Reservation res) {
        Connection conn = null;
        PreparedStatement psRes = null;
        PreparedStatement psRoom = null;

        try {
            conn = com.ql_khach_san.config.DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch (Transaction)

            // 1. Thêm đơn
            String sqlRes = "INSERT INTO reservation(customer_id, room_id, booking_date, checkin_date, checkout_date, status) VALUES(?, ?, ?, ?, ?, ?)";
            psRes = conn.prepareStatement(sqlRes, Statement.RETURN_GENERATED_KEYS);
            psRes.setInt(1, res.getCustomerId());
            psRes.setInt(2, res.getRoomId());
            psRes.setTimestamp(3, res.getBookingDate() != null ? Timestamp.valueOf(res.getBookingDate()) : Timestamp.valueOf(LocalDateTime.now()));
            psRes.setTimestamp(4, res.getCheckinDate() != null ? Timestamp.valueOf(res.getCheckinDate()) : null);
            psRes.setTimestamp(5, res.getCheckoutDate() != null ? Timestamp.valueOf(res.getCheckoutDate()) : null);
            psRes.setString(6, "Đã đặt");

            int affected = psRes.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = psRes.getGeneratedKeys()) {
                    if (keys.next()) res.setReservationId(keys.getInt(1));
                }
            }

            // 2. Cập nhật trạng thái
            String sqlRoom = "UPDATE room SET status = 'Đã đặt' WHERE room_id = ?";
            psRoom = conn.prepareStatement(sqlRoom);
            psRoom.setInt(1, res.getRoomId());
            psRoom.executeUpdate();

            conn.commit(); // Hoàn tất, lưu mọi thay đổi
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng các tài nguyên
            try {
                if (psRes != null) psRes.close();
                if (psRoom != null) psRoom.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    public List<Reservation> getByCustomerId(int customerId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT reservation_id, customer_id, room_id, booking_date, checkin_date, checkout_date, status FROM reservation WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime booking = rs.getTimestamp("booking_date") != null ? rs.getTimestamp("booking_date").toLocalDateTime() : null;
                    LocalDateTime checkin = rs.getTimestamp("checkin_date") != null ? rs.getTimestamp("checkin_date").toLocalDateTime() : null;
                    LocalDateTime checkout = rs.getTimestamp("checkout_date") != null ? rs.getTimestamp("checkout_date").toLocalDateTime() : null;
                    list.add(new Reservation(rs.getInt("reservation_id"), rs.getInt("customer_id"), rs.getInt("room_id"), booking, checkin, checkout, rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean cancelReservationTransaction(Reservation res) {
        Connection conn = null;
        PreparedStatement psRes = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            String sqlRes = "UPDATE reservation SET status = 'Đã hủy' WHERE reservation_id = ?";
            psRes = conn.prepareStatement(sqlRes);
            psRes.setInt(1, res.getReservationId());
            psRes.executeUpdate();

            conn.commit(); // Lưu thay đổi vào Database
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Quay xe nếu có bất kỳ lỗi nào xảy ra
                    System.out.println("Transaction rolled back!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng tài nguyên thủ công vì không dùng try-with-resources cho Transaction được
            try {
                if (psRes != null) psRes.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean checkInReservationTransaction(Reservation res) {
        Connection conn = null;
        PreparedStatement psRes = null;
        PreparedStatement psCheckin = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Cập nhật trạng thái reservation
            String sqlRes = "UPDATE reservation SET status = 'Đã nhận phòng' WHERE reservation_id = ?";
            psRes = conn.prepareStatement(sqlRes);
            psRes.setInt(1, res.getReservationId());
            psRes.executeUpdate();

            // Tạo bản ghi checkin
            String sqlCheckin = "INSERT INTO checkin(reservation_id, checkin_time, checkout_time) VALUES(?, ?, ?)";
            psCheckin = conn.prepareStatement(sqlCheckin);
            psCheckin.setInt(1, res.getReservationId());
            psCheckin.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            psCheckin.setTimestamp(3, null);
            psCheckin.executeUpdate();

            conn.commit(); // Lưu thay đổi vào Database
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Quay xe nếu có bất kỳ lỗi nào xảy ra
                    System.out.println("Transaction rolled back!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng tài nguyên thủ công vì không dùng try-with-resources cho Transaction được
            try {
                if (psRes != null) psRes.close();
                if (psCheckin != null) psCheckin.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
