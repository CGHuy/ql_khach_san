package com.ql_khach_san.service;

import com.ql_khach_san.dao.StatisticDAO;
import com.ql_khach_san.model.Statistic;

import java.util.List;

/**
 * Statistic business logic moved to service package
 */
public class StatisticService {
    private StatisticDAO statisticDAO;

    public StatisticService() {
        this.statisticDAO = new StatisticDAO();
    }

    public boolean addStatistic(Statistic statistic) {
        return statisticDAO.insertStatistic(statistic);
    }

    public boolean updateStatistic(Statistic statistic) {
        return statisticDAO.updateStatistic(statistic);
    }

    public boolean deleteStatistic(int statisticId) {
        return statisticDAO.deleteStatistic(statisticId);
    }

    public Statistic getStatisticById(int statisticId) {
        return statisticDAO.getStatisticById(statisticId);
    }

    public List<Statistic> getAllStatistics() {
        return statisticDAO.getAllStatistics();
    }

    public Statistic generateStatisticByDate(java.sql.Date date) {
        Statistic statistic = new Statistic();
        statistic.setStatDate(new java.util.Date(date.getTime()));
        statistic.setStatPeriod("day");
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection()) {
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE DATE(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE DATE(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            String sqlRoom = "SELECT COUNT(DISTINCT room_id) AS room_rented_count FROM checkin WHERE DATE(checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE DATE(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setDate(1, date);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statistic;
    }

    public Statistic generateStatisticByMonth(int year, int month) {
        Statistic statistic = new Statistic();
        statistic.setStatPeriod("month");
        statistic.setStatDate(java.sql.Date.valueOf(String.format("%04d-%02d-01", year, month)));
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection()) {
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE YEAR(ci.checkin_time) = ? AND MONTH(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            String sqlRoom = "SELECT COUNT(DISTINCT room_id) AS room_rented_count FROM checkin WHERE YEAR(checkin_time) = ? AND MONTH(checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statistic;
    }

    public Statistic generateStatisticByYear(int year) {
        Statistic statistic = new Statistic();
        statistic.setStatPeriod("year");
        statistic.setStatDate(java.sql.Date.valueOf(String.format("%04d-01-01", year)));
        try (java.sql.Connection conn = com.ql_khach_san.config.DBConnection.getConnection()) {
            String sqlRevenue = "SELECT SUM(room_fee) AS room_revenue, SUM(service_fee) AS service_revenue, SUM(total_amount) AS total_revenue FROM invoice WHERE YEAR(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRevenue)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRevenue(rs.getDouble("room_revenue"));
                        statistic.setServiceRevenue(rs.getDouble("service_revenue"));
                        statistic.setRevenue(rs.getDouble("total_revenue"));
                    }
                }
            }
            String sqlCustomer = "SELECT COUNT(DISTINCT c.customer_id) AS customer_count FROM checkin ci JOIN reservation r ON ci.reservation_id = r.reservation_id JOIN customer c ON r.customer_id = c.customer_id WHERE YEAR(ci.checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCustomer)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setCustomerCount(rs.getInt("customer_count"));
                    }
                }
            }
            String sqlRoom = "SELECT COUNT(DISTINCT room_id) AS room_rented_count FROM checkin WHERE YEAR(checkin_time) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setRoomRentedCount(rs.getInt("room_rented_count"));
                    }
                }
            }
            String sqlService = "SELECT SUM(quantity) AS service_count FROM service_usage WHERE YEAR(created_at) = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlService)) {
                ps.setInt(1, year);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        statistic.setServiceCount(rs.getInt("service_count"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statistic;
    }
}