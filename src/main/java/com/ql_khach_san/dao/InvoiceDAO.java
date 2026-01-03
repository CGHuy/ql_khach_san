package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public Invoice getById(int id) {
        String sql = "SELECT invoice_id, checkin_id, employee_id, room_fee, service_fee, total_amount, created_at FROM invoice WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Invoice(rs.getInt("invoice_id"), rs.getInt("checkin_id"), rs.getInt("employee_id"), rs.getDouble("room_fee"), rs.getDouble("service_fee"), rs.getDouble("total_amount"), rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Invoice getByCheckinId(int checkinId) {
        String sql = "SELECT invoice_id, checkin_id, employee_id, room_fee, service_fee, total_amount, created_at FROM invoice WHERE checkin_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, checkinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Invoice(rs.getInt("invoice_id"), rs.getInt("checkin_id"), rs.getInt("employee_id"), rs.getDouble("room_fee"), rs.getDouble("service_fee"), rs.getDouble("total_amount"), rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Invoice inv) {
        String sql = "INSERT INTO invoice(checkin_id, employee_id, room_fee, service_fee, total_amount, created_at) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, inv.getCheckinId());
            ps.setInt(2, inv.getEmployeeId());
            ps.setDouble(3, inv.getRoomFee());
            ps.setDouble(4, inv.getServiceFee());
            ps.setDouble(5, inv.getTotalAmount());
            if (inv.getCreatedAt() != null) ps.setTimestamp(6, Timestamp.valueOf(inv.getCreatedAt())); else ps.setTimestamp(6, null);
            int affected = ps.executeUpdate();
            if (affected > 0) { try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) inv.setInvoiceId(keys.getInt(1)); } return true; }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Invoice> getAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT invoice_id, checkin_id, employee_id, room_fee, service_fee, total_amount, created_at FROM invoice";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Invoice(rs.getInt("invoice_id"), rs.getInt("checkin_id"), rs.getInt("employee_id"), rs.getDouble("room_fee"), rs.getDouble("service_fee"), rs.getDouble("total_amount"), rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM invoice WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
