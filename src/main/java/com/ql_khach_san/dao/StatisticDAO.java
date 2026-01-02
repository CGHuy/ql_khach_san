package com.ql_khach_san.dao;

import com.ql_khach_san.model.Statistic;
import com.ql_khach_san.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {
    @Deprecated
    public boolean insertStatistic(Statistic statistic) {
        throw new UnsupportedOperationException("Manual statistic persistence is disabled. Use live computed data instead.");
    }

    @Deprecated
    public boolean updateStatistic(Statistic statistic) {
        throw new UnsupportedOperationException("Manual statistic persistence is disabled. Use live computed data instead.");
    }

    @Deprecated
    public boolean deleteStatistic(int statisticId) {
        throw new UnsupportedOperationException("Manual statistic deletion is disabled. Use live computed data instead.");
    }

    @Deprecated
    public Statistic getStatisticById(int statisticId) {
        throw new UnsupportedOperationException("Fetching saved statistics by ID is deprecated in live-only mode.");
    }

    @Deprecated
    public Statistic getStatisticByDateAndPeriod(java.sql.Date date, String period) {
        throw new UnsupportedOperationException("Fetching saved statistics is deprecated in live-only mode.");
    }

    @Deprecated
    public boolean existsStatistic(java.sql.Date date, String period) {
        throw new UnsupportedOperationException("Checking existence in saved statistics is deprecated in live-only mode.");
    }

    public List<Statistic> getAllStatistics() {
        List<Statistic> list = new ArrayList<>();
        String sql = "SELECT * FROM statistic ORDER BY stat_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractStatistic(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Statistic> getStatisticsByPeriod(String period) {
        List<Statistic> list = new ArrayList<>();
        String sql = "SELECT * FROM statistic WHERE stat_period = ? ORDER BY stat_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, period);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractStatistic(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get revenue grouped by day for a given month
     * returns list of arrays ["yyyy-MM-dd", revenue]
     */
    public List<String[]> getDailyRevenueForMonth(int year, int month) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT DATE(created_at) as d, SUM(total_amount) as revenue FROM invoice WHERE YEAR(created_at)=? AND MONTH(created_at)=? GROUP BY DATE(created_at) ORDER BY DATE(created_at)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[] { rs.getDate("d").toString(), String.valueOf(rs.getDouble("revenue")) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get revenue grouped by month for a given year
     * returns list of arrays ["yyyy-MM", revenue]
     */
    public List<String[]> getMonthlyRevenueForYear(int year) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT YEAR(created_at) as y, MONTH(created_at) as m, SUM(total_amount) as revenue FROM invoice WHERE YEAR(created_at)=? GROUP BY YEAR(created_at), MONTH(created_at) ORDER BY MONTH(created_at)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String label = String.format("%04d-%02d", rs.getInt("y"), rs.getInt("m"));
                list.add(new String[] { label, String.valueOf(rs.getDouble("revenue")) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get nearest N dates with revenue around target date (by proximity), returns list of ["yyyy-MM-dd", revenue]
     */
    public List<String[]> getNearestDaysRevenue(java.sql.Date targetDate, int n) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT d, revenue FROM (SELECT DATE(created_at) d, SUM(total_amount) revenue FROM invoice GROUP BY DATE(created_at)) t ORDER BY ABS(DATEDIFF(d, ?)) LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, targetDate);
            ps.setInt(2, n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[] { rs.getDate("d").toString(), String.valueOf(rs.getDouble("revenue")) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // sort by date ascending
        list.sort((a,b) -> java.sql.Date.valueOf(a[0]).compareTo(java.sql.Date.valueOf(b[0])));
        return list;
    }

    private Statistic extractStatistic(ResultSet rs) throws SQLException {
        Statistic s = new Statistic();
        s.setStatisticId(rs.getInt("statistic_id"));
        s.setStatDate(rs.getDate("stat_date"));
        s.setStatPeriod(rs.getString("stat_period"));
        s.setRevenue(rs.getDouble("revenue"));
        s.setRoomRevenue(rs.getDouble("room_revenue"));
        s.setServiceRevenue(rs.getDouble("service_revenue"));
        s.setCustomerCount(rs.getInt("customer_count"));
        s.setRoomRentedCount(rs.getInt("room_rented_count"));
        s.setServiceCount(rs.getInt("service_count"));
        s.setNote(rs.getString("note"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
