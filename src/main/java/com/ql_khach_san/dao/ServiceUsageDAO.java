package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.ServiceUsage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceUsageDAO {

    public List<ServiceUsage> getByCheckinId(int checkinId) {
        List<ServiceUsage> list = new ArrayList<>();
        String sql = "SELECT usage_id, checkin_id, service_id, quantity, created_at FROM service_usage WHERE checkin_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, checkinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime created = rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null;
                    list.add(new ServiceUsage(rs.getInt("usage_id"), rs.getInt("checkin_id"), rs.getInt("service_id"), rs.getInt("quantity"), created));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ServiceUsage su) {
        String sql = "INSERT INTO service_usage(checkin_id, service_id, quantity, created_at) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, su.getCheckinId());
            ps.setInt(2, su.getServiceId());
            ps.setInt(3, su.getQuantity());
            if (su.getCreatedAt() != null) ps.setTimestamp(4, Timestamp.valueOf(su.getCreatedAt())); else ps.setTimestamp(4, null);
            int affected = ps.executeUpdate();
            if (affected > 0) { try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) su.setUsageId(keys.getInt(1)); } return true; }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int usageId) {
        String sql = "DELETE FROM service_usage WHERE usage_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usageId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isServiceUsed(int serviceId) {
        String sql = "SELECT COUNT(*) FROM service_usage WHERE service_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}