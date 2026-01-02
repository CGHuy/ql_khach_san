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

    public List<Statistic> getStatisticsByPeriod(String period) {
        return statisticDAO.getStatisticsByPeriod(period);
    }

    public java.util.List<String[]> getDailyRevenueForMonth(int year, int month) {
        return statisticDAO.getDailyRevenueForMonth(year, month);
    }

    public java.util.List<String[]> getMonthlyRevenueForYear(int year) {
        return statisticDAO.getMonthlyRevenueForYear(year);
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

    // Check existence by date/period
    public boolean existsStatistic(java.sql.Date date, String period) {
        return statisticDAO.existsStatistic(date, period);
    }

    /**
     * Save statistic with optional overwrite if exists
     * @param stat statistic to save
     * @param overwrite if true, update existing record
     * @return true if inserted/updated
     */
    public boolean saveStatistic(Statistic stat, boolean overwrite) {
        // Convert stat_date to sql.Date if necessary
        java.sql.Date sqlDate = new java.sql.Date(stat.getStatDate().getTime());
        String period = stat.getStatPeriod();
        if (existsStatistic(sqlDate, period)) {
            if (!overwrite) return false;
            Statistic existing = statisticDAO.getStatisticByDateAndPeriod(sqlDate, period);
            if (existing != null) {
                stat.setStatisticId(existing.getStatisticId());
                return statisticDAO.updateStatistic(stat);
            }
            return false;
        } else {
            return statisticDAO.insertStatistic(stat);
        }
    }

    /**
     * Get nearest N days with revenue around a target date (for comparison charts)
     */
    public java.util.List<String[]> getNearestDaysRevenue(java.sql.Date targetDate, int n) {
        return statisticDAO.getNearestDaysRevenue(targetDate, n);
    }

    /**
     * Compute daily statistics for a range ending at endDate for 'days' days (inclusive)
     */
    public java.util.List<Statistic> computeDailyStats(java.sql.Date endDate, int days) {
        java.util.List<Statistic> list = new java.util.ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(endDate);
        // iterate backward to produce ascending-order list
        for (int i = days - 1; i >= 0; i--) {
            java.util.Calendar c = (java.util.Calendar) cal.clone();
            c.add(java.util.Calendar.DATE, -i);
            java.sql.Date d = new java.sql.Date(c.getTimeInMillis());
            Statistic s = generateStatisticByDate(d);
            if (s != null) {
                // ensure statDate set properly
                s.setStatDate(new java.util.Date(d.getTime()));
                list.add(s);
            }
        }
        return list;
    }
}