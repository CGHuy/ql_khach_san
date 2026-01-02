package com.ql_khach_san.dao;

import com.ql_khach_san.model.Statistic;
import com.ql_khach_san.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {
    public boolean insertStatistic(Statistic statistic) {
        String sql = "INSERT INTO statistic (stat_date, stat_period, revenue, room_revenue, service_revenue, customer_count, room_rented_count, service_count, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(statistic.getStatDate().getTime()));
            ps.setString(2, statistic.getStatPeriod());
            ps.setDouble(3, statistic.getRevenue());
            ps.setDouble(4, statistic.getRoomRevenue());
            ps.setDouble(5, statistic.getServiceRevenue());
            ps.setInt(6, statistic.getCustomerCount());
            ps.setInt(7, statistic.getRoomRentedCount());
            ps.setInt(8, statistic.getServiceCount());
            ps.setString(9, statistic.getNote());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatistic(Statistic statistic) {
        String sql = "UPDATE statistic SET stat_date=?, stat_period=?, revenue=?, room_revenue=?, service_revenue=?, customer_count=?, room_rented_count=?, service_count=?, note=? WHERE statistic_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(statistic.getStatDate().getTime()));
            ps.setString(2, statistic.getStatPeriod());
            ps.setDouble(3, statistic.getRevenue());
            ps.setDouble(4, statistic.getRoomRevenue());
            ps.setDouble(5, statistic.getServiceRevenue());
            ps.setInt(6, statistic.getCustomerCount());
            ps.setInt(7, statistic.getRoomRentedCount());
            ps.setInt(8, statistic.getServiceCount());
            ps.setString(9, statistic.getNote());
            ps.setInt(10, statistic.getStatisticId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStatistic(int statisticId) {
        String sql = "DELETE FROM statistic WHERE statistic_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statisticId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Statistic getStatisticById(int statisticId) {
        String sql = "SELECT * FROM statistic WHERE statistic_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statisticId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractStatistic(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Statistic getStatisticByDateAndPeriod(java.sql.Date date, String period) {
        String sql = "SELECT * FROM statistic WHERE stat_date = ? AND stat_period = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ps.setString(2, period);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractStatistic(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsStatistic(java.sql.Date date, String period) {
        String sql = "SELECT 1 FROM statistic WHERE stat_date = ? AND stat_period = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ps.setString(2, period);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
